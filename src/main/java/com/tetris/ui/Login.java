package com.tetris.ui;
import javax.swing.*;

public class Login {

    public static String askPlayerName() {
        String nome = JOptionPane.showInputDialog(null,
                "Digite seu nome:", "Bem-vindo ao Tetris",
                JOptionPane.QUESTION_MESSAGE);
        if (nome == null || nome.trim().isEmpty()) {
            nome = "Player";
        }
        return nome;
    }
}
