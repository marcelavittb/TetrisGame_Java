package com.tetris.persistence;

import com.tetris.model.PlayerScore;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class ScoreRepository {
    private static final String SCORE_FILE = "scores.dat";

    public static class ScoreRecord implements Serializable {
        String playerName;
        int score;
        byte[] replayData;

        public ScoreRecord(String playerName, int score, byte[] replayData) {
            this.playerName = playerName;
            this.score = score;
            this.replayData = replayData;
        }
    }

    private static List<ScoreRecord> records = loadScores();

    @SuppressWarnings("unchecked")
    private static List<ScoreRecord> loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORE_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<ScoreRecord>) obj;
            }
        } catch (Exception e) {
            // Normal na primeira execução quando arquivo não existe ainda
        }
        return new ArrayList<>();
    }

    private static void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveScore(String playerName, int score, byte[] replayData) {
        ScoreRecord sr = new ScoreRecord(playerName, score, replayData);
        records.add(sr);
        saveScores();
    }

    public static List<PlayerScore> getTopScores(int n) {
        List<PlayerScore> top = new ArrayList<>();
        records.sort(Comparator.comparingInt(r -> -r.score));
        int limit = Math.min(records.size(), n);
        for (int i = 0; i < limit; i++) {
            ScoreRecord sr = records.get(i);
            // Usa construtor simplificado para não dar erro de parâmetros
            top.add(new PlayerScore(sr.playerName, sr.score));
        }
        return top;
    }

    public static byte[] getReplayData(int scoreIndex) {
        if (scoreIndex >= 0 && scoreIndex < records.size()) {
            return records.get(scoreIndex).replayData;
        }
        return null;
    }
}