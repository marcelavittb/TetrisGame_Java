package com.tetris.sound;

import java.net.URL;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];

    private static final Logger logger = Logger.getLogger(Sound.class.getName());

    public Sound() {
        logger.fine("Inicializando Sound");
        // recursos devem estar em src/main/resources/res/
        soundURL[0] = getClass().getResource("/res/Tetris.wav");
        soundURL[1] = getClass().getResource("/res/clear.wav");
        soundURL[2] = getClass().getResource("/res/gameover.wav");
        soundURL[3] = getClass().getResource("/res/rotation.wav");
        soundURL[4] = getClass().getResource("/res/delete.wav");
    }

    public void setFile(int i) {
        try {
            if (soundURL[i] == null) {
                logger.severe("Resource not found for sound index: " + i);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            logger.fine("Arquivo de som carregado: " + i);
        } catch (Exception e) {
            logger.severe("Erro ao carregar som: " + e.getMessage());
        }
    }

    public void play(int i, boolean loop) {
        setFile(i);
        if (clip == null) return;
        clip.start();
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void loop(int i) {
        setFile(i);
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            logger.fine("Som parado");
        }
    }
}