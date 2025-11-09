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

    public GamePanel(String playerName) {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.requestFocus();

        playManager = new PlayManager(playerName);
        this.addKeyListener(new KeyHandler());

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Antialias se quiser:
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        playManager.draw(g2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        playManager.update();
        repaint();
    }

    public PlayManager getPlayManager() {
        return playManager;
    }
}