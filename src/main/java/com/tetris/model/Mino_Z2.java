package com.tetris.model;

import java.awt.Color;

public class Mino_Z2 extends Mino {
    public Mino_Z2() {
        super();
        b[0].c = Color.GREEN;
        b[1].c = Color.GREEN;
        b[2].c = Color.GREEN;
        b[3].c = Color.GREEN;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX + Block.SIZE;
        b[0].y = startY;

        b[1].x = startX + 2 * Block.SIZE;
        b[1].y = startY;

        b[2].x = startX;
        b[2].y = startY + Block.SIZE;

        b[3].x = startX + Block.SIZE;
        b[3].y = startY + Block.SIZE;
    }
}