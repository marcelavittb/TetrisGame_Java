package com.tetris.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main - abre o LoginDialog estilizado e cria o MainFrame.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // cria um frame temporário owner para o diálogo (não mostrado)
            JFrame owner = new JFrame();
            owner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            com.tetris.ui.LoginDialog login = new com.tetris.ui.LoginDialog(owner);
            String playerName = login.showDialog();
            if (playerName == null || playerName.trim().isEmpty()) {
                // usuário cancelou
                System.exit(0);
            }

            // Abre o frame principal com o nome retornado
            new MainFrame(playerName);
        });
    }
}
