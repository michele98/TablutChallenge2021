package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BecchiWhiteHeuristic implements Heuristic{
    @Override
    public double getValue(State state) {

        double countWinWays = Features.countWinWays(state);

        return (countWinWays > 1 ? 4.5 * countWinWays : 0.0)
                + Features.bestPositions(state) * 2.0
                + Features.numberOfWhiteAlive(state) * 35.0
                + Features.numberOfBlackEaten(state) * 20.0
                + Features.blackSurroundKing(state) * 7.0
                + Features.protectionKing(state) * 18.0;
    }
}
