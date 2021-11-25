package it.unibo.ai.didattica.competition.tablut.becchi.solver;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;

import java.util.*;

public class TranspositionTableIterativeDeepeningSolver implements AdversarialSearch<State, Action> {

    public final static String METRICS_NODES_EXPANDED = "nodesExpanded";
    public final static String METRICS_MAX_DEPTH = "maxDepth";

    protected Game<State, Action, State.Turn> game;
    protected double utilMax;
    protected double utilMin;
    protected int currDepthLimit;
    private boolean heuristicEvaluationUsed; // indicates that non-terminal nodes have been evaluated.
    private final Timer timer;
    private boolean logEnabled;

    private Metrics metrics = new Metrics();
    private final Heuristic heuristic;
    private final HashSet<String> visited = new HashSet<>();

    private TranspositionTable transpositionTable;

    /**
     * Creates a new search object for a given game.
     *
     * @param game    The game.
     * @param utilMin Utility value of worst state for this player. Supports
     *                evaluation of non-terminal states and early termination in
     *                situations with a safe winner.
     * @param utilMax Utility value of best state for this player. Supports
     *                evaluation of non-terminal states and early termination in
     *                situations with a safe winner.
     * @param time    Maximal computation time in seconds.
     * @param heuristic the Heuristic used for evaluation of intermediate states.
     */
    public TranspositionTableIterativeDeepeningSolver(Game<State, Action, State.Turn> game, double utilMin, double utilMax,
                                                      int time, Heuristic heuristic) {
        this.game = game;
        this.utilMin = utilMin;
        this.utilMax = utilMax;
        this.timer = new Timer(time);
        this.heuristic = heuristic;
        setLogEnabled(true);
    }

    public void setLogEnabled(boolean b) {
        logEnabled = b;
    }

