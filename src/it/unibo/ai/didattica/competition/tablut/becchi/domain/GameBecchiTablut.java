package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import java.io.IOException;
import java.util.List;

// Adapter di GameAshtonTablut a Game di aima-library
public class GameBecchiTablut implements Game<State, Action, State.Turn> {

    private final it.unibo.ai.didattica.competition.tablut.domain.Game game;
    private final MovesCheckerTablut movesChecker;


    public GameBecchiTablut() {

        this.game = new GameAshtonTablutBecchi(0, -1, "garbage", "ciccio", "ciccio");
        this.movesChecker = new MovesCheckerTablut();
    }

    @Override
    public State getInitialState() {
        return new StateTablut();
    }

    @Override
    public State.Turn[] getPlayers() {
        // Probably we don't use this function, but must be implemented because is in the interface.
        State.Turn[] players = new State.Turn[2];
        players[0] = State.Turn.WHITE;
        players[1] = State.Turn.BLACK;
        return players;
    }

    @Override
    public State.Turn getPlayer(State stateTablut) {
        return stateTablut.getTurn();
    }

    @Override
    public List<Action> getActions(State state) {
        List<Action> actions = null;
        try {
            actions = movesChecker.getAllMoves(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actions;
    }


    @Override
    public State getResult(State state, Action action) {
        //TODO: fix the logic instead of using try catch
        State newState = null;
        try {
            newState = this.game.checkMove(state.clone(), action);
        } catch (Exception e) {
            System.out.println("The action was illegal");
            System.exit(-1);
        }
        return newState;
    }

    @Override
    public boolean isTerminal(State state) {

        return state.getTurn().equals(State.Turn.BLACKWIN) ||
                state.getTurn().equals(State.Turn.WHITEWIN) ||
                state.getTurn().equals(State.Turn.DRAW);
    }

    @Override
    public double getUtility(State state, State.Turn player) {
        if (player.equals(State.Turn.BLACK)) {
            if (state.getTurn().equals(State.Turn.BLACKWIN)) {
                return 1;
            } else if (state.getTurn().equals(State.Turn.WHITEWIN)) {
                return -1;
            }
        } else if (player.equals(State.Turn.WHITE)) {
            if (state.getTurn().equals(State.Turn.BLACKWIN)) {
                return -1;
            } else if (state.getTurn().equals(State.Turn.WHITEWIN)) {
                return 1;
            }
        }
        return 0;
    }
}
