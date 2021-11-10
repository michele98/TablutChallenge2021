package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.io.IOException;

public class PlayerBeccoBlack extends PlayerBecco {

    public PlayerBeccoBlack() {
        super(State.Turn.BLACK);
    }

    @Override
    public Action getOptimalAction (State state) throws IOException {
        //TODO: implement Minimax to determine Action
        return new Action("z0", "z0", this.getColor());
    }

}
