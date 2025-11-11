package com.tetris.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tetris.model.GameAction;

public class ReplayManager {
    private static final Logger logger = Logger.getLogger(ReplayManager.class.getName());
    private static final String REPLAY_DIR = "replays";

    // Salva ReplayData (seed + score + ações)
    public static void saveReplay(String playerName, int score, long seed, List<GameAction> actions) {
        File dir = new File(REPLAY_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("Diretório criado: " + created);
        }

        // use timestamp to avoid sobrescrever replays iguais por score
        String filename = String.format("%s/%s_%d_%d.replay", REPLAY_DIR, playerName, score, System.currentTimeMillis());
        System.out.println("Tentando salvar replay em: " + filename);
        ReplayData data = new ReplayData(seed, score, actions);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            logger.log(Level.INFO, "Replay salvo: {0}", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar replay: {0}", e.getMessage());
        }
    }

    // Carrega e retorna ReplayData. Se for arquivo antigo contendo apenas List<GameAction>,
    // retorna ReplayData com seed = 0 (não determinístico).
    @SuppressWarnings("unchecked")
    public static ReplayData loadReplay(String filename) {
        System.out.println("Tentando carregar replay de: " + filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof ReplayData) {
                ReplayData data = (ReplayData) obj;
                System.out.println("ReplayData carregado com " + (data.actions != null ? data.actions.size() : 0) + " ações. seed=" + data.seed);
                return data;
            } else if (obj instanceof List) {
                List<GameAction> actions = (List<GameAction>) obj;
                System.out.println("Arquivo de replay em formato antigo (somente ações). Ações: " + actions.size());
                // Seed desconhecida -> 0 (replay pode não ser idêntico)
                return new ReplayData(0L, 0, actions);
            } else {
                logger.log(Level.SEVERE, "Formato de replay desconhecido: {0}", obj.getClass().getName());
                return new ReplayData(0L, 0, new ArrayList<>());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar replay: {0}", e.getMessage());
            return new ReplayData(0L, 0, new ArrayList<>());
        }
    }

    // Retorna lista de arquivos de replay (caminhos)
    public static File[] listReplayFiles() {
        File dir = new File(REPLAY_DIR);
        if (!dir.exists() || !dir.isDirectory()) return new File[0];
        return dir.listFiles((d, name) -> name.toLowerCase().endsWith(".replay"));
    }
}
