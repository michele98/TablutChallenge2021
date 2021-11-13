package it.unibo.ai.didattica.competition.tablut.becchi;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class StateBecchiTablut extends StateTablut {
    private int depth;

    public StateBecchiTablut() {
        super();
        depth = 0;
    }

    public void increaseDepth() {
        depth = depth + 1;
    }

    public int getDepth() {
        return depth;
    }
}
