package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class OurBecchiWhiteHeuristic implements Heuristic{

    Heuristic black = new BecchiBlackHeuristic();
    Heuristic white = new BecchiWhiteHeuristic();


    @Override
    public double getValue(State state) {
        int numberWinWays = Features.countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays > 1 ? (double) Features.countWinWays(state) / 4 : 0.0;
        return  Features.ratioWhitePlayersBestPositions(state) * 4.0 +
                Features.ratioNumberOfWhiteAlive(state) * 40.0 +
                Features.ratioNumberOfBlackEaten(state) * 15.0 +
                numberOfWinEscapesKing * 13.0 +
                Features.protectionKing(state) * 13.0 -
                Features.kingDistanceFromTheEdges(state) * 7.0;
    }
    /*
    @Override
    public double getValue(State state) {
        return white.getValue(state) - black.getValue(state);
    }
    */
}
