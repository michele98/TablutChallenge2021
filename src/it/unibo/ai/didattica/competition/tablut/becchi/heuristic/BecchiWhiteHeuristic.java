package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BecchiWhiteHeuristic implements Heuristic{
    @Override
    public double getValue(State state) {

        double countWinWays = Features.countWinWays(state);

        return (countWinWays > 1 ? 4.5 * countWinWays : 0.0)
                + Features.ratioWhitePlayersBestPositions(state) * 2.0
                + Features.ratioNumberOfWhiteAlive(state) * 35.0
                + Features.ratioNumberOfBlackEaten(state) * 20.0
                + Features.ratioBlackFarFromKing(state) * 7.0
                + Features.protectionKing(state) * 18.0;
    }
}
