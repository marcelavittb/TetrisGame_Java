package com.tetris.model;

import java.awt.Color;

public class Mino_T extends Mino {
    public Mino_T() {
        super();
        b[0].c = Color.MAGENTA;
        b[1].c = Color.MAGENTA;
        b[2].c = Color.MAGENTA;
        b[3].c = Color.MAGENTA;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX;
        b[0].y = startY + Block.SIZE;

        b[1].x = startX + Block.SIZE;
        b[1].y = startY + Block.SIZE;

        b[2].x = startX + 2 * Block.SIZE;
        b[2].y = startY + Block.SIZE;

        b[3].x = startX + Block.SIZE;
        b[3].y = startY;
    }
}