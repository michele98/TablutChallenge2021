package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.IterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco {
    private final State.Turn color;
    private final int timeout;
    private final AdversarialSearch<State, Action> solver;

    public PlayerBecco(State.Turn color, int timeout, GameBecchiTablut game) {
        this.color = color;
        this.timeout = timeout;
        this.solver = new IterativeDeepeningSolver(game, -1, 1, timeout);
    }

    public State.Turn getColor() {
        return color;
    }

    public Action getOptimalAction (State state) throws IOException {
        return solver.makeDecision(state);
    }
}
