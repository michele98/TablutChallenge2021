package it.unibo.ai.didattica.competition.tablut.becchi.player;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiBlackHeuristic;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerBeccoBlack extends PlayerBecco {
    public PlayerBeccoBlack(int timeout, GameBecchiTablut game) {
        super(State.Turn.BLACK, timeout, game, new BecchiBlackHeuristic());
    }
}
