package it.unibo.ai.didattica.competition.tablut.becchi.solver;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiBlackHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;

public class IterativeDeepeningSolver extends IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn> {

    //private Heuristic heuristic;

    public IterativeDeepeningSolver(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
    }

    @Override
    protected double eval(State state, State.Turn player) {

        if (game.isTerminal(state)) {
            return super.eval(state,player);
        }
        super.eval(state,player);

        // TODO: check if is better considering state.getTurn() instead of player
        Heuristic heuristic;
        if (player.equals(State.Turn.WHITE)) {
            heuristic = new BecchiWhiteHeuristic();
        } else {
            heuristic = new BecchiBlackHeuristic();
        }
        return  heuristic.getValue(state);
    }

    @Override
    public List<Action> orderActions(State state, List<Action> actions, State.Turn player, int depth) {
        //TODO: implement this method
        return actions;
    }

    //TODO: maybe implement isSignificantlybetter
}
