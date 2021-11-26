package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.MovesCheckerTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public abstract class Features {

    private final static int NUM_BLACK = 16;
    private final static int NUM_WHITE = 8;

    //Threshold used to decide whether to use best positions configuration
    private final static int THRESHOLD_BEST = 2;

    //Matrix of favourite white positions in the initial stages of the game
    private final static int[][] bestPositions = {
            {2,3},  {3,5},
            {5,3},  {6,5}
    };

    private final static int NUM_BEST_POSITION = bestPositions.length;

    //Threshold used to decide whether to use rhombus configuration
    private final static int THRESHOLD = 10;

    //Matrix of favourite black positions in initial stages and to block the escape ways
    private final static int[][] rhombus = {
            {1,2},       {1,6},
    {2,1},                   {2,7},

    {6,1},                   {6,7},
            {7,2},       {7,6}
    };

    //Number of tiles on rhombus
    private final static int NUM_TILES_ON_RHOMBUS = rhombus.length;

    private final static boolean[][] safePositions = new boolean[9][9];

    static {
        safePositions[3][4] = true;
        safePositions[4][4] = true;
        safePositions[5][4] = true;
        safePositions[4][3] = true;
        safePositions[4][5] = true;
    }


    // percentage of white players in the best positions
    //FIXME Strange positions of white players..
    public static double bestPositions(State state) {
        return (double) getNumberOnBestPositions(state) / NUM_BEST_POSITION;
    }

    // percentage of white players that are still alive
    public static double numberOfWhiteAlive(State state) {
        return (double)(state.getNumberOf(State.Pawn.WHITE)) / NUM_WHITE;
    }

    // percentage of black players that are dead
    public static double numberOfBlackEaten(State state) {
        return (double)(NUM_BLACK - state.getNumberOf(State.Pawn.BLACK)) / NUM_BLACK;
    }

    // percentage of black players FAR from the king (that are not on a side of the king)
    public static double blackSurroundKing(State state) {
        //FIXME stupid name
        int blackKillersRequired = Features.getNumEatenPositions(state);
        return (double)(blackKillersRequired - checkNearPawns(state, kingPosition(state),State.Turn.BLACK.toString())) / blackKillersRequired;
    }

    // percentage of black players that are alive
    //FIXME name should be "numberOfBlackAlive"
    public static double numberOfBlack(State state) {
        return (double) state.getNumberOf(State.Pawn.BLACK) / NUM_BLACK;
    }

    //percentage of white players that are dead
    public static double numberOfWhiteEaten(State state) {
        return (double) (NUM_WHITE - state.getNumberOf(State.Pawn.WHITE)) / NUM_WHITE;
    }

    //percentage of killers near the king
    public static double pawnsNearKing(State state) {
        return (double)  checkNearPawns(state, kingPosition(state),State.Turn.BLACK.toString()) / getNumEatenPositions(state);
    }

    //percentage of black pawns in rhombus position (or 0 if black players are under the THRESHOLD)
    public static double numberOfPawnsOnRhombus(State state) {
        return (double) getNumberOnRhombus(state) / NUM_TILES_ON_RHOMBUS;
    }

    //value of protection of the king
    public static double protectionKing(State state){
        //Values whether there is only a white pawn near to the king
        final double VAL_NEAR = 0.6;
        final double VAL_TOT = 1.0;

        double result = 0.0;

        int[] kingPos = kingPosition(state);
        //Pawns near to the king
        ArrayList<int[]> pawnsPositions = (ArrayList<int[]>) positionNearPawns(state,kingPos,State.Pawn.BLACK.toString());

        //There is a black pawn that threatens the king and 2 pawns are enough to eat the king
        if (pawnsPositions.size() == 1 && getNumEatenPositions(state) == 2){

            //Here we check if we would be able to protect the king from one black killer
            // (we check if there's already a white protector)

            int[] enemyPos = pawnsPositions.get(0); //FIXME qui si puo migliorare levando collections o con hashset
            //Used to store other position from where king could be eaten
            int[] targetPosition = new int[2];
            //Enemy right to the king
            if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] + 1){
                //Left to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] - 1;
                if (state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
                //Enemy left to the king
            }else if(enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] -1){
                //Right to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] + 1;
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
                //Enemy up to the king
            }else if(enemyPos[1] == kingPos[1] && enemyPos[0] == kingPos[0] - 1){
                //Down to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] + 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
                //Enemy down to the king
            }else{
                //Up there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] - 1;
                targetPosition[1] = kingPos[1];
                if(state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    result += VAL_NEAR;
                }
            }

            //Considering (movable) whites that we can use as barriers for the target pawn
            double otherPoints = VAL_TOT - VAL_NEAR;
            double contributionPerN = 0.0;

            //TODO capire questo
            //Whether it is better to keep free the position
            if (targetPosition[0] == 0 || targetPosition[0] == 8 || targetPosition[1] == 0 || targetPosition[1] == 8){
                if(state.getPawn(targetPosition[0],targetPosition[1]).equalsPawn(State.Pawn.EMPTY.toString())){
                    result = 1.0;
                } else {
                    result = 0.0;
                }
            }else{
                //Considering a reduced number of neighbours whether target is near to citadels or throne
                if (targetPosition[0] == 4 && targetPosition[1] == 2 || targetPosition[0] == 4 && targetPosition[1] == 6
                        || targetPosition[0] == 2 && targetPosition[1] == 4 || targetPosition[0] == 6 && targetPosition[1] == 4
                        || targetPosition[0] == 3 && targetPosition[1] == 4 || targetPosition[0] == 5 && targetPosition[1] == 4
                        || targetPosition[0] == 4 && targetPosition[1] == 3 || targetPosition[0] == 4 && targetPosition[1] == 5){
                    contributionPerN = otherPoints / 2;
                }else{
                    contributionPerN = otherPoints / 3;
                }

                result += contributionPerN * checkNearPawns(state, targetPosition,State.Pawn.WHITE.toString());
            }

        }
        return result;
    }

    //returns the number of ways through which the king can win (from 0 up to 4)
    public static int countWinWays(State state){
        int[] kingPosition= kingPosition(state);
        int col = 0;
        int row = 0;

        if(safePositionKing(kingPosition)) {
            return 0;
        }
        //TODO here they check that the king is not in the big cross defined by the black camps
        //FIXME this can be made faster, also: maybe add the king position directly here?
        /*
        if((!(kingPosition[1] > 2 && kingPosition[1] < 6)) &&
                (!(kingPosition[0] > 2 && kingPosition[0] < 6))) {
            //if we are in an unsafe position then it hasn't much sense to consider moves towards the black camps
            //this, unless the camps are empty...
            //not safe row not safe col

                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state,kingPosition);
                }


        else {
            */
        if ((kingPosition[1] > 2 && kingPosition[1] < 6)) {
                // safe row not safe col
                row = countFreeRow(state, kingPosition);
            }
        if ((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                // safe col not safe row
                col = countFreeColumn(state, kingPosition);
            }

        return (col + row);
    }

    //checks the whole table to get the king position
    private static int[] kingPosition(State state) {
        //where I saved the int position of the king
        int[] king= new int[2];
        //obtain the board
        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (state.getPawn(i, j).equalsPawn("K")) {
                    king[0] = i;
                    king[1] = j;
                }
            }
        }
        return king;
    }

    //number of pawns of the kind specified by target that are at manhattan distance=1 from the target
    private static int checkNearPawns(State state, int[] position, String target){
        int count=0;
        //GET TURN
        State.Pawn[][] board = state.getBoard();
        if(board[position[0]-1][position[1]].equalsPawn(target))
            count++;
        if(board[position[0]+1][position[1]].equalsPawn(target))
            count++;
        if(board[position[0]][position[1]-1].equalsPawn(target))
            count++;
        if(board[position[0]][position[1]+1].equalsPawn(target))
            count++;
        return count;
    }

    //number of pawns of kind target in side of the given position
    private static List<int[]> positionNearPawns(State state,int[] position, String target){
        List<int[]> occupiedPosition = new ArrayList<>();
        int[] pos = new int[2];
        //GET TURN
        State.Pawn[][] board = state.getBoard();
        if(board[position[0]-1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] - 1;
            pos[1] = position[1];
            occupiedPosition.add(pos);
        }
        if(board[position[0]+1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] + 1;
            occupiedPosition.add(pos);
        }
        if(board[position[0]][position[1]-1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] - 1;
            occupiedPosition.add(pos);
        }
        if(board[position[0]][position[1]+1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] + 1;
            occupiedPosition.add(pos);
        }

        return occupiedPosition;
    }

    //checks if the king is over or on a side of the throne
    private static boolean safePositionKing(int[] kingPosition){
        return safePositions[kingPosition[0]][kingPosition[1]];
    }

    // 2 if both rows sides <-- and --> are free to the border
    private static int countFreeRow(State state,int[] position){
        int row=position[0];
        int column=position[1];
        int[] currentPosition;
        int freeWays=0;

        boolean leftObs = false;
        boolean rightObs = false;

        int right = column+1;
        int left = column-1;

        //going right
        while (right<=8 && (!rightObs)){

            currentPosition = new int[]{row,right};

            // i don't find an obstacle on right
            if (!MovesCheckerTablut.checkObstacles (position,currentPosition,state)) {
                right = right+1;
            }

            // i find an obstacle on right
            else
                rightObs = true;
        }

        if(!rightObs)
            freeWays++;

        //going left
        while (left>=0 && (!leftObs)){

            currentPosition = new int[]{row,left};

            // i don't find an obstacle on left
            if (!MovesCheckerTablut.checkObstacles (position,currentPosition,state)) {
                left = left-1;
            }

            // i find an obstacle on left
            else
                leftObs = true;
        }
        if(!leftObs)
            freeWays++;

        return freeWays;
    }

    // 2 if both column sides<-- and --> are free to the border
    private static int countFreeColumn(State state,int[] position){
        //lock column
        int row=position[0];
        int column=position[1];
        int[] currentPosition;
        int freeWays=0;
        int down = row+1;
        int up = row-1;

        boolean upObs = false;
        boolean downObs = false;

        //going down
        while(down <=8 && (!downObs)){
            currentPosition = new int[]{down, column};

            // i don't find an obstacle down
            if (!MovesCheckerTablut.checkObstacles(position, currentPosition, state)) {
                down = down + 1;
            }
            // i find an obstacle down
            else
                downObs = true;
        }

        if(!downObs)
            freeWays++;

        //going up
 /*       for(int i=row-1;i>=0;i--) {
            currentPosition[0]=i;
            currentPosition[1]=column;
            if (checkOccupiedPosition(state,currentPosition)){
                countUp++;
            }
        }
*/
        while(up >=0 && (!upObs)){
            currentPosition = new int[]{up, column};

            // i don't find an obstacle up
            if (!MovesCheckerTablut.checkObstacles(position, currentPosition, state)) {
                up = up - 1;
            }
            // i find an obstacle up
            else
                upObs = true;
        }


        if(!upObs)
            freeWays++;

        return freeWays;
    }

    /* if a given position is occupied by any kind of pawn, doesn't consider black camps or thrones as obstacles
    private static boolean checkOccupiedPosition(State state,int[] position){
        return !state.getPawn(position[0], position[1]).equals(State.Pawn.EMPTY);
    }
     */

    // number of black pawns that are required to kill the King in the given state
    private static int getNumEatenPositions(State state){

        int[] kingPosition = kingPosition(state);

        if (kingPosition[0] == 4 && kingPosition[1] == 4){
            return 4;
        } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
                || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)){
            return 3;
        } else{
            return 2;
        }

    }

    // number of white pawns that are in the defined best positions
    private static int getNumberOnBestPositions(State state){

        int num = 0;

        if (state.getNumberOf(State.Pawn.WHITE) >= NUM_WHITE - THRESHOLD_BEST){
            for(int[] pos: bestPositions){
                if(state.getPawn(pos[0],pos[1]).equalsPawn(State.Pawn.WHITE.toString())){
                    num++;
                }
            }
        }

        return num;
    }

    // forwards to getValuesOnRhombus just if black players are over a given threshold, otherwise gives 0
    private static int getNumberOnRhombus(State state){

        if (state.getNumberOf(State.Pawn.BLACK) >= THRESHOLD) {
            return getValuesOnRhombus(state);
        }else{
            return 0;
        }
    }

    // counts the number of pawns in rhombus position
    private static int getValuesOnRhombus(State state){

        int count = 0;
        for (int[] position : rhombus) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;

    }
}