    /**
     * Template method controlling the search. It is based on iterative
     * deepening and tries to make to a good decision in limited time. Credit
     * goes to Behi Monsio who had the idea of ordering actions by utility in
     * subsequent depth-limited search runs.
     */
    @Override
    public Action makeDecision(State state) {
        metrics = new Metrics();
        this.visited.add(state.toLinearString());
        StringBuffer logText = null;
        State.Turn player = game.getPlayer(state);
        List<Action> results = orderActions(state, game.getActions(state), player, 0);
        timer.start();
        currDepthLimit = 0;
        do {
            clearTranspositionTable();
            incrementDepthLimit();
            if (logEnabled)
                logText = new StringBuffer("depth " + currDepthLimit + ": ");
            heuristicEvaluationUsed = false;
            ActionStore newResults = new ActionStore();
            for (Action action : results) {
                HashSet<String> visited = new HashSet<>(this.visited);
                double value = minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, visited);
                if (timer.timeOutOccurred())
                    break; // exit from action loop
                newResults.add(action, value);
                if (logEnabled) {
                    assert logText != null;
                    logText.append(action).append("->").append(value).append(" ");
                }
            }
            if (logEnabled)
                System.out.println(logText);
            if (newResults.size() > 0) {
                results = newResults.actions;
                if (!timer.timeOutOccurred()) {
                    if (hasSafeWinner(newResults.utilValues.get(0)))
                        break; // exit from iterative deepening loop
                    else if (newResults.size() > 1
                            && isSignificantlyBetter(newResults.utilValues.get(0), newResults.utilValues.get(1)))
                        break; // exit from iterative deepening loop
                }
            }
        } while (!timer.timeOutOccurred() && heuristicEvaluationUsed);
        Action result = results.get(0);
        this.visited.add(game.getResult(state,result).toLinearString());
        return result;
    }

    private void clearTranspositionTable() {
        transpositionTable = new TranspositionTable();
    }

    // returns an utility value
    public double maxValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        updateMetrics(depth);

        String stateString = state.toLinearString();
        if (transpositionTable.hasAlreadyEvaluated(stateString, depth)) {
            return transpositionTable.getValue(stateString);
        }

        if (game.isTerminal(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
            return eval(state, player);
        }
        double value = Double.NEGATIVE_INFINITY;
        for (Action action : orderActions(state, game.getActions(state), player, depth)) {
            State resultingState = game.getResult(state, action);

            if (visited.contains(resultingState.toLinearString())) {
                resultingState.setTurn(State.Turn.DRAW);
            }
            HashSet<String> newVisited = new HashSet<>(visited);
            newVisited.add(resultingState.toLinearString());

            double currentValue = minValue(resultingState, player, alpha, beta, depth + 1, newVisited);
            value = Math.max(value, currentValue);

            transpositionTable.put(resultingState.toLinearString(), currentValue, depth + 1);

            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    // returns an utility value
    public double minValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        updateMetrics(depth);

        String stateString = state.toLinearString();
        if (transpositionTable.hasAlreadyEvaluated(stateString, depth)) {
            return transpositionTable.getValue(stateString);
        }

        if (game.isTerminal(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
            return eval(state, player);
        }
        double value = Double.POSITIVE_INFINITY;
        for (Action action : orderActions(state, game.getActions(state), player, depth)) {
            State resultingState = game.getResult(state, action);

            if (visited.contains(resultingState.toLinearString())) {
                resultingState.setTurn(State.Turn.DRAW);
            }
            HashSet<String> newVisited = new HashSet<>(visited);
            newVisited.add(resultingState.toLinearString());

            double currentValue = maxValue(resultingState, player, alpha, beta, depth + 1, newVisited);
            value = Math.min(value, currentValue);

            transpositionTable.put(resultingState.toLinearString(), currentValue, depth + 1);

            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    private void updateMetrics(int depth) {
        metrics.incrementInt(METRICS_NODES_EXPANDED);
        metrics.set(METRICS_MAX_DEPTH, Math.max(metrics.getInt(METRICS_MAX_DEPTH), depth));
    }

    /**
     * Returns some statistic data from the last search.
     */
    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Primitive operation which is called at the beginning of one depth limited
     * search step. This implementation increments the current depth limit by
     * one.
     */
    protected void incrementDepthLimit() {
        currDepthLimit++;
    }

    /**
     * Primitive operation which is used to stop iterative deepening search in
     * situations where a clear best action exists. This implementation returns
     * true if the new utility has the maximum value.
     */
    protected boolean isSignificantlyBetter(double newUtility, double utility) {
        return newUtility == Double.MAX_VALUE;
    }

    /**
     * Primitive operation which is used to stop iterative deepening search in
     * situations where a safe winner has been identified. This implementation
     * returns true if the given value (for the currently preferred action
     * result) is the highest or lowest utility value possible.
     */
    protected boolean hasSafeWinner(double resultUtility) {
        return resultUtility <= utilMin || resultUtility >= utilMax;
    }

    /**
     * Primitive operation, which estimates the value for (not necessarily
     * terminal) states. This implementation returns the utility value for
     * terminal states and the given heuristic for non-terminal states
     */
    protected double eval(State state, State.Turn player) {
        if (game.isTerminal(state)) {
            return game.getUtility(state, player);
        } else {
            heuristicEvaluationUsed = true;
            return heuristic.getValue(state);
        }
    }

    /**
     * Primitive operation for action ordering. This implementation shuffles the
     * actions randomly.
     */
    public List<Action> orderActions(State state, List<Action> actions, State.Turn player, int depth) {
        Collections.shuffle(actions);
        return actions;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // nested helper classes

    private static class TranspositionTable {
        private final HashMap<String, Double> valueTable = new HashMap<>(); //stores the utility values of the explored states
        private final HashMap<String, Integer> depthTable = new HashMap<>(); //stores the depths at which the states were encountered

        //overwrites any old depth value
        void put(String state, double value, int depth) {
            valueTable.put(state, value);
            depthTable.put(state, depth);
        }

        /*
         * if the node corresponding to the given state is at an equal or greater depth
         * the node with the same state already evaluated before, it is not necessary to
         * re-evaluate it.
         * If the same node is encountered at a shallower depth ds, the subtree expanded
         * from there brings additional information.
         */
        boolean hasAlreadyEvaluated(String state, int newDepth) {
            return valueTable.containsKey(state) && newDepth >= depthTable.get(state);
        }

        double getValue(String state) {
            return valueTable.get(state);
        }

        double getDepth(String state) {
            return depthTable.get(state);
        }
    }

    private static class Timer {
        private final long duration;
        private long startTime;

        Timer(int maxSeconds) {
            this.duration = 1000L * maxSeconds;
        }

        void start() {
            startTime = System.currentTimeMillis();
        }

        boolean timeOutOccurred() {
            return System.currentTimeMillis() > startTime + duration;
        }
    }

    /**
     * Orders actions by utility.
     */
    private static class ActionStore {
        private final List<Action> actions = new ArrayList<>();
        private final List<Double> utilValues = new ArrayList<>();

        void add(Action action, double utilValue) {
            int idx = 0;
            while (idx < actions.size() && utilValue <= utilValues.get(idx))
                idx++;
            actions.add(idx, action);
            utilValues.add(idx, utilValue);
        }

        int size() {
            return actions.size();
        }
    }
}