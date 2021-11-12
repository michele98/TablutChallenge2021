package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco {
    private State.Turn color;
    private int timeout;

    public PlayerBecco(State.Turn color, int timeout) {
        this.color = color;
        this.timeout = timeout;
    }

    public State.Turn getColor() {
        return color;
    }

    public abstract Action getOptimalAction (State state) throws IOException;
}
