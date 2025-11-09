package com.tetris.ui;

import com.tetris.ui.Login;
import com.tetris.ui.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = Login.askPlayerName();
            new MainFrame(playerName);
        });
    }
}