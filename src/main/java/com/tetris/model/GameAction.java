package com.tetris.model;

import java.io.Serializable;

public class GameAction implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MOVE_LEFT = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int ROTATE = 2;
    public static final int DROP = 3;
    public static final int PAUSE = 4;

    public int actionType;
    public long frameNumber;

    public GameAction(int actionType, long frameNumber) {
        this.actionType = actionType;
        this.frameNumber = frameNumber;
    }
}