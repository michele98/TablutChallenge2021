package it.unibo.ai.didattica.competition.tablut.becchi.player;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerBeccoWhite extends PlayerBecco {
    public PlayerBeccoWhite(int timeout, GameBecchiTablut game) {
        super(State.Turn.WHITE, timeout, game, new BecchiWhiteHeuristic());
    }
}
