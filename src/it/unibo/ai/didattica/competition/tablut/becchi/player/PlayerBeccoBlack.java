package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;
import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.MyHeuristicBlack;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.TranspositionTableIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerBeccoBlack extends PlayerBecco {
    public PlayerBeccoBlack(int timeout, GameBecchiTablut game) {
        super();
        // TODO: change OurHeuristics to beat BecchiHeuristics
        Heuristic heuristic = new MyHeuristicBlack();
        AdversarialSearch<State, Action> solver = new TranspositionTableIterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);
        this.setSolver(solver);
    }
}
