package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;

import aima.core.search.framework.Metrics;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco {
    private AdversarialSearch<State,Action> solver;
    private Action previousAction = null;

    public Action getOptimalAction (State state) {
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

    public void setSolver(AdversarialSearch<State,Action> solver) {
        this.solver = solver;
    }
}
