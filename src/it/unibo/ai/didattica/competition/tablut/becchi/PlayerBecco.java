package it.unibo.ai.didattica.competition.tablut.becchi;

import aima.core.search.adversarial.AlphaBetaSearch;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco extends AlphaBetaSearch<StateBecchiTablut, Action, State.Turn> {
    private State.Turn color;
    private int timeout;

    public PlayerBecco(State.Turn color, int timeout, GameBecchiTablut game) {
        super(game);
        this.color = color;
        this.timeout = timeout;
    }

    public State.Turn getColor() {
        return color;
    }

    public abstract Action getOptimalAction (State state) throws IOException;
}
