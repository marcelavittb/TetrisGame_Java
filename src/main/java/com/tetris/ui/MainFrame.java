package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.tetris.game.PlayManager;

/**
 * MainFrame atualizado:
 * - cria PlayManager e GamePanel
 * - inicia o GamePanel.start() após o frame ficar visível (garante foco)
 */
public class MainFrame extends JFrame {

    private PlayManager playManager;
    private GamePanel gamePanel;

    public MainFrame(String playerName) {
        super("Tetris");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Passa a referência do JFrame para PlayManager para evitar erro de foco
        playManager = new PlayManager(playerName, this);

        // Cria um painel para o jogo
        gamePanel = new GamePanel(playManager);
        add(gamePanel, BorderLayout.CENTER);

        // Painel inferior com botão para abrir diálogo de replay
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton replayButton = new JButton("Ver Replays");
        replayButton.addActionListener(e -> playManager.openReplayDialog(this));

        buttonPanel.add(replayButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        // após o frame visível, inicia o loop do painel (garante que requestFocus funcione)
        gamePanel.start();
    }
}
