package com.tetris.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {
    public static boolean leftPressed = false;
    public static boolean rightPressed = false;
    public static boolean upPressed = false;
    public static boolean downPressed = false;
    public static boolean pausePressed = false;

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
        }
    }
}