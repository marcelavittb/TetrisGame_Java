package com.tetris.model;

import com.tetris.game.PlayManager;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Mino {
    public Block[] b = new Block[4];

    public Mino() {
        for (int i = 0; i < 4; i++) {
            b[i] = new Block();
        }
    }

    // Deve definir posição inicial dos blocos da peça em pixels (usar multiplos de Block.SIZE)
    public abstract void setInitialPosition(int startX, int startY);

    // Move a peça por (dx, dy) em pixels
    public void move(int dx, int dy) {
        for (Block block : b) {
            block.x += dx;
            block.y += dy;
        }
    }

    // Desenhar peça
    public void draw(Graphics2D g2) {
        for (Block block : b) {
            block.draw(g2);
        }
    }

    // Rotaciona a peça (90 graus sentido horário)
    public void rotate() {
        // Base rotaciona os blocos em relação ao bloco 1 como pivot
        Block pivot = b[1];
        for (Block block : b) {
            int relX = block.x - pivot.x;
            int relY = block.y - pivot.y;

            int newX = pivot.x - relY;
            int newY = pivot.y + relX;

            block.x = newX;
            block.y = newY;
        }
    }

    // Rotaciona a peça no sentido contrário (desfaz rotação)
    public void rotateBack() {
        Block pivot = b[1];
        for (Block block : b) {
            int relX = block.x - pivot.x;
            int relY = block.y - pivot.y;

            int newX = pivot.x + relY;
            int newY = pivot.y - relX;

            block.x = newX;
            block.y = newY;
        }
    }
}