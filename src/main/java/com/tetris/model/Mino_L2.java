package com.tetris.model;

import java.awt.Color;

public class Mino_L2 extends Mino {
    public Mino_L2() {
        super();
        b[0].c = Color.BLUE;
        b[1].c = Color.BLUE;
        b[2].c = Color.BLUE;
        b[3].c = Color.BLUE;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX + Block.SIZE;
        b[0].y = startY;

        b[1].x = startX + Block.SIZE;
        b[1].y = startY + Block.SIZE;

        b[2].x = startX + Block.SIZE;
        b[2].y = startY + 2 * Block.SIZE;

        b[3].x = startX;
        b[3].y = startY + 2 * Block.SIZE;
    }
}