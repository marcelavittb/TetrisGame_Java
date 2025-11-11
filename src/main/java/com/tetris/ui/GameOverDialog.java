package com.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.tetris.game.PlayManager;

public class GameOverDialog extends JDialog {
    private PlayManager playManager;
    private JLabel lblScore;
    private JButton btnRestart;
    private JButton btnReplays;
    private JButton btnExit;

    public GameOverDialog(Frame owner, PlayManager playManager, int finalScore) {
        super(owner, "Game Over", true);
        this.playManager = playManager;
        initUI(finalScore);
    }

    private void initUI(int finalScore) {
        setSize(420, 220);
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10,10));

        // header
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(28,34,48));
        head.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("GAME OVER");
        title.setForeground(new Color(255,100,100));
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        head.add(title, BorderLayout.WEST);
        add(head, BorderLayout.NORTH);

        // center card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(22,28,40));
        card.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);

        lblScore = new JLabel("Pontuação: " + finalScore);
        lblScore.setForeground(Color.WHITE);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        card.add(lblScore, gbc);

        JLabel sub = new JLabel("O que deseja fazer?");
        sub.setForeground(new Color(200,200,200));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 1;
        card.add(sub, gbc);

        add(card, BorderLayout.CENTER);

        // bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        bottom.setBackground(new Color(18,24,37));
        btnRestart = new JButton("Recomeçar");
        btnReplays = new JButton("Ver Replays");
        btnExit = new JButton("Sair");

        stylePrimary(btnRestart);
        styleSecondary(btnReplays);
        styleDanger(btnExit);

        bottom.add(btnRestart);
        bottom.add(btnReplays);
        bottom.add(btnExit);
        add(bottom, BorderLayout.SOUTH);

        // actions
        btnRestart.addActionListener(e -> {
            dispose();
            playManager.resetGame();
        });

        btnReplays.addActionListener(e -> {
            dispose();
            playManager.openReplayDialog();
        });

        btnExit.addActionListener(e -> {
            System.exit(0);
        });

        // Esc closes (restarts by default)
        getRootPane().registerKeyboardAction(e -> {
            dispose();
            playManager.resetGame();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void stylePrimary(JButton b) {
        b.setBackground(new Color(70,160,255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void styleSecondary(JButton b) {
        b.setBackground(new Color(60,70,90));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleDanger(JButton b) {
        b.setBackground(new Color(220,80,80));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    public static void showFor(Frame owner, PlayManager pm, int score) {
        SwingUtilities.invokeLater(() -> {
            GameOverDialog d = new GameOverDialog(owner, pm, score);
            d.setVisible(true);
        });
    }
}
