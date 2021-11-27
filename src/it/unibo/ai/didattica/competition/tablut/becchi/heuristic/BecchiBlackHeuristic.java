package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BecchiBlackHeuristic implements Heuristic{
    @Override
    public double getValue(State state) {
        return Features.ratioNumberOfBlackAlive(state) * 35.
                + Features.ratioNumberOfWhiteEaten(state) * 48.
                + Features.ratioBlackPawnsNearKing(state) * 15.
                + Features.ratioNumberOfPawnsOnRhombus(state) * 2.;
    }
}