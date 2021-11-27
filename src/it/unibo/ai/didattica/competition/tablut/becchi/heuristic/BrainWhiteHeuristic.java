package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class BrainWhiteHeuristic implements Heuristic{
    Heuristic black = new BecchiBlackHeuristic();
    Heuristic white = new BecchiWhiteHeuristic();

    @Override
    public double getValue(State state) {
        return white.getValue(state) - black.getValue(state);
    }
}
