package com.tetris.model;

import java.awt.Color;

public class Mino_Bar extends Mino {
    public Mino_Bar() {
        super();
        b[0].c = Color.CYAN;
        b[1].c = Color.CYAN;
        b[2].c = Color.CYAN;
        b[3].c = Color.CYAN;
    }

    @Override
    public void setInitialPosition(int startX, int startY) {
        b[0].x = startX;
        b[0].y = startY;

        b[1].x = startX;
        b[1].y = startY + Block.SIZE;

        b[2].x = startX;
        b[2].y = startY + 2 * Block.SIZE;

        b[3].x = startX;
        b[3].y = startY + 3 * Block.SIZE;
    }
}