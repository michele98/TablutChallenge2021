package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import aima.core.search.adversarial.Game;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.ArrayList;
import java.util.List;

// Adapter di GameAshtonTablut a Game di aima-library
public class GameBecchiTablut implements Game<State, Action, State.Turn> {

    private GameAshtonTablut game;
    private List<String> escapes;

    public GameBecchiTablut() {

        // TODO check if there are better parameters.
        this.game = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");

        escapes = new ArrayList<String>();
        escapes.add("b1");
        escapes.add("c1");
        escapes.add("g1");
        escapes.add("h1");
        escapes.add("a2");
        escapes.add("a3");
        escapes.add("a7");
        escapes.add("a8");
        escapes.add("b9");
        escapes.add("c9");
        escapes.add("g9");
        escapes.add("h9");
        escapes.add("i2");
        escapes.add("i3");
        escapes.add("i7");
        escapes.add("i8");
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
    public List<Action> getActions(State stateTablut) {
        // Method implemented by Alessandro D'Amico
        return null;
    }

    @Override
    public State getResult(State state, Action action) {
        //TODO: fix the logic instead of using try catch
        State newState = null;

        // checkMove as he says check if the move is correct and return the newState
        try {
            newState = this.game.checkMove(state, action);
        } catch (Exception e) {
            System.out.println("The action was illegal");
            System.exit(-1);
        }
        // TODO check if newState has the variable depth updated
        return newState;
    }

    @Override
    public boolean isTerminal(State state) {

        return state.getTurn().equals(State.Turn.BLACKWIN) ||
                state.getTurn().equals(State.Turn.WHITEWIN) ||
                state.getTurn().equals(State.Turn.DRAW);
        /*
        String kingBox = "";
        boolean exKing = false;
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())){
                    kingBox = state.getBox(i, j);
                    exKing = true;
                }
            }
        }
        // se il re è in una zona di escape
        // o se non c'è il re
        // o se arrivo a depth-lim
        return escapes.contains(kingBox) || !exKing || state.getDepth()>depthLim;
         */
    }

    @Override
    public double getUtility(State stateTablut, State.Turn turn) {
        //TODO: Write the utility function
        return 0;
    }
}
