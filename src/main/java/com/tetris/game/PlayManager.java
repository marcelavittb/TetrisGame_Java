package com.tetris.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.tetris.input.KeyHandler;
import com.tetris.model.Block;
import com.tetris.model.GameAction;
import com.tetris.model.Mino;
import com.tetris.model.Mino_Bar;
import com.tetris.model.Mino_L1;
import com.tetris.model.Mino_L2;
import com.tetris.model.Mino_Square;
import com.tetris.model.Mino_T;
import com.tetris.model.Mino_Z1;
import com.tetris.model.Mino_Z2;
import com.tetris.model.PlayerScore;
import com.tetris.persistence.ScoreRepository;
import com.tetris.replay.ReplayManager;
import com.tetris.ui.GamePanel;

/**
 * PlayManager completo — lógica do jogo, replay e desenho.
 * Observação: usa GameOverDialog e ReplayDialogEnhanced para diálogos.
 */
public class PlayManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private JFrame parentFrame;

    public static final int BLOCK_SIZE = 30;
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int GAME_X = 140;
    public static final int GAME_Y = 50;
    public static final int INFO_X = 520;

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

    // RNG and seed for deterministic piece generation
    private transient Random rng;
    private long gameSeed = 0L;

    public PlayManager(String playerName, JFrame parentFrame) {
        this.playerName = playerName;
        this.parentFrame = parentFrame;

        left_x = GAME_X;
        top_y = GAME_Y;
        right_x = left_x + WIDTH;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + BLOCK_SIZE * (BOARD_WIDTH / 2 - 1);
        MINO_START_Y = top_y + BLOCK_SIZE;

        NEXTMINO_X = INFO_X + 20;
        NEXTMINO_Y = top_y + 60;

        // Inicializa RNG com seed determinística para permitir replay fiel
        gameSeed = new Random().nextLong();
        rng = new Random(gameSeed);

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
        int i = (rng == null ? new Random().nextInt(7) : rng.nextInt(7)); // 0..6
        switch (i) {
            case 0: return new Mino_L1();
            case 1: return new Mino_L2();
            case 2: return new Mino_Square();
            case 3: return new Mino_Bar();
            case 4: return new Mino_T();
            case 5: return new Mino_Z1();
            case 6: return new Mino_Z2();
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
        if (replayIndex >= replayActions.size()) {
            isReplayMode = false;
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
                    moveMinoDown();
                    break;
                case GameAction.PAUSE:
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
        // Salva replay com seed
        ReplayManager.saveReplay(playerName, score, gameSeed, replayActions);
        logger.info("Replay salvo via ReplayManager (incluindo seed).");

        // Tenta salvar score no ScoreRepository — adapte o método se seu repo tiver outro nome
        try {
            ScoreRepository.saveScore(new PlayerScore(playerName, score));
            logger.info("Score salvo via ScoreRepository: " + playerName + " - " + score);
        } catch (Throwable t) {
            // se o ScoreRepository tiver API diferente, não vamos travar o jogo — me envie o código do repo e eu ajusto
            logger.warning("Não foi possível salvar score automaticamente no ScoreRepository: " + t.getMessage());
        }
    }

    public void resetGame() {
        resetGame(true);
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
        gameSeed = new Random().nextLong();
        rng = new Random(gameSeed);

        currentMino = pickMino();
        currentMino.setInitialPosition(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setInitialPosition(NEXTMINO_X + 30, NEXTMINO_Y + 30);

        if (clearReplay) {
            replayActions.clear();
        }
    }

    public void draw(Graphics2D g2) {
        // --- BACKGROUND ---
        int w = GamePanel.WIDTH;
        int h = GamePanel.HEIGHT;
        GradientPaint gp = new GradientPaint(0, 0, new Color(18, 24, 37), 0, h, new Color(40, 60, 90));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // Board card
        int boardPad = 8;
        int boardX = left_x - boardPad;
        int boardY = top_y - boardPad;
        int boardW = WIDTH + boardPad*2;
        int boardH = HEIGHT + boardPad*2;

        g2.setColor(new Color(0,0,0,100));
        g2.fillRoundRect(boardX+6, boardY+6, boardW, boardH, 16, 16);

        g2.setColor(new Color(12, 17, 28, 230));
        g2.fillRoundRect(boardX, boardY, boardW, boardH, 16, 16);

        g2.setColor(new Color(255,255,255,60));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boardX, boardY, boardW, boardH, 16, 16);

        g2.setColor(new Color(10, 18, 30));
        g2.fillRect(left_x, top_y, WIDTH, HEIGHT);

        // blocks
        for (Block b : staticBlocks) b.draw(g2);
        if (currentMino != null) currentMino.draw(g2);

        // subtle grid
        g2.setColor(new Color(255,255,255,12));
        for (int gx = left_x; gx <= right_x; gx += BLOCK_SIZE) g2.drawLine(gx, top_y, gx, bottom_y);
        for (int gy = top_y; gy <= bottom_y; gy += BLOCK_SIZE) g2.drawLine(left_x, gy, right_x, gy);

        // NEXT card
        int cardW = 160;
        int cardH = 160;
        int cardX = INFO_X;
        int cardY = NEXTMINO_Y - 30;

        g2.setColor(new Color(0,0,0,80));
        g2.fillRoundRect(cardX+4, cardY+6, cardW, cardH, 14, 14);

        g2.setColor(new Color(20, 28, 44, 220));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        g2.setColor(new Color(255,255,255,70));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("NEXT", cardX + 16, cardY + 28);

        if (nextMino != null) {
            int centerX = cardX + cardW/2;
            int centerY = cardY + cardH/2 + 8;

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

            int blockDrawSize = BLOCK_SIZE - 6;
            int offsetX = centerX - (relWidth * blockDrawSize) / 2;
            int offsetY = centerY - (relHeight * blockDrawSize) / 2;

            for (int i = 0; i < 4; i++) {
                int x = offsetX + (relBlocks[i][0] - minRelX) * blockDrawSize;
                int y = offsetY + (relBlocks[i][1] - minRelY) * blockDrawSize;
                g2.setColor(nextMino.b[i].c);
                g2.fillRoundRect(x, y, blockDrawSize, blockDrawSize, 6, 6);
                g2.setColor(new Color(0,0,0,60));
                g2.drawRoundRect(x, y, blockDrawSize, blockDrawSize, 6, 6);
            }
        }

        // STATUS panel
        int statusX = INFO_X;
        int statusY = cardY + cardH + 18;
        int statusW = cardW;
        int statusH = 180;

        g2.setColor(new Color(0,0,0,60));
        g2.fillRoundRect(statusX+4, statusY+6, statusW, statusH, 14, 14);

        g2.setColor(new Color(20, 28, 44, 220));
        g2.fillRoundRect(statusX, statusY, statusW, statusH, 14, 14);

        g2.setColor(new Color(255,255,255,70));
        g2.drawRoundRect(statusX, statusY, statusW, statusH, 14, 14);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("SCORE", statusX + 16, statusY + 30);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.drawString(String.valueOf(score), statusX + 16, statusY + 62);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString("LEVEL: " + level, statusX + 16, statusY + 96);
        g2.drawString("LINES: " + lines, statusX + 16, statusY + 120);

        // top scores alinhados (monospace)
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        int ty = statusY + 148;
        List<PlayerScore> top = null;
        try {
            top = ScoreRepository.getTopScores(5);
        } catch (Throwable t) {
            logger.warning("Erro ao obter top scores: " + t.getMessage());
        }
        if (top == null || top.isEmpty()) {
            g2.drawString("Nenhum score salvo.", statusX + 16, ty);
        } else {
            for (PlayerScore ps : top) {
                String name = ps.name == null ? "anon" : ps.name;
                int s = ps.score;
                if (name.length() > 12) name = name.substring(0, 12);
                String line = String.format("%-12s %6d", name, s);
                g2.drawString(line, statusX + 16, ty);
                ty += 18;
            }
        }

        // overlay GAME OVER
        if (gameOver) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g2.setColor(new Color(8, 10, 15));
            g2.fillRoundRect(left_x + 20, top_y + (HEIGHT/2) - 60, WIDTH - 40, 120, 12, 12);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
            g2.setColor(new Color(255,80,80));
            String text = "GAME OVER";
            FontMetrics fm = g2.getFontMetrics();
            int tx = left_x + (WIDTH - fm.stringWidth(text)) / 2;
            int ty2 = top_y + (HEIGHT/2);
            g2.drawString(text, tx, ty2);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            String sub = "Pressione 'R' para recomeçar ou abra Replays";
            FontMetrics fm2 = g2.getFontMetrics();
            int sx = left_x + (WIDTH - fm2.stringWidth(sub)) / 2;
            g2.drawString(sub, sx, ty2 + 28);
        }
    }

    // Mantém assinatura antiga e adiciona versão com seed
    public void playReplayFromActions(List<GameAction> actions) {
        playReplayFromActions(actions, 0L);
    }

    public void playReplayFromActions(List<GameAction> actions, long seed) {
        System.out.println("Carregando replay a partir de ações...");
        replayActions = new ArrayList<>();
        for (GameAction ga : actions) {
            replayActions.add(new GameAction(ga.actionType, ga.frameNumber));
        }

        // Normaliza frameNumbers
        if (!replayActions.isEmpty()) {
            long base = replayActions.get(0).frameNumber;
            if (base != 0L) {
                System.out.println("Normalizando replay: subtraindo baseFrame = " + base);
                for (GameAction ga : replayActions) {
                    ga.frameNumber = Math.max(0L, ga.frameNumber - base);
                }
            }
        }

        if (seed != 0L) {
            this.gameSeed = seed;
            this.rng = new Random(gameSeed);
        } else {
            this.gameSeed = 0L;
            this.rng = new Random();
        }

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
        System.out.println("Modo replay ativado com " + replayActions.size() + " ações. seed=" + this.gameSeed);
    }

    // Abre diálogo de replays melhorado
    public void openReplayDialog(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            com.tetris.ui.ReplayDialogEnhanced.open(parent != null ? parent : parentFrame, this);
        });
    }

    public void openReplayDialog() {
        openReplayDialog(parentFrame);
    }

    // usa GameOverDialog estilizado
    private void showGameOverOptions() {
        SwingUtilities.invokeLater(() -> {
            try {
                com.tetris.ui.GameOverDialog.showFor(parentFrame, this, score);
            } catch (Exception ex) {
                ex.printStackTrace();
                Object[] options = {"Recomeçar", "Ver Replays", "Sair"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Pontuação Final: " + score + "\nJogador: " + playerName,
                        "Game Over",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (choice == 0) resetGame();
                else if (choice == 1) openReplayDialog();
                else if (choice == 2) System.exit(0);
            }
        });
    }
}
