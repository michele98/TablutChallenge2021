package it.unibo.ai.didattica.competition.tablut.becchi.player;

import aima.core.search.adversarial.AdversarialSearch;
import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.*;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.BecchiIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.TableIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.TranspositionTableIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class PlayerBeccoWhite extends PlayerBecco {
    public PlayerBeccoWhite(int timeout, GameBecchiTablut game) {
        super();
        // TODO: change OurHeuristics to beat BecchiHeuristics
        Heuristic heuristic = new MyHeuristicWhite();
        AdversarialSearch<State, Action> solver = new TranspositionTableIterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);
        this.setSolver(solver);
    }
}
