package com.tetris.replay;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.tetris.model.GameAction;
import java.util.logging.Level;

public class ReplayManager {
    private static final Logger logger = Logger.getLogger(ReplayManager.class.getName());
    private static final String REPLAY_DIR = "replays";

    public static void saveReplay(String playerName, int score, List<GameAction> actions) {
        File dir = new File(REPLAY_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("Diretório criado: " + created);
        }

        String filename = String.format("%s/%s_%d.replay", REPLAY_DIR, playerName, score);
        System.out.println("Tentando salvar replay em: " + filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(actions);
            logger.log(Level.INFO, "Replay salvo: {0}", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar replay: {0}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<GameAction> loadReplay(String filename) {
        System.out.println("Tentando carregar replay de: " + filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            List<GameAction> actions = (List<GameAction>) ois.readObject();
            System.out.println("Replay carregado com " + actions.size() + " ações.");
            return actions;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar replay: {0}", e.getMessage());
            return new ArrayList<>();
        }
    }
}