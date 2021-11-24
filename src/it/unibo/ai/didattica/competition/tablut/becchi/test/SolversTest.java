package it.unibo.ai.didattica.competition.tablut.becchi.test;

import aima.core.search.adversarial.AdversarialSearch;
import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiBlackHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.BecchiWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.heuristic.Heuristic;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.BecchiIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.IterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.becchi.solver.TableIterativeDeepeningSolver;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SolversTest {

    @Test
    void makeDecision() throws IOException {
        int timeout = 20;
        GameBecchiTablut game = new GameBecchiTablut(0, 0, "garbage", "client_w", "client_b");
        Heuristic heuristic = new BecchiWhiteHeuristic();
        AdversarialSearch<State, Action> solver = new IterativeDeepeningSolver(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout, heuristic);

        State.Pawn[][] board = new State.Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = State.Pawn.EMPTY;
            }
        }

        board[4][4] = State.Pawn.THRONE;

        board[3][7] = State.Pawn.KING;

        board[0][8] = State.Pawn.WHITE;
        board[3][3] = State.Pawn.WHITE;
        board[4][2] = State.Pawn.WHITE;
        board[5][4] = State.Pawn.WHITE;
        board[7][0] = State.Pawn.WHITE;
        board[7][5] = State.Pawn.WHITE;
        board[8][6] = State.Pawn.WHITE;
        board[6][4] = State.Pawn.WHITE;

        board[3][0] = State.Pawn.BLACK;
        board[4][0] = State.Pawn.BLACK;
        board[5][0] = State.Pawn.BLACK;
        board[2][1] = State.Pawn.BLACK;
        board[5][8] = State.Pawn.BLACK;
        board[8][3] = State.Pawn.BLACK;
        board[8][4] = State.Pawn.BLACK;
        board[8][5] = State.Pawn.BLACK;

        State state = new StateTablut();
        state.setTurn(State.Turn.WHITE);
        state.setBoard(board);

        Action result = solver.makeDecision(state);
        System.out.println(result);
        assertEquals( new Action("h4","h1",State.Turn.WHITE), result);
    }
}