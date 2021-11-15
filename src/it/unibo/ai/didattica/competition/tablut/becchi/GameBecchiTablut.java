package it.unibo.ai.didattica.competition.tablut.becchi;


import aima.core.search.adversarial.Game;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

// Adapter di GameAshtonTablut a Game di aima-library
public class GameBecchiTablut implements Game<StateBecchiTablut, Action, State.Turn> {

    private GameAshtonTablut game;
    private List<String> escapes;
    private int depthLim;

    public GameBecchiTablut(int depthLim) {
        this.depthLim = depthLim;

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
    public StateBecchiTablut getInitialState() {
        return new StateBecchiTablut();
    }

    @Override
    public State.Turn[] getPlayers() {
        // Probably we don't use this function, but must be implemented because is in the interface.
        return new State.Turn[0];
    }

    @Override
    public State.Turn getPlayer(StateBecchiTablut stateTablut) {
        return stateTablut.getTurn();
    }

    @Override
    public List<Action> getActions(StateBecchiTablut stateTablut) {
        // Method implemented by Alessandro D'Amico
        return null;
    }

    @Override
    public StateBecchiTablut getResult(StateBecchiTablut state, Action action) {
        StateBecchiTablut newState = null;

        // checkMove as he says check if the move is correct and return the newState
        try {
            newState = (StateBecchiTablut) this.game.checkMove(state, action);
        } catch (Exception e) {
            System.out.println("The action was illegal");
            System.exit(-1);
        }
        // TODO check if newState has the variable depth updated
        newState.increaseDepth();
        return newState;
    }

    @Override
    public boolean isTerminal(StateBecchiTablut state) {

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
    }

    @Override
    public double getUtility(StateBecchiTablut stateTablut, State.Turn turn) {
        return 0;
    }
}
