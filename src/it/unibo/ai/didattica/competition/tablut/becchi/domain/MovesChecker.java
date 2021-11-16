package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public interface MovesChecker {

    /**
     *
     * @param origin the coordinates of the pawn to move
     * @param state the state of the game
     * @return the list of possible moves
     */
    public HashSet<int[]> obtainLegalMovesInt(int[] origin, State state);
    public HashSet<Action> obtainLegalMoves(int[] origin, State state) throws IOException;

}
