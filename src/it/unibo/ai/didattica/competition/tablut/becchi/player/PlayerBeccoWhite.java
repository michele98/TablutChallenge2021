package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;
import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.OurBecchiWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.BecchiIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerBeccoWhite extends PlayerBecco {
    public PlayerBeccoWhite(int timeout, GameBecchiTablut game) {
        super();
        // TODO: change OurHeuristics to beat BecchiHeuristics
        Heuristic heuristic = new BecchiWhiteHeuristic();
        AdversarialSearch<State, Action> solver = new BecchiIterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);
        this.setSolver(solver);
    }
}
