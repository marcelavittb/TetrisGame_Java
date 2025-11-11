package com.tetris.replay;

import java.io.Serializable;
import java.util.List;

import com.tetris.model.GameAction;

public class ReplayData implements Serializable {
    private static final long serialVersionUID = 1L;

    public long seed;
    public int score;
    public List<GameAction> actions;

    public ReplayData(long seed, int score, List<GameAction> actions) {
        this.seed = seed;
        this.score = score;
        this.actions = actions;
    }
}
