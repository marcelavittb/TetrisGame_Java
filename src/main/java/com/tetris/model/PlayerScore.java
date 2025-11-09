package com.tetris.model;

import java.io.Serializable;

public class PlayerScore implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;
    public String name;
    public int score;
    public String date;
    public byte[] replayData;

    // Construtor completo existente
    public PlayerScore(int id, String name, int score, String date, byte[] replayData) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.date = date;
        this.replayData = replayData;
    }

    // Construtor simplificado para exibir top7 no painel (resolve o seu erro)
    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
        this.id = 0;
        this.date = "";
        this.replayData = null;
    }
}