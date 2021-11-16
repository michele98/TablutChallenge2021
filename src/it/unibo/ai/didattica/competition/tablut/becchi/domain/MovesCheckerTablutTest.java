package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.exceptions.*;

class MovesCheckerTablutTest {

    @org.junit.jupiter.api.Test
    void obtainLegalMoves() throws PawnException, DiagonalException, ClimbingException, ActionException, CitadelException, StopException, OccupitedException, BoardException, ClimbingCitadelException, ThroneException, IOException {

        // a simple test with the white pawn in pos (D1)
        State s = new StateTablut();
        s.setTurn(State.Turn.WHITE);
        MovesCheckerTablut m = new MovesCheckerTablut();
        int[] pawn = new int[]{0,3};
        HashSet<int[]> l = m.obtainLegalMovesInt(pawn, s);
        System.out.println(Arrays.deepToString(l.toArray()));

        //TODO cosa intende con "repeated moves??

        // a simple test with the white pawn in pos (D5)
        pawn = new int[]{5,4};
        s.setTurn(State.Turn.WHITE);
        l = m.obtainLegalMovesInt(pawn,  s);
        System.out.println(Arrays.deepToString(l.toArray()));


        //testing all moves of black players from the start situation
        s.setTurn(State.Turn.BLACK);
        HashSet<Action> allmoves = m.getAllMoves(s, State.Turn.BLACK);
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
        allmoves = m.getAllMoves(s, State.Turn.WHITE);
        System.out.println("Number of moves available: "+allmoves.size());
        allmoves.forEach(System.out::println);

        for (Action a : allmoves) {
            s = new StateTablut();
            s.setTurn(State.Turn.WHITE);
            g = new GameAshtonTablut(2, -1, "logs", "WP", "BP");
            g.checkMove(s, a);
        }

    }
}