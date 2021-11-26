package it.unibo.ai.didattica.competition.tablut.becchi.solver;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.framework.Metrics;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
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
    private final TranspositionTable transpositionTable = new TranspositionTable();

    protected int transpositionTableLookups = 0;

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
        double value = Double.NEGATIVE_INFINITY;
        timer.start();
        currDepthLimit = 0;
        int bestActionIndex = 0;
        do {
            incrementDepthLimit();
            if (logEnabled)
                logText = new StringBuffer("depth " + currDepthLimit + ": ");

            if (transpositionTable.hasAlreadyEvaluated(state, currDepthLimit)) {
                if (logEnabled)
                    System.out.println(logText);
                    System.out.println("Decided first action from transposition table");
                continue;
            }
            heuristicEvaluationUsed = false;
            ActionStore newResults = new ActionStore();
            for (Action action : results) {
                HashSet<String> visited = new HashSet<>(this.visited);
                value = minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, visited);
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
                bestActionIndex = newResults.evalDecreasingOrder.get(0);
                value = newResults.utilValues.get(0);
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

        transpositionTable.saveSearchData(state, value, currDepthLimit, bestActionIndex);
        this.visited.add(game.getResult(state,result).toLinearString());

        transpositionTable.printSize();
        System.out.println(transpositionTableLookups);
        transpositionTableLookups = 0;
        return result;
    }

    // returns an utility value
    public double maxValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        updateMetrics(depth);

        if (transpositionTable.hasAlreadyEvaluated(state, currDepthLimit - depth)) {
            return transpositionTable.getValue(state);
        }
        if (game.isTerminal(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
            double value = eval(state, player);
            transpositionTable.saveSearchData(state, value, 0, 0);
            return value;
        }

        double value = Double.NEGATIVE_INFINITY;
        int bestActionIndex = 0;
        int idx = 0;
        for (Action action : orderActions(state, game.getActions(state), player, depth)) {
            State resultingState = game.getResult(state, action);

            if (visited.contains(resultingState.toLinearString())) {
                resultingState.setTurn(State.Turn.DRAW);
            }
            HashSet<String> newVisited = new HashSet<>(visited);
            newVisited.add(resultingState.toLinearString());

            double currentValue = minValue(resultingState, player, alpha, beta, depth + 1, newVisited);

            if (currentValue > value) {
                value = currentValue;
                bestActionIndex = idx;
            }

            if (value >= beta)
                break;
            alpha = Math.max(alpha, value);
            idx++;
        }
        transpositionTable.saveSearchData(state, value, currDepthLimit - depth, bestActionIndex);
        return value;
    }

    // returns an utility value
    public double minValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        updateMetrics(depth);

        if (transpositionTable.hasAlreadyEvaluated(state, currDepthLimit - depth)) {
            return transpositionTable.getValue(state);
        }
        if (game.isTerminal(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
            double value = eval(state, player);
            transpositionTable.saveSearchData(state, value, 0, 0);
            return value;
        }

        double value = Double.POSITIVE_INFINITY;
        int bestActionIndex = 0;
        int idx = 0;
        for (Action action : orderActions(state, game.getActions(state), player, depth)) {
            State resultingState = game.getResult(state, action);

            if (visited.contains(resultingState.toLinearString())) {
                resultingState.setTurn(State.Turn.DRAW);
            }
            HashSet<String> newVisited = new HashSet<>(visited);
            newVisited.add(resultingState.toLinearString());

            double currentValue = maxValue(resultingState, player, alpha, beta, depth + 1, newVisited);

            if (currentValue < value) {
                value = currentValue;
                bestActionIndex = idx;
            }

            if (value <= alpha)
                break;
            beta = Math.min(beta, value);
            idx++;
        }
        transpositionTable.saveSearchData(state, value, currDepthLimit - depth, bestActionIndex);
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
        if (!transpositionTable.hasState(state)) {
            return actions;
        }

        int bestActionIndex = transpositionTable.getBestActionIndex(state);

        Action oldFirst = actions.get(0);

        actions.set(0, actions.get(bestActionIndex));
        actions.set(bestActionIndex, oldFirst);

        return actions;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // nested helper classes

    private class TranspositionTable {
        private final int cacheSize = 1000000;
        private final double fractionKept;
        private final HashMap<String, Information> transpositionTable = new HashMap<>(cacheSize*2); //stores the utility values of the explored states

        public TranspositionTable(double fractionKept) {
            this.fractionKept = fractionKept;
        }
        public TranspositionTable() {
            this(0.8);
        }

        private class Information {
            public float value;
            public byte subtreeDepth = -1;
            public short occurrences = 0;
            public short bestActionIndex = 0;
        }

        private class OccurrenceComparator implements Comparator<String> {
            @Override
            public int compare(String s2, String s1) {
                return transpositionTable.get(s2).occurrences - transpositionTable.get(s1).occurrences;
            }
        }

        private void removeLeastOccurring() {
            if (logEnabled)
                System.out.println("Truncating transposition table");
            List<String> sortedKeys = new ArrayList<>(transpositionTable.keySet());
            sortedKeys.sort(new OccurrenceComparator());

            for (int i=0; i<sortedKeys.size()*(1-fractionKept); i++) {
                transpositionTable.remove(sortedKeys.get(i));
            }

            for(String state: transpositionTable.keySet()) {
                transpositionTable.get(state).occurrences = 1;
            }
        }

        //overwrites any old entry
        void saveSearchData(State state, double value, int subtreeDepth, int bestActionIndex) {
            String stateString = state.toLinearString();

            Information info = transpositionTable.containsKey(stateString) ? transpositionTable.get(stateString) : new Information();

            info.occurrences++;
            if (subtreeDepth > info.subtreeDepth) {
                info.value = value >= Float.MAX_VALUE ? Float.MAX_VALUE : (float) value;
                info.subtreeDepth = (byte) subtreeDepth;
                info.bestActionIndex = (short) bestActionIndex;
                transpositionTable.put(state.toLinearString(), info);
            }

            if (transpositionTable.size() >= cacheSize) {
                removeLeastOccurring();
            }
        }

        boolean hasAlreadyEvaluated(State state, int subtreeDepth) {
            String stateString = state.toLinearString();
            return transpositionTable.containsKey(stateString) && subtreeDepth < transpositionTable.get(stateString).subtreeDepth;
        }

        boolean hasState(State state) {
            return transpositionTable.containsKey(state.toLinearString());
        }

        double getValue(State state) {
            transpositionTableLookups++;
            double value = transpositionTable.get(state.toLinearString()).value;
            return value >= Float.MAX_VALUE ? Double.MAX_VALUE : value;
        }

        int getBestActionIndex(State state) {
            return transpositionTable.get(state.toLinearString()).bestActionIndex;
        }

        void printSize() {
            System.out.println("Size of transposition table: " + transpositionTable.size());
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
        private final List<Integer> evalDecreasingOrder = new ArrayList<>();

        void add(Action action, double utilValue) {
            int idx = 0;
            while (idx < actions.size() && utilValue <= utilValues.get(idx))
                idx++;
            actions.add(idx, action);
            utilValues.add(idx, utilValue);
            evalDecreasingOrder.add(idx, evalDecreasingOrder.size());
        }

        int size() {
            return actions.size();
        }
    }
}