package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public class PlayerBeccoWhite extends PlayerBecco {

    public PlayerBeccoWhite(int timeout) {
        super(State.Turn.WHITE, timeout);
    }

    @Override
    public Action getOptimalAction (State state) throws IOException {
        //TODO: implement Minimax to determine Action
        return new Action("z0", "z0", this.getColor());
    }
}
