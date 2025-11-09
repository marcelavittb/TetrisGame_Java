package com.tetris.model;

import java.awt.Color;
import java.awt.Graphics2D;

public class Block {
    public int x;
    public int y;
    public Color c;

    public static final int SIZE = 30; // tamanho fixo do bloco

    public Block() {
        x = 0;
        y = 0;
        c = Color.GRAY;
    }

    public Block(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    // Desenha o bloco no canvas
    public void draw(Graphics2D g2) {
        g2.setColor(c);
        g2.fillRect(x, y, SIZE, SIZE);

        g2.setColor(Color.black);
        g2.drawRect(x, y, SIZE, SIZE);
    }
}