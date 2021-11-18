package it.unibo.ai.didattica.competition.tablut.becchi.heuristic;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashMap;
import java.util.Map;

public class BecchiBlackHeuristic implements Heuristic{

    private final String RHOMBUS_POSITIONS = "rhombusPositions";
    private final String WHITE_EATEN = "numberOfWhiteEaten";
    private final String BLACK_ALIVE = "numberOfBlackAlive";
    private final String BLACK_SURROUND_KING = "blackSurroundKing";

    private final Map<String,Double> weights;
    private String[] keys;

    public BecchiBlackHeuristic() {
        //Initializing weights
        weights = new HashMap<String, Double>();
        weights.put(BLACK_ALIVE, 35.0);
        weights.put(WHITE_EATEN, 48.0);
        weights.put(BLACK_SURROUND_KING, 15.0);
        weights.put(RHOMBUS_POSITIONS, 2.0);

        //Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);
    }

    @Override
    public double getValue(State state) {
        double utilityValue = 0.0;

        //Atomic functions to combine to get utility value through the weighted sum
        double numberOfBlack = Features.numberOfBlack(state);
        double numberOfWhiteEaten = Features.numberOfWhiteEaten(state);
        double  pawnsNearKing = Features.pawnsNearKing(state);
        double numberOfPawnsOnRhombus = Features.numberOfPawnsOnRhombus(state);


        //Weighted sum of functions to get final utility value
        Map<String,Double> atomicUtilities = new HashMap<String,Double>();
        atomicUtilities.put(BLACK_ALIVE,numberOfBlack);
        atomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        atomicUtilities.put(BLACK_SURROUND_KING,pawnsNearKing);
        atomicUtilities.put(RHOMBUS_POSITIONS,numberOfPawnsOnRhombus);

        for (int i = 0; i < weights.size(); i++){
            utilityValue += weights.get(keys[i]) * atomicUtilities.get(keys[i]);
        }

        return utilityValue;
    }
}
