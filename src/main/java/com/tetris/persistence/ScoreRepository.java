package com.tetris.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tetris.model.PlayerScore;

/**
 * ScoreRepository compatível com versões antigas.
 * - Mantém serialVersionUID compatível com arquivos antigos para permitir leitura.
 * - Em caso de erro irreversível, renomeia o arquivo antigo para scores.dat.bak e retorna lista vazia.
 */
public class ScoreRepository {
    private static final String SCORE_FILE = "scores.dat";

    public static class ScoreRecord implements Serializable {
        // Serial UID compatível com arquivos gravados anteriormente no seu projeto
        private static final long serialVersionUID = -3412062713148606929L;

        String playerName;
        int score;
        byte[] replayData;
        long timestamp;

        public ScoreRecord(String playerName, int score, byte[] replayData) {
            this.playerName = playerName;
            this.score = score;
            this.replayData = replayData;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static List<ScoreRecord> records = loadScores();

    @SuppressWarnings("unchecked")
    private static List<ScoreRecord> loadScores() {
        File f = new File(SCORE_FILE);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> raw = (List<?>) obj;
                List<ScoreRecord> out = new ArrayList<>();
                for (Object o : raw) {
                    if (o instanceof ScoreRecord) out.add((ScoreRecord) o);
                }
                return out;
            }
        } catch (InvalidClassException ice) {
            System.err.println("Incompatibilidade de classe ao ler " + SCORE_FILE + ": " + ice.getMessage());
            try {
                File bak = new File(SCORE_FILE + ".bak");
                if (bak.exists()) bak.delete();
                boolean renamed = new File(SCORE_FILE).renameTo(bak);
                System.err.println("Arquivo antigo renomeado para: " + bak.getAbsolutePath() + "  (renomeado=" + renamed + ")");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File bak = new File(SCORE_FILE + ".bak");
                if (bak.exists()) bak.delete();
                boolean renamed = new File(SCORE_FILE).renameTo(bak);
                System.err.println("Problema ao ler scores.dat — arquivo renomeado para: " + bak.getAbsolutePath() + " (renomeado=" + renamed + ")");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private static synchronized void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void saveScore(String playerName, int score, byte[] replayData) {
        ScoreRecord sr = new ScoreRecord(playerName, score, replayData);
        records.add(sr);
        saveScores();
    }

    public static synchronized void saveScore(PlayerScore ps) {
        if (ps == null) return;
        byte[] data = ps.replayData;
        ScoreRecord sr = new ScoreRecord(ps.name, ps.score, data);
        records.add(sr);
        saveScores();
    }

    public static synchronized List<PlayerScore> getTopScores(int n) {
        if (records == null) records = new ArrayList<>();
        List<ScoreRecord> copy = new ArrayList<>(records);
        Collections.sort(copy, (a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) return cmp;
            return Long.compare(b.timestamp, a.timestamp);
        });

        List<PlayerScore> out = new ArrayList<>();
        int limit = Math.min(n, copy.size());
        for (int i = 0; i < limit; i++) {
            ScoreRecord sr = copy.get(i);
            out.add(new PlayerScore(i + 1, sr.playerName, sr.score));
        }
        return out;
    }

    public static synchronized byte[] getReplayData(int scoreIndex) {
        if (scoreIndex >= 0 && scoreIndex < records.size()) {
            return records.get(scoreIndex).replayData;
        }
        return null;
    }

    public static synchronized byte[] getReplayDataByScore(String name, int score) {
        for (ScoreRecord sr : records) {
            if (sr.playerName.equals(name) && sr.score == score) {
                return sr.replayData;
            }
        }
        return null;
    }

    public static synchronized void clearAll() {
        records.clear();
        saveScores();
    }
}
