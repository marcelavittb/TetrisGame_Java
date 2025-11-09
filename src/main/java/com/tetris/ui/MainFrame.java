package com.tetris.ui;

import com.tetris.game.PlayManager;

import javax.swing.*;
import java.awt.*;

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

        gamePanel.requestFocusInWindow();
    }
}