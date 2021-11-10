package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class PlayerBecco {
    private State.Turn color;

    public PlayerBecco(State.Turn color) {
        this.color = color;
    }
    public State.Turn getColor() {
        return color;
    }

    public abstract Action getOptimalAction (State state) throws IOException;
}
