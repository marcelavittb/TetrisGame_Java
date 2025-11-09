package com.tetris.game;

import com.tetris.model.*;
import com.tetris.persistence.ScoreRepository;
import com.tetris.model.PlayerScore;
import com.tetris.input.KeyHandler;
import com.tetris.ui.ReplayDialog;
import com.tetris.replay.ReplayManager;  // Importe o ReplayManager
import com.tetris.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class PlayManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private JFrame parentFrame;

    public static final int BLOCK_SIZE = 30;
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int GAME_X = 300;
    public static final int GAME_Y = 50;
    public static final int INFO_X = 650;

    private final int WIDTH = BOARD_WIDTH * BLOCK_SIZE;
    private final int HEIGHT = BOARD_HEIGHT * BLOCK_SIZE;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    private Mino currentMino;
    private Mino nextMino;

    public static ArrayList<Block> staticBlocks;

    private int dropInterval = 60;
    private int dropCounter = 0;

    private int level = 1;
    private int lines = 0;
    private int score = 0;
    private boolean gameOver = false;
    private boolean scoreSaved = false;

    private List<GameAction> replayActions = new ArrayList<>();
    private boolean isReplayMode = false;
    private int replayIndex = 0;
    private long frameCount = 0;

    private static final Logger logger = Logger.getLogger(PlayManager.class.getName());

    private String playerName;

    private final int MINO_START_X;
    private final int MINO_START_Y;
    private final int NEXTMINO_X;
    private final int NEXTMINO_Y;

    public PlayManager(String playerName, JFrame parentFrame) {
        this.playerName = playerName;
        this.parentFrame = parentFrame;

        left_x = GAME_X;
        top_y = GAME_Y;
        right_x = left_x + WIDTH;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + BLOCK_SIZE * (BOARD_WIDTH / 2 - 1);
        MINO_START_Y = top_y + BLOCK_SIZE;

        NEXTMINO_X = INFO_X;
        NEXTMINO_Y = top_y + 100;

        currentMino = pickMino();
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);

        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);

        staticBlocks = new ArrayList<>();
    }

    public PlayManager(String playerName) {
        this(playerName, null);
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

        int effectiveDropInterval = dropInterval;

        if (KeyHandler.downPressed) {
            effectiveDropInterval = Math.max(1, dropInterval / 5);
            recordAction(GameAction.DROP);
        }

        if (dropCounter >= effectiveDropInterval) {
            if (!moveMinoDown()) {
                freezeCurrentMino();
                checkDelete();
                spawnNextMino();
            }
            dropCounter = 0;
        }

        if (!isReplayMode) {
            if (KeyHandler.leftPressed) {
                moveMinoLeft();
                recordAction(GameAction.MOVE_LEFT);
                KeyHandler.leftPressed = false;
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
        }
    }

    private void playReplayStep() {
        System.out.println("playReplayStep chamado - replayIndex: " + replayIndex + ", total ações: " + replayActions.size() + ", frameCount: " + frameCount);

        if (replayIndex >= replayActions.size()) {
            System.out.println("Replay finalizado - todas as ações executadas.");
            isReplayMode = false;
            return;
        }

        GameAction action = replayActions.get(replayIndex);
        if (frameCount >= action.frameNumber) {
            System.out.println("Executando ação: " + action.actionType + " no frame " + frameCount + " (replayIndex: " + replayIndex + ")");
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
                    moveMinoDown();
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
        System.out.println("Ação gravada: " + actionType + " no frame " + frameCount + ". Total ações: " + replayActions.size());
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
        int linesCleared = 0;
        for (int y = bottom_y - BLOCK_SIZE; y >= top_y; y -= BLOCK_SIZE) {
            boolean fullLine = true;
            for (int x = left_x; x < right_x; x += BLOCK_SIZE) {
                boolean found = false;
                for (Block b : staticBlocks) {
                    if (b.x == x && b.y == y) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                removeLine(y);
                linesCleared++;
                lines++;
                y += BLOCK_SIZE;
            }
        }

        if (linesCleared > 0) {
            score += 100 * linesCleared * level;
            if (lines % 10 == 0 && dropInterval > 10) {
                level++;
                dropInterval -= 5;
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
        // Substitua o salvamento para usar ReplayManager
        ReplayManager.saveReplay(playerName, score, replayActions);
        logger.info("Score e replay salvos via ReplayManager");
    }

    public void resetGame() {
        resetGame(true);  // Limpa replay por padrão
    }

    public void resetGame(boolean clearReplay) {
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

        if (clearReplay) {
            replayActions.clear();
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2.setColor(Color.white);
        g2.drawRect(left_x, top_y, WIDTH, HEIGHT);

        for (Block b : staticBlocks) b.draw(g2);

        if (currentMino != null) currentMino.draw(g2);

        drawNextPiece(g2);

        drawStatusPanel(g2);

        if (gameOver) {
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.setColor(Color.red);
            String text = "GAME OVER";
            FontMetrics fm = g2.getFontMetrics();
            int x = left_x + (WIDTH - fm.stringWidth(text)) / 2;
            int y = top_y + (HEIGHT + fm.getAscent()) / 2;
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

        int pivotX = nextMino.b[0].x;
        int pivotY = nextMino.b[0].y;

        int minRelX = Integer.MAX_VALUE, minRelY = Integer.MAX_VALUE;
        int maxRelX = Integer.MIN_VALUE, maxRelY = Integer.MIN_VALUE;

        int[][] relBlocks = new int[4][2];
        for (int i = 0; i < 4; i++) {
            int relX = (nextMino.b[i].x - pivotX) / BLOCK_SIZE;
            int relY = (nextMino.b[i].y - pivotY) / BLOCK_SIZE;
            relBlocks[i][0] = relX;
            relBlocks[i][1] = relY;
            if (relX < minRelX) minRelX = relX;
            if (relY < minRelY) minRelY = relY;
            if (relX > maxRelX) maxRelX = relX;
            if (relY > maxRelY) maxRelY = relY;
        }

        int relWidth = maxRelX - minRelX + 1;
        int relHeight = maxRelY - minRelY + 1;

        int offsetX = centerX - (relWidth * BLOCK_SIZE) / 2;
        int offsetY = centerY - (relHeight * BLOCK_SIZE) / 2;

        for (int i = 0; i < 4; i++) {
            int x = offsetX + (relBlocks[i][0] - minRelX) * BLOCK_SIZE;
            int y = offsetY + (relBlocks[i][1] - minRelY) * BLOCK_SIZE;
            g2.setColor(nextMino.b[i].c);
            g2.fillRect(x, y, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
            g2.setColor(Color.black);
            g2.drawRect(x, y, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        }
    }

    private void drawStatusPanel(Graphics2D g2) {
        int y = top_y + 30;
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("LEVEL: " + level, INFO_X, y);
        y += 30;
        g2.drawString("LINES: " + lines, INFO_X, y);
        y += 30;
        g2.drawString("SCORE: " + score, INFO_X, y);

        y += 70;
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        g2.drawString("NEXT", INFO_X, y);
        y += 10;

        int size = BLOCK_SIZE * 4;
        g2.drawRect(INFO_X, y, size, size);

        y += size + 40;
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        g2.drawString("Top Scores:", INFO_X, y);
        y += 25;

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

    // Método para carregar replay a partir de ações (usado pelo ReplayManager)
    public void playReplayFromActions(List<GameAction> actions) {
        System.out.println("Carregando replay a partir de ações...");
        replayActions = new ArrayList<>(actions);  // Copia as ações
        // Reset manual do estado do jogo, SEM limpar replayActions
        score = 0;
        lines = 0;
        level = 1;
        dropInterval = 60;
        dropCounter = 0;
        gameOver = false;
        scoreSaved = false;
        staticBlocks.clear();
        currentMino = pickMino();
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);
        isReplayMode = true;
        replayIndex = 0;
        frameCount = 0;
        System.out.println("Modo replay ativado com " + actions.size() + " ações.");
    }

    public void openReplayDialog(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            if (parent != null) {
                new ReplayDialog(parent, this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Não foi possível abrir o diálogo de replay. Janela pai não definida.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void openReplayDialog() {
        openReplayDialog(parentFrame);
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
                    openReplayDialog();
                    break;
                case 2:
                    System.exit(0);
                    break;
            }
        });
    }
}
