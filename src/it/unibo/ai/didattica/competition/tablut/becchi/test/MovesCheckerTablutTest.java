package it.unibo.ai.didattica.competition.tablut.becchi.test;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.MovesCheckerTablut;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.exceptions.*;

class MovesCheckerTablutTest {

    @org.junit.jupiter.api.Test
    void obtainLegalMoves() throws PawnException, DiagonalException, ClimbingException, ActionException, CitadelException, StopException, OccupitedException, BoardException, ClimbingCitadelException, ThroneException, IOException {

        // a simple test with the white pawn in pos (D1)
        State s = new StateTablut();
        s.setTurn(State.Turn.BLACK);
        MovesCheckerTablut m = new MovesCheckerTablut();
        int[] pawn = new int[]{0,3};
        List<int[]> l = m.obtainLegalMovesInt(pawn, s);
        System.out.println(Arrays.deepToString(l.toArray()));

        // a simple test with the white pawn in pos (D5)
        pawn = new int[]{5,4};
        s.setTurn(State.Turn.WHITE);
        l = m.obtainLegalMovesInt(pawn,  s);
        System.out.println(Arrays.deepToString(l.toArray()));


        //testing all moves of black players from the start situation
        s.setTurn(State.Turn.BLACK);
        List<Action> allmoves = m.getAllMoves(s);
        System.out.println("Number of moves available: "+allmoves.size());
        allmoves.forEach(System.out::println);

        GameAshtonTablut g;

        for (Action a : allmoves) {
            s = new StateTablut();
            s.setTurn(State.Turn.BLACK);
            g = new GameAshtonTablut(2, -1, "logs", "WP", "BP");
            g.checkMove(s, a);
        }

        //testing all moves of white players from the start situation
        s.setTurn(State.Turn.WHITE);
        long startTime = System.nanoTime();
        allmoves = m.getAllMoves(s);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Number of moves available: "+allmoves.size());
        allmoves.forEach(System.out::println);

        for (Action a : allmoves) {
            s = new StateTablut();
            s.setTurn(State.Turn.WHITE);
            g = new GameAshtonTablut(2, -1, "logs", "WP", "BP");
            g.checkMove(s, a);
        }

        System.out.println(elapsedTime*1e-9);
/*
        //testing all moves of white players from the start situation
        s.setTurn(State.Turn.WHITE);
        long startTime = System.nanoTime();
        allmoves = m.getAllMovesMultithread(s);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Number of moves available: "+allmoves.size());
        allmoves.forEach(System.out::println);

        for (Action a : allmoves) {
            s = new StateTablut();
            s.setTurn(State.Turn.WHITE);
            g = new GameAshtonTablut(2, -1, "logs", "WP", "BP");
            g.checkMove(s, a);
        }

        System.out.println(elapsedTime*1e-9);
*/
    }
}