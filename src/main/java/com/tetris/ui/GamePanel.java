package com.tetris.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.tetris.game.PlayManager;
import com.tetris.input.KeyHandler;

/**
 * GamePanel atualizado:
 * - possui método start() que inicia o Timer (para garantir foco)
 * - mantém compatibilidade com PlayManager.draw/update
 */
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
        // adiciona listener que define flags estáticas no KeyHandler
        addKeyListener(new KeyHandler());

        // não inicia o timer aqui — inicia no start() para garantir foco depois do frame visível
        timer = new Timer(16, this); // Aproximadamente 60 FPS
    }

    /**
     * Inicia o loop (deve ser chamado após o frame ficar visível,
     * para garantir que o painel receba o foco de teclado).
     */
    public void start() {
        timer.start();
        // pede foco para o painel assim que possível
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
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
