package com.tetris.ui;

import com.tetris.game.PlayManager;
import com.tetris.input.KeyHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private PlayManager playManager;
    private Timer timer;

    public GamePanel(PlayManager playManager) {
        this.playManager = playManager;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyHandler());

        timer = new Timer(16, this); // Aproximadamente 60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        playManager.draw((Graphics2D) g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        playManager.update();
        repaint();
    }
}