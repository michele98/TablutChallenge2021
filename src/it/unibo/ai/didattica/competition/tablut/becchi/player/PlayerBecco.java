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

    public PlayerBecco(State.Turn color, int timeout, GameBecchiTablut game, Heuristic heuristic) {
        this.color = color;
        this.solver = new IterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);
    }

    public State.Turn getColor() {
        return color;
    }

    public Action getOptimalAction (State state) throws IOException {
        previousAction = solver.makeDecision(state);
        return previousAction;
    }

    public Action getOptimalAction(State state, boolean verbose) throws IOException {
        if (verbose) {
            System.out.println("Choosing optimal action...");
        }
        Action result = getOptimalAction(state);
        printMetrics();

        return result;
    }

    public String getMetrics() {
        Metrics metrics = solver.getMetrics();
        String message = "Search summary:";
        message += "\n - Maximum depth: " + metrics.get("maxDepth");
        message += "\n - Nodes expanded: " + metrics.get("nodesExpanded");
        message += "\n - Action chosen: " + previousAction.toString();
        return message;
    }

    public void printMetrics() {
        System.out.println(getMetrics());
    }
}
