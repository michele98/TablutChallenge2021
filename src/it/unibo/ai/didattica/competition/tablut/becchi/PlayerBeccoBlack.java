package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.io.IOException;

public class PlayerBeccoBlack extends PlayerBecco {

    public PlayerBeccoBlack(int timeout) {
        super(State.Turn.BLACK, timeout);
    }

    @Override
    public Action getOptimalAction (State state) throws IOException {
        //TODO: implement Minimax to determine Action
        return new Action("z0", "z0", this.getColor());
    }

}
