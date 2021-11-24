package it.unibo.ai.didattica.competition.tablut.becchi.solver;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import aima.core.search.framework.Metrics;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.*;
import java.util.stream.Collectors;

public class TableIterativeDeepeningSolver implements AdversarialSearch<State,Action> {
    public static final String METRICS_NODES_EXPANDED = "nodesExpanded";
    public static final String METRICS_MAX_DEPTH = "maxDepth";
    protected Game<State, Action, State.Turn> game;
    protected double utilMax;
    protected double utilMin;
    protected int currDepthLimit;
    private boolean heuristicEvaluationUsed;
    private final Timer timer;
    private boolean logEnabled;
    private Metrics metrics = new Metrics();
    private final Heuristic heuristic;
    private final HashMap<String,List<Integer>> stateOrders = new HashMap<>();
    private final HashSet<String> visited = new HashSet<>();

    public TableIterativeDeepeningSolver(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time, Heuristic heuristic) {
        this.game = game;
        this.utilMin = utilMin;
        this.utilMax = utilMax;
        this.timer = new Timer(time);
        this.heuristic = heuristic;
        this.setLogEnabled(true);
    }

    public void setLogEnabled(boolean b) {
        this.logEnabled = b;
    }

    @Override
    public Action makeDecision(State state) {
        this.metrics = new Metrics();
        this.visited.add(state.toLinearString());
        StringBuffer logText = null;
        State.Turn player = this.game.getPlayer(state);
        List<Action> results = this.orderActions(state, this.game.getActions(state), player, 0);
        this.timer.start();
        this.currDepthLimit = 0;

        do {
            this.incrementDepthLimit();
            if (this.logEnabled) {
                logText = new StringBuffer("depth " + this.currDepthLimit + ": ");
            }

            this.heuristicEvaluationUsed = false;
            ActionStore newResults = new ActionStore();

            for (Action action : results) {
                HashSet<String> visited = new HashSet<>(this.visited);
                double value = this.minValue(this.game.getResult(state, action), player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, visited);
                if (this.timer.timeOutOccured()) {
                    break;
                }

                newResults.add(action, value);
                if (this.logEnabled) {
                    assert logText != null;
                    logText.append(action).append("->").append(value).append(" ");
                }
            }

            if (this.logEnabled) {
                System.out.println(logText);
            }

            if (newResults.size() > 0) {
                results = newResults.actions;
                stateOrders.put(state.toLinearString(),newResults.order);
                if (!this.timer.timeOutOccured() && (this.hasSafeWinner(newResults.utilValues.get(0)) || newResults.size() > 1 && this.isSignificantlyBetter(newResults.utilValues.get(0), newResults.utilValues.get(1)))) {
                    break;
                }
            }
        } while(!this.timer.timeOutOccured() && this.heuristicEvaluationUsed);

        Action result = results.get(0);
        this.visited.add(game.getResult(state,result).toLinearString());
        return result;
    }

