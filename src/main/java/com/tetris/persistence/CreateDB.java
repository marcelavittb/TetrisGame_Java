package com.tetris.persistence;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

public class CreateDB {
    private static final Logger LOGGER = Logger.getLogger(CreateDB.class.getName());

    public static void main(String[] args) {
        try {
            // carrega a classe para disparar o bloco static do ScoreRepository
            Class<?> repo = Class.forName("com.tetris.persistence.ScoreRepository");
            LOGGER.info("ScoreRepository class carregada.");

            // Tenta ler o campo DB_PATH (se existir) para mostrar o caminho exato
            try {
                Field f = repo.getDeclaredField("DB_PATH");
                f.setAccessible(true);
                Object val = f.get(null);
                if (val instanceof String) {
                    String dbPath = (String) val;
                    File dbFile = new File(dbPath);
                    LOGGER.info("DB path (ScoreRepository.DB_PATH) = " + dbPath);
                    LOGGER.info("DB file exists: " + dbFile.exists() + " -> " + dbFile.getAbsolutePath());
                }
            } catch (NoSuchFieldException nsf) {
                // campo não encontrado — informa diretório de trabalho
                LOGGER.info("ScoreRepository não expõe DB_PATH. diretório de trabalho: " +
                        new File(".").getAbsolutePath());
            }

            LOGGER.info("CreateDB finalizado. Se ScoreRepository inicializa o DB, ele já foi criado.");
        } catch (Throwable t) {
            LOGGER.severe("Failed to create database: " + t.getMessage());
            t.printStackTrace();
        }
    }
}