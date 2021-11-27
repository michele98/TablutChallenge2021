package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class OurBecchiBlackHeuristic implements Heuristic{

    @Override
    public double getValue(State state) {
        int numberWinWays = Features.countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays > 1 ? (double) Features.countWinWays(state) / 4 : 0.0;
        return   Features.ratioNumberOfBlackAlive(state) * 25.
                + Features.ratioNumberOfWhiteEaten(state) * 40.
                + Features.ratioBlackPawnsNearKing(state) * 20.
                + Features.ratioNumberOfPawnsOnRhombus(state) * 5.
                + Features.kingDistanceFromTheEdges(state) * 5.
                - numberOfWinEscapesKing * 5.;
    }

}
