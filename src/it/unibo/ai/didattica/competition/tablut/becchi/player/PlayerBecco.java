package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;

import aima.core.search.framework.Metrics;
import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.IterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco {
    private final State.Turn color;
    private final AdversarialSearch<State, Action> solver;
    private Action previousAction = null;
    private double searchTime = 0.0;
    private int movesMade = 0;

    public PlayerBecco(State.Turn color, int timeout, GameBecchiTablut game, Heuristic heuristic) {
        this.color = color;
        this.solver = new IterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);
    }

    public State.Turn getColor() {
        return color;
    }

    public Action getOptimalAction (State state) throws IOException {
        previousAction = solver.makeDecision(state);
        movesMade++;
        return previousAction;
    }

    public Action getOptimalAction(State state, boolean verbose) throws IOException {
        if (verbose) {
            System.out.println("Choosing optimal action...");
        }
        double startTime = System.currentTimeMillis();
        Action result = getOptimalAction(state);
        searchTime = (System.currentTimeMillis() - startTime);
        printMetrics();

        return result;
    }

    public String getMetrics() {
        Metrics metrics = solver.getMetrics();

        String message = "Search summary for my move " + movesMade + ":";
        message += "\n - Maximum depth: " + metrics.get("maxDepth");
        message += "\n - Nodes expanded: " + metrics.get("nodesExpanded");
        message += "\n - Action chosen: " + previousAction.toString();
        message += "\n - time used for search: " + searchTime + " ms";
        return message;
    }

    public void printMetrics() {
        System.out.println(getMetrics());
    }
}
