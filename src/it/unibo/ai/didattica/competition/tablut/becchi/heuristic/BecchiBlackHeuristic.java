package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BecchiBlackHeuristic implements Heuristic{
    @Override
    public double getValue(State state) {
        return Features.numberOfBlack(state) * 35.
                + Features.numberOfWhiteEaten(state) * 48.
                + Features.pawnsNearKing(state) * 15.
                + Features.numberOfPawnsOnRhombus(state) * 2.;
    }
}