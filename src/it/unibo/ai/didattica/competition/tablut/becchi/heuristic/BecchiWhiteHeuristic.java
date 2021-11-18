package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashMap;
import java.util.Map;

public class BecchiWhiteHeuristic implements Heuristic{

    private final static String BEST_POSITIONS = "bestPositions";
    private final static String BLACK_EATEN = "numberOfBlackEaten";
    private final static String WHITE_ALIVE = "numberOfWhiteAlive";
    private final static String NUM_ESCAPES_KING = "numberOfWinEscapesKing";
    private final static String BLACK_SURROUND_KING = "blackSurroundKing";
    private final static String PROTECTION_KING = "protectionKing";

    private final Map<String,Double> weights;
    private String[] keys;

    public BecchiWhiteHeuristic() {

        //Initializing weights
        weights = new HashMap<String,Double>();
        //Positions which are the best moves at the beginning of the game
        weights.put(BEST_POSITIONS, 2.0);
        weights.put(BLACK_EATEN, 20.0);
        weights.put(WHITE_ALIVE, 35.0);
        weights.put(NUM_ESCAPES_KING, 18.0);
        weights.put(BLACK_SURROUND_KING, 7.0);
        weights.put(PROTECTION_KING, 18.0);

        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    @Override
    public double getValue(State state) {
        double utilityValue = 0;
        //Atomic functions to combine to get utility value through the weighted sum
        double bestPositions = Features.bestPositions(state);
        double numberOfWhiteAlive =  Features.numberOfWhiteAlive(state);
        double numberOfBlackEaten = Features.numberOfBlackEaten(state);
        double blackSurroundKing = Features.blackSurroundKing(state);
        double protectionKing = Features.protectionKing(state);

        int numberWinWays = Features.countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays>1 ? (double)Features.countWinWays(state)/4 : 0.0;

        Map<String, Double> values = new HashMap<String, Double>();
        values.put(BEST_POSITIONS, bestPositions);
        values.put(WHITE_ALIVE, numberOfWhiteAlive);
        values.put(BLACK_EATEN, numberOfBlackEaten);
        values.put(NUM_ESCAPES_KING,numberOfWinEscapesKing);
        values.put(BLACK_SURROUND_KING,blackSurroundKing);
        values.put(PROTECTION_KING,protectionKing);

        for (int i=0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * values.get(keys[i]);
        }

        return utilityValue;
    }

}
