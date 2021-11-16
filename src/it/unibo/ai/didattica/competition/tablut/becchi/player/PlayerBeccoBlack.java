package it.unibo.ai.didattica.competition.tablut.becchi.player;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.io.IOException;

public class PlayerBeccoBlack extends PlayerBecco {
    public PlayerBeccoBlack(int timeout, GameBecchiTablut game) {
        super(State.Turn.BLACK, timeout, game);
    }
}
