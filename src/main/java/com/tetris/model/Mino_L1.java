package com.tetris.model;

import java.awt.Color;

public class Mino_L1 extends Mino {
    public Mino_L1() {
        super();
        b[0].c = Color.ORANGE;
        b[1].c = Color.ORANGE;
        b[2].c = Color.ORANGE;
        b[3].c = Color.ORANGE;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX;
        b[0].y = startY;

        b[1].x = startX;
        b[1].y = startY + Block.SIZE;

        b[2].x = startX;
        b[2].y = startY + 2 * Block.SIZE;

        b[3].x = startX + Block.SIZE;
        b[3].y = startY + 2 * Block.SIZE;
    }
}