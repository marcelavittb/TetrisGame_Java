package com.tetris.model;

import java.io.Serializable;

public class PlayerScore implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;  // ID único
    public String name;
    public int score;
    public String date;
    public byte[] replayData;

    // Construtor completo
    public PlayerScore(int id, String name, int score, String date, byte[] replayData) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.date = date;
        this.replayData = replayData;
    }

    // Construtor simplificado para top scores (com ID)
    public PlayerScore(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.date = "";
        this.replayData = null;
    }

    // Construtor antigo para compatibilidade
    public PlayerScore(String name, int score) {
        this(0, name, score);
    }
}