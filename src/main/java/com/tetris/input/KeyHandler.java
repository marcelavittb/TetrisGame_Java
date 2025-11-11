package com.tetris.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * KeyHandler que é tambem KeyAdapter (pode ser adicionado como listener)
 * e expõe flags estáticas que o PlayManager checa.
 *
 * Observações:
 * - Usamos campos 'volatile' para evitar efeitos de cache em threads diferentes.
 * - Incluí rPressed para recomeçar (se você usar).
 */
public class KeyHandler extends KeyAdapter {
    public static volatile boolean leftPressed = false;
    public static volatile boolean rightPressed = false;
    public static volatile boolean upPressed = false;
    public static volatile boolean downPressed = false;
    public static volatile boolean pausePressed = false;
    public static volatile boolean rPressed = false; // tecla 'R' para reiniciar

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_P:
                pausePressed = !pausePressed;
                break;
            case KeyEvent.VK_R:
                rPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                // opcional: mapa de espaço -> drop rápido
                downPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_R:
                rPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                downPressed = false;
                break;
        }
    }
}
