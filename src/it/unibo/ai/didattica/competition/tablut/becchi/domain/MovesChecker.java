package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;
import java.util.HashSet;

public interface MovesChecker {

    /**
     * alternative of obtainLegalMoves : In this (faster) implementation the possible destinations are
     * returned as 1D array with the destination coordinates
     * @param origin the coordinates of the pawn whose we wanna know the possible moves
     * @param state the actual state of the game in which moves are calculated
     * @return arrays with destinations coordinates
     */
    HashSet<int[]> obtainLegalMovesInt(int[] origin, State state);

    /**
     * alternative of obtainLegalMoves : In this (faster) implementation the possible moves are
     * returned as 2D array where the first element are the origin coordinates and the
     * second element are the destination coordinates
     * @param origin the coordinates of the pawn whose we wanna know the possible moves
     * @param state the actual state of the game in which moves are calculated
     * @return 2D arrays whose first element is the origin and second element is the feasible destination
     */
    HashSet<int[][]> obtainLegalMovesIntOrigin(int[] origin, State state);

    /**
     * In this implementation all the possible moves of a certain pawn are returned as a set of possible Actions
     *
     *
     * @param origin the coordinates of the pawn whose we wanna know the possible moves
     * @param state the actual state of the game in which moves are calculated
     * @return the set of possible Actions
     * @throws IOException in case the boxing of the moves in Actions fails
     */
    HashSet<Action> obtainLegalMoves(int[] origin, State state) throws IOException;

    /**
     * In this implementation all the possible moves that can be performed by each player of a given color are
     * returned as a set of possible Actions
     *
     *
     * @param state the actual state of the game in which moves are calculated
     * @param turnColor The turn of the player, StateTablut.Turn.WHITE or StateTablut.Turn.BLACK
     * @return the set of all possible Actions
     * @throws IOException in case the boxing of the moves in Actions fails
     */
    HashSet<Action> getAllMoves(State state, State.Turn turnColor) throws IOException;

    /**
     * alternative of getAllMoves: in this (faster) implementation are calculated all the possible moves that can be performed by each player of a given color
     *
     *
     * @param state the actual state of the game in which moves are calculated
     * @param turnColor The turn of the player, StateTablut.Turn.WHITE or StateTablut.Turn.BLACK
     * @return 2D arrays whose first element is the origin and second element is the feasible destination
     */
    HashSet<int[][]> getAllMovesInt(State state, State.Turn turnColor);

}