    public double maxValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        this.updateMetrics(depth);
        if (!this.game.isTerminal(state) && depth < this.currDepthLimit && !this.timer.timeOutOccured()) {
            double value = Double.NEGATIVE_INFINITY;
            ActionStore newResults = new ActionStore();
            List<Action> actions = this.orderActions(state, this.game.getActions(state), player, depth);
            for(Iterator<Action> var10 = actions.iterator(); var10.hasNext(); alpha = Math.max(alpha, value)) {
                Action action = var10.next();
                State newState = this.game.getResult(state, action);
                if (visited.contains(newState.toLinearString())) {
                    newState.setTurn(State.Turn.DRAW);
                }
                HashSet<String> newVisited = new HashSet<>(visited);
                newVisited.add(newState.toLinearString());
                value = Math.max(value, this.minValue(newState, player, alpha, beta, depth + 1, newVisited));
                newResults.add(action,value);
                if (value >= beta) {
                    for (int i = newResults.size(); i < actions.size(); i++) { newResults.append(); }
                    stateOrders.put(state.toLinearString(),newResults.order);
                    return value;
                }
            }
            stateOrders.put(state.toLinearString(),newResults.order);
            return value;
        } else {
            return this.eval(state, player);
        }
    }

    public double minValue(State state, State.Turn player, double alpha, double beta, int depth, HashSet<String> visited) {
        this.updateMetrics(depth);
        if (!this.game.isTerminal(state) && depth < this.currDepthLimit && !this.timer.timeOutOccured()) {
            double value = Double.POSITIVE_INFINITY;
            ActionStore newResults = new ActionStore();
            List<Action> actions = this.orderActions(state, this.game.getActions(state), player, depth);
            for(Iterator<Action> var10 = actions.iterator(); var10.hasNext(); beta = Math.min(beta, value)) {
                Action action = var10.next();
                State newState = this.game.getResult(state, action);
                if (visited.contains(newState.toLinearString())) {
                    newState.setTurn(State.Turn.DRAW);
                }
                HashSet<String> newVisited = new HashSet<>(visited);
                newVisited.add(newState.toLinearString());
                value = Math.min(value, this.maxValue(newState, player, alpha, beta, depth + 1, newVisited));
                newResults.add(action,value);
                if (value <= alpha) {
                    for (int i = newResults.size(); i < actions.size(); i++) { newResults.append(); }
                    stateOrders.put(state.toLinearString(),newResults.order);
                    return value;
                }
            }
            stateOrders.put(state.toLinearString(),newResults.order);
            return value;
        } else {
            return this.eval(state, player);
        }
    }

    private void updateMetrics(int depth) {
        this.metrics.incrementInt("nodesExpanded");
        this.metrics.set("maxDepth", Math.max(this.metrics.getInt("maxDepth"), depth));
    }

    @Override
    public Metrics getMetrics() {
        return this.metrics;
    }

    protected void incrementDepthLimit() {
        ++this.currDepthLimit;
    }

    protected boolean isSignificantlyBetter(double newUtility, double utility) {
        return false;
    }

    protected boolean hasSafeWinner(double resultUtility) {
        return resultUtility <= this.utilMin || resultUtility >= this.utilMax;
    }

    protected double eval(State state, State.Turn player) {
        if (this.game.isTerminal(state)) {
            return this.game.getUtility(state, player);
        } else {
            this.heuristicEvaluationUsed = true;
            return heuristic.getValue(state);
        }
    }

    public List<Action> orderActions(State state, List<Action> actions, State.Turn player, int depth) {
        String key = state.toLinearString();
        if (stateOrders.containsKey(key)) {
            return stateOrders.get(key).stream().map(actions::get).collect(Collectors.toList());
        }
        return actions;
    }

    private static class ActionStore {
        private final List<Action> actions;
        private final List<Double> utilValues;
        private final List<Integer> order;

        private ActionStore() {
            this.actions = new ArrayList<>();
            this.utilValues = new ArrayList<>();
            this.order = new ArrayList<>();
        }

        void add(Action action, double utilValue) {
            int idx = 0;

            while(idx < this.actions.size() && utilValue <= this.utilValues.get(idx)) {
                idx++;
            }

            this.actions.add(idx, action);
            this.utilValues.add(idx, utilValue);
            this.order.add(idx,this.order.size());
        }

        void append() {
            Action action = null;
            double utilValue = Double.NEGATIVE_INFINITY;
            int idx = this.actions.size();
            this.actions.add(idx, action);
            this.utilValues.add(idx, utilValue);
            this.order.add(idx,this.order.size());
        }

        int size() {
            return this.actions.size();
        }
    }

    private static class Timer {
        private final long duration;
        private long startTime;

        Timer(int maxSeconds) {
            this.duration = 1000L * (long)maxSeconds;
        }

        void start() {
            this.startTime = System.currentTimeMillis();
        }

        boolean timeOutOccured() {
            return System.currentTimeMillis() > this.startTime + this.duration;
        }
    }
}
