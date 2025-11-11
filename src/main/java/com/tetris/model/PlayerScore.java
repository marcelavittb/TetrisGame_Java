package com.tetris.model;

import java.io.Serializable;

/**
 * PlayerScore simples e compatível com o repositório.
 */
public class PlayerScore implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;  // ID único (1-based no getTopScores)
    public String name;
    public int score;
    public String date;
    public byte[] replayData;

    public PlayerScore(int id, String name, int score, String date, byte[] replayData) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.date = date;
        this.replayData = replayData;
    }

    public PlayerScore(int id, String name, int score) {
        this(id, name, score, "", null);
    }

    public PlayerScore(String name, int score) {
        this(0, name, score, "", null);
    }
}
