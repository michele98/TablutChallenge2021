package it.unibo.ai.didattica.competition.tablut.becchi.domain;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MovesCheckerTablut implements MovesChecker {

    private final boolean [][] campsTop  = new boolean[9][9];
    private final boolean [][] campsBottom = new boolean[9][9];
    private final boolean [][] campsLeft  = new boolean[9][9];
    private final boolean [][] campsRight  = new boolean[9][9];
    int [] throne = new int[]{4, 4};

    public MovesCheckerTablut() {

        //top camps
        campsTop[0][3] = true;
        campsTop[0][4] = true;
        campsTop[0][5] = true;
        campsTop[1][4] = true;

        //bottom camps
        campsBottom[8][3] = true;
        campsBottom[8][4] = true;
        campsBottom[8][5] = true;
        campsBottom[7][4] = true;

        //left camps
        campsLeft[3][0] = true;
        campsLeft[4][0] = true;
        campsLeft[5][0] = true;
        campsLeft[4][1] = true;

        //right camps
        campsRight[3][8] = true;
        campsRight[4][8] = true;
        campsRight[5][8] = true;
        campsRight[4][7] = true;

    }


    private boolean checkObstacles(int origin[], int destination[], State state){

        State.Pawn pawnToMove = state.getPawn(origin[0],origin[1]);
        State.Pawn pawnDestination = state.getPawn(destination[0],destination[1]);

        // non possono andare addosso alle altre nere ne sulle bianche, ne sul re
        if (!pawnDestination.equalsPawn(State.Pawn.EMPTY.toString()))
            return true;

        // non posso andare sul trono
        if (destination[0] == throne[0] && destination[1] == throne[1])
            return true;

        if (pawnToMove.equalsPawn(State.Pawn.BLACK.toString())) {
            // non posso andare nei campi SE NE SONO FUORI
            if (campsBottom[destination[0]][destination[1]]) {
                if (!campsBottom[origin[0]][origin[1]])
                    return true;
            }
            else if (campsTop[destination[0]][destination[1]]) {
                if (!campsTop[origin[0]][origin[1]])
                    return true;
            }
            else if (campsLeft[destination[0]][destination[1]]) {
                if (!campsLeft[origin[0]][origin[1]])
                    return true;
            }
            else if (campsRight[destination[0]][destination[1]]) {
                if (!campsRight[origin[0]][origin[1]])
                    return true;
            }


        }
        else if (pawnToMove.equalsPawn(State.Pawn.WHITE.toString()) || pawnToMove.equalsPawn(State.Pawn.KING.toString())) {
            //non possono andare nei campi
            if (campsTop[destination[0]][destination[1]] || campsBottom[destination[0]][destination[1]] || campsLeft[destination[0]][destination[1]] || campsRight[destination[0]][destination[1]] )
                return true;

        }

        return false;
    }

    @Override
    public List<int[]> obtainLegalMovesInt(int[] origin, State state){
        State.Pawn pawnToMove = state.getPawn(origin[0], origin[1]);
        if (pawnToMove.equalsPawn(State.Pawn.EMPTY.toString()))
            throw new IllegalArgumentException("Can't move an empty tile!");

        int i = origin[0];
        int j = origin[1];
        List<int[]> list = new ArrayList<>();

        int left = j-1;
        int right = j+1;
        int down = i+1;
        int up = i-1;

        int rightLimit = 8;
        int leftLimit = 0;
        int upLimit = 0;
        int downLimit = 8;

        boolean leftObs = false;
        boolean rightObs = false;
        boolean upObs = false;
        boolean downObs = false;

        int[] dest;

        while ((left >= leftLimit) && (!leftObs)) {
            dest = new int[]{i, left};
            if (!checkObstacles(origin, dest, state)){
                list.add(new int[]{i, left});
                left = left - 1;
            }
            else
                leftObs = true;
        }

        while((right <= rightLimit) && (!rightObs)){
            dest = new int[]{i, right};
            if (!checkObstacles(origin, dest, state)){
                list.add(new int[]{i, right});
                right = right + 1;
            }
            else
                rightObs = true;
        }

        while((up >= upLimit) && (!upObs)){
            dest = new int[]{up, j};
            if (!checkObstacles(origin, dest, state)){
                list.add(new int[]{up, j});
                up = up - 1;
            }
            else
                upObs = true;
        }

        while((down <= downLimit) && (!downObs)) {
            dest = new int[]{down, j};
            if (!checkObstacles(origin, dest, state)) {
                list.add(new int[]{down, j});
                down = down + 1;
            } else
                downObs = true;
        }

        return list;
    }

    @Override
    public List<int[][]> obtainLegalMovesIntOrigin(int[] origin, State state){
        State.Pawn pawnToMove = state.getPawn(origin[0], origin[1]);
        if (pawnToMove.equalsPawn(State.Pawn.EMPTY.toString()))
            throw new IllegalArgumentException("Can't move an empty tile!");

        int i = origin[0];
        int j = origin[1];
        List<int[][]> list = new ArrayList<>();

        int left = j-1;
        int right = j+1;
        int down = i+1;
        int up = i-1;

        int rightLimit = 8;
        int leftLimit = 0;
        int upLimit = 0;
        int downLimit = 8;

        boolean leftObs = false;
        boolean rightObs = false;
        boolean upObs = false;
        boolean downObs = false;

        int[] dest;
        int[][] transition = new int[2][2];

        while ((left >= leftLimit) && (!leftObs)) {
            dest = new int[]{i, left};
            if (!checkObstacles(origin, dest, state)){
                transition[0] =  origin;
                transition[1] = dest;
                list.add(transition);
                left = left - 1;
            }
            else
                leftObs = true;
        }

        while((right <= rightLimit) && (!rightObs)){
            dest = new int[]{i, right};
            if (!checkObstacles(origin, dest, state)){
                transition[0] =  origin;
                transition[1] = dest;
                list.add(transition);
                right = right + 1;
            }
            else
                rightObs = true;
        }

        while((up >= upLimit) && (!upObs)){
            dest = new int[]{up, j};
            if (!checkObstacles(origin, dest, state)){
                transition[0] =  origin;
                transition[1] = dest;
                list.add(transition);
                up = up - 1;
            }
            else
                upObs = true;
        }

        while((down <= downLimit) && (!downObs)) {
            dest = new int[]{down, j};
            if (!checkObstacles(origin, dest, state)) {
                transition[0] =  origin;
                transition[1] = dest;
                list.add(transition);
                down = down + 1;
            } else
                downObs = true;
        }

        return list;
    }

    @Override
    public List<Action> obtainLegalMoves(int[] origin, State state) throws IOException {
        int i = origin[0];
        int j = origin[1];

        State.Turn turn;

        if( state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())
                || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString()))
            turn = State.Turn.WHITE;

        else
            turn = State.Turn.BLACK;


        State.Pawn pawnToMove = state.getPawn(i, j);
        if (pawnToMove.equalsPawn(State.Pawn.EMPTY.toString()))
            throw new IllegalArgumentException("Can't move an empty tile!");


        List<Action> list = new ArrayList<>();

        int left = j-1;
        int right = j+1;
        int down = i+1;
        int up = i-1;

        int rightLimit = 8;
        int leftLimit = 0;
        int upLimit = 0;
        int downLimit = 8;

        boolean leftObs = false;
        boolean rightObs = false;
        boolean upObs = false;
        boolean downObs = false;

        int[] dest;

        String from = state.getBox(origin[0], origin[1]);
        String to;

        while ((left >= leftLimit) && (!leftObs)) {
            dest = new int[]{i, left};
            if (!checkObstacles(origin, dest, state)){
                to = state.getBox(i, left);
                Action a = new Action(from, to, turn);
                list.add(a);
                left = left - 1;
            }
            else
                leftObs = true;
        }

        while((right <= rightLimit) && (!rightObs)){
            dest = new int[]{i, right};
            if (!checkObstacles(origin, dest, state)){
                to = state.getBox(i, right);
                Action a = new Action(from, to, turn);
                list.add(a);
                right = right + 1;
            }
            else
                rightObs = true;
        }

        while((up >= upLimit) && (!upObs)){
            dest = new int[]{up, j};
            if (!checkObstacles(origin, dest, state)){
                to = state.getBox(up, j);
                Action a = new Action(from, to, turn);
                list.add(a);
                up = up - 1;
            }
            else
                upObs = true;
        }

        while((down <= downLimit) && (!downObs)) {
            dest = new int[]{down, j};
            if (!checkObstacles(origin, dest, state)) {
                to = state.getBox(down, j);
                Action a = new Action(from, to, turn);
                list.add(a);
                down = down + 1;
            } else
                downObs = true;
        }

        return list;
    }

    @Override
    public List<Action> getAllMoves(State state) throws IOException {

        List<int[]> movablePawns = new ArrayList<>();
        List<Action> allMoves = new ArrayList<>();

        if (state.getTurn().equals(State.Turn.WHITE) ) {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
                            || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        movablePawns.add(buf);
                    }
                }

            }
        } else if (state.getTurn().equals(State.Turn.BLACK)) {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        movablePawns.add(buf);
                    }
                }
            }

        }


        for (int[] i : movablePawns) {
            allMoves.addAll(obtainLegalMoves(i, state));
        }
        return  allMoves;
    }

    @Override
    public List<int[][]> getAllMovesInt(State state, State.Turn turnColor) {

        List<int[]> movablePawns = new ArrayList<>();
        List<int[][]> allMoves = new ArrayList<>();

        if (turnColor.equals(State.Turn.WHITE)) {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
                            || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        movablePawns.add(buf);
                    }
                }

            }
        } else if (turnColor.equals(State.Turn.BLACK)) {
            int[] buf;
            for (int i = 0; i < state.getBoard().length; i++) {
                for (int j = 0; j < state.getBoard().length; j++) {
                    if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                        buf = new int[2];
                        buf[0] = i;
                        buf[1] = j;
                        movablePawns.add(buf);
                    }
                }
            }

        }

        for (int[] i : movablePawns) {
            allMoves.addAll(obtainLegalMovesIntOrigin(i, state));
        }
        return  allMoves;
    }


}