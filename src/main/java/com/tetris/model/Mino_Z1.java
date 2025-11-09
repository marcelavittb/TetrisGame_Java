package com.tetris.model;

import java.awt.Color;

public class Mino_Z1 extends Mino {
    public Mino_Z1() {
        super();
        b[0].c = Color.RED;
        b[1].c = Color.RED;
        b[2].c = Color.RED;
        b[3].c = Color.RED;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX;
        b[0].y = startY;

        b[1].x = startX + Block.SIZE;
        b[1].y = startY;

        b[2].x = startX + Block.SIZE;
        b[2].y = startY + Block.SIZE;

        b[3].x = startX + 2 * Block.SIZE;
        b[3].y = startY + Block.SIZE;
    }
}