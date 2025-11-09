package com.tetris.game;

import com.tetris.model.*;
import com.tetris.persistence.ScoreRepository;
import com.tetris.model.PlayerScore;
import com.tetris.input.KeyHandler;
import com.tetris.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class PlayManager {

    private static final int GAME_X = 300;
    private static final int GAME_Y = 50;
    private static final int INFO_X = 650;
    private static final int BLOCK_SIZE = Block.SIZE;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    final int WIDTH = BOARD_WIDTH * BLOCK_SIZE;
    final int HEIGHT = BOARD_HEIGHT * BLOCK_SIZE;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;

    public static ArrayList<Block> staticBlocks;

    public static int dropInterval = 60;
    private int dropCounter = 0;

    public boolean gameOver;

    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    int level = 1;
    int lines;
    int score;
    boolean scoreSaved = false;
    String playerName = "Player";

    private List<GameAction> replayActions = new ArrayList<>();
    private boolean isReplayMode = false;
    private int replayIndex = 0;
    private long frameCount = 0;

    private static final Logger logger = Logger.getLogger(PlayManager.class.getName());

    public PlayManager(String playerName) {
        this.playerName = playerName;

        left_x = GAME_X;
        top_y = GAME_Y;
        right_x = left_x + WIDTH;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + BLOCK_SIZE * (BOARD_WIDTH / 2 - 1);
        MINO_START_Y = top_y + BLOCK_SIZE;

        NEXTMINO_X = INFO_X;
        NEXTMINO_Y = top_y + 120;

        currentMino = pickMino();
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);

        staticBlocks = new ArrayList<>();
    }

    private Mino pickMino() {
        int i = new Random().nextInt(7) + 1;
        switch (i) {
            case 1: return new Mino_L1();
            case 2: return new Mino_L2();
            case 3: return new Mino_Square();
            case 4: return new Mino_Bar();
            case 5: return new Mino_T();
            case 6: return new Mino_Z1();
            case 7: return new Mino_Z2();
            default: return new Mino_Square();
        }
    }

    public void update() {
        frameCount++;

        if (gameOver) {
            if (!scoreSaved) {
                saveGameData();
                scoreSaved = true;
                showGameOverOptions();
            }
            return;
        }

        if (isReplayMode) {
            playReplayStep();
            return;
        }

        dropCounter++;
        if (dropCounter >= dropInterval) {
            if (!moveMinoDown()) {
                freezeCurrentMino();
                checkDelete();
                spawnNextMino();
            }
            dropCounter = 0;
        }

        // Process inputs (only if not replay mode)
        if (KeyHandler.leftPressed) {
            moveMinoLeft();
            recordAction(GameAction.MOVE_LEFT);
            KeyHandler.leftPressed = false;  // To avoid multiple moves per frame
        }
        if (KeyHandler.rightPressed) {
            moveMinoRight();
            recordAction(GameAction.MOVE_RIGHT);
            KeyHandler.rightPressed = false;
        }
        if (KeyHandler.upPressed) {
            rotateMino();
            recordAction(GameAction.ROTATE);
            KeyHandler.upPressed = false;
        }
        if (KeyHandler.downPressed) {
            while (moveMinoDown()) { // drop all the way
                score += 1; // bonus por drop rápido
            }
            recordAction(GameAction.DROP);
            KeyHandler.downPressed = false;
        }
    }

    private void playReplayStep() {
        if (replayIndex >= replayActions.size()) {
            // replay acabou
            isReplayMode = false;
            logger.info("Replay finalizado");
            return;
        }
        GameAction action = replayActions.get(replayIndex);
        if (frameCount >= action.frameNumber) {
            switch (action.actionType) {
                case GameAction.MOVE_LEFT:
                    moveMinoLeft();
                    break;
                case GameAction.MOVE_RIGHT:
                    moveMinoRight();
                    break;
                case GameAction.ROTATE:
                    rotateMino();
                    break;
                case GameAction.DROP:
                    while (moveMinoDown()) {
                        score += 1;
                    }
                    break;
            }
            replayIndex++;
        }
        dropCounter++;
        if (dropCounter >= dropInterval) {
            if (!moveMinoDown()) {
                freezeCurrentMino();
                checkDelete();
                spawnNextMino();
            }
            dropCounter = 0;
        }
    }

    private void recordAction(int actionType) {
        replayActions.add(new GameAction(actionType, frameCount));
    }

    private boolean moveMinoDown() {
        currentMino.move(0, BLOCK_SIZE);
        if (detectCollision()) {
            currentMino.move(0, -BLOCK_SIZE);
            return false;
        }
        return true;
    }

    private void moveMinoLeft() {
        currentMino.move(-BLOCK_SIZE, 0);
        if (detectCollision()) {
            currentMino.move(BLOCK_SIZE, 0);
        }
    }

    private void moveMinoRight() {
        currentMino.move(BLOCK_SIZE, 0);
        if (detectCollision()) {
            currentMino.move(-BLOCK_SIZE, 0);
        }
    }

    private void rotateMino() {
        currentMino.rotate();
        if (detectCollision()) {
            currentMino.rotateBack();
        }
    }

    private boolean detectCollision() {
        for (Block b : currentMino.b) {
            if (b.x < left_x || b.x >= right_x || b.y >= bottom_y) return true;
            for (Block st : staticBlocks) {
                if (b.x == st.x && b.y == st.y) return true;
            }
        }
        return false;
    }

    private void freezeCurrentMino() {
        for (Block b : currentMino.b) {
            staticBlocks.add(new Block(b.x, b.y, b.c));
        }
    }

    private void checkDelete() {
        for (int y = bottom_y - BLOCK_SIZE; y >= top_y; y -= BLOCK_SIZE) {
            int count = 0;
            for (int x = left_x; x < right_x; x += BLOCK_SIZE) {
                boolean found = false;
                for (Block b : staticBlocks) {
                    if (b.x == x && b.y == y) {
                        found = true;
                        count++;
                        break;
                    }
                }
                if (!found) break;
            }
            if (count == BOARD_WIDTH) {
                removeLine(y);
                lines++;
                score += 100 * level;
                if (lines % 10 == 0 && dropInterval > 10) {
                    level++;
                    dropInterval -= 5;
                }
                y += BLOCK_SIZE;
            }
        }
    }

    private void removeLine(int y) {
        staticBlocks.removeIf(b -> b.y == y);
        for (Block b : staticBlocks) {
            if (b.y < y) b.y += BLOCK_SIZE;
        }
    }

    private void spawnNextMino() {
        currentMino = nextMino;
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);

        if (detectCollision()) {
            gameOver = true;
        }
    }

    private void saveGameData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(replayActions);
            oos.close();

            byte[] replayData = baos.toByteArray();

            ScoreRepository.saveScore(playerName, score, replayData);
            logger.info("Score e replay salvos com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Erro salvando replay: " + e.getMessage());
        }
    }

    public void resetGame() {
        score = 0;
        lines = 0;
        level = 1;
        dropInterval = 60;
        dropCounter = 0;
        gameOver = false;
        scoreSaved = false;
        isReplayMode = false;
        replayIndex = 0;
        frameCount = 0;

        staticBlocks.clear();
        currentMino = pickMino();
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);
        replayActions.clear();
    }

    public void draw(Graphics2D g2) {
        // Fundo preto
        g2.setColor(Color.black);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // Borda principal
        g2.setColor(Color.white);
        g2.drawRect(left_x, top_y, WIDTH, HEIGHT);

        // Blocos fixos
        for (Block b : staticBlocks) b.draw(g2);

        // Minos atual e próximo
        if (currentMino != null) currentMino.draw(g2);
        drawNextPiece(g2);

        // Status e scores
        drawStatusPanel(g2);

        // Game Over message
        if (gameOver) {
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.setColor(Color.red);
            String text = "GAME OVER";
            FontMetrics fm = g2.getFontMetrics();
            int x = left_x + (WIDTH - fm.stringWidth(text)) / 2;
            int y = top_y + HEIGHT / 2;
            g2.drawString(text, x, y);
        }
    }

    private void drawNextPiece(Graphics2D g2) {
        int boxX = NEXTMINO_X;
        int boxY = NEXTMINO_Y;
        int boxSize = BLOCK_SIZE * 4;
        g2.setColor(Color.white);
        g2.drawRect(boxX, boxY, boxSize, boxSize);

        if (nextMino == null) return;

        int centerX = boxX + boxSize / 2;
        int centerY = boxY + boxSize / 2;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        for (Block b : nextMino.b) {
            if (b.x < minX) minX = b.x;
            if (b.y < minY) minY = b.y;
        }

        for (Block b : nextMino.b) {
            int px = centerX + (b.x - minX - 1) * BLOCK_SIZE - BLOCK_SIZE / 2;
            int py = centerY + (b.y - minY - 1) * BLOCK_SIZE - BLOCK_SIZE / 2;
            g2.setColor(b.c);
            g2.fillRect(px, py, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
            g2.setColor(Color.black);
            g2.drawRect(px, py, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        }
    }

    private void drawStatusPanel(Graphics2D g2) {
        int y = top_y + 40;
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("LEVEL: " + level, INFO_X, y);
        y += 30;
        g2.drawString("LINES: " + lines, INFO_X, y);
        y += 30;
        g2.drawString("SCORE: " + score, INFO_X, y);
        
        y += 110; // espaço reservado para NEXT box
        g2.setFont(new Font("Arial", Font.ITALIC, 20));
        g2.drawString("Top Scores:", INFO_X, y);
        y += 30;

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        List<PlayerScore> top = ScoreRepository.getTopScores(5);
        if (top.isEmpty()) {
            g2.drawString("Nenhum score salvo.", INFO_X, y);
        } else {
            for (PlayerScore ps : top) {
                g2.drawString(ps.name + " - " + ps.score, INFO_X, y);
                y += 22;
            }
        }
    }

    public void playReplayFromScore(int scoreIndex) {
        byte[] replayData = ScoreRepository.getReplayData(scoreIndex);
        if (replayData == null) return;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(replayData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            replayActions = (List<GameAction>) ois.readObject();
            ois.close();

            resetGame();
            isReplayMode = true;
            replayIndex = 0;
            frameCount = 0;
            logger.info("Replay iniciado");
        } catch (Exception ex) {
            logger.severe("Falha ao carregar replay: " + ex.getMessage());
        }
    }

    private void showGameOverOptions() {
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Recomeçar", "Ver Replays", "Sair"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Pontuação Final: " + score + "\nJogador: " + playerName,
                    "Game Over",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            switch (choice) {
                case 0:
                    resetGame();
                    break;
                case 1:
                    // implementar diálogo de replay aqui (se já existir)
                    break;
                case 2:
                    System.exit(0);
                    break;
            }
        });
    }
}