import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

public final class PlayManager {
    //Main Play Area

    final int SCREEN_WIDTH = 360;
    final int SCREEN_HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino

    Mino currentMino;
    final int Mino_START_X;
    final int Mino_START_Y;
    Mino nextMino;
    int NEXT_MINO_X = 0;
    int NEXT_MINO_Y = 0;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    //Others
    public static int dropInterval = 60;
    boolean gameOver;

    //Efects
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer>effectY = new ArrayList<>();


    public PlayManager() {
        left_x = (GamePanel.SCREEN_WIDTH/2) - (SCREEN_WIDTH/2);
        right_x = left_x + SCREEN_WIDTH;
        top_y = 50;
        bottom_y = top_y + SCREEN_HEIGHT;

        Mino_START_X = left_x + (SCREEN_WIDTH / 2) - Block.SIZE;
        Mino_START_Y = top_y + Block.SIZE ;

        NEXT_MINO_X = right_x + 175;
        NEXT_MINO_Y = top_y + 500;
     //Set the starting position of the Mino
        currentMino = pickMino();
        currentMino.setXY(Mino_START_X, Mino_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
    
    }
    public Mino pickMino(){
        Mino mino = null;
        int i = new Random().nextInt(7);
        switch(i){
            case 0 -> mino = new Mino_L1();
            case 1 -> mino = new Mino_L2();
            case 2 -> mino = new Mino_T();
            case 3 -> mino = new Mino_Bar();
            case 4 -> mino = new Mino_Square();
            case 5 -> mino = new Mino_Z1();
            case 6 -> mino = new Mino_Z2();
        }
        return mino;
    }



    public void update() {
        //Check if current Mino is active
        if(currentMino.active == false){
            //if not, put into static blocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);
             //Check for game over
             if(currentMino.b[0].x ==Mino_START_X && currentMino.b[0].y ==Mino_START_Y){
               gameOver = true;
             }

            currentMino.deactivating = false;

            // When Mino becomes inactive, check for line deletion
            CheckDelete();

            //set next Mino as current
            currentMino = nextMino;
            currentMino.setXY(Mino_START_X, Mino_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

            
        }
        currentMino.update();
    }

private void CheckDelete() {
    int cols = SCREEN_WIDTH / Block.SIZE;           // número de colunas (deveria ser 12)
    int y = bottom_y - Block.SIZE;                 // começar pela linha mais abaixo possível

    while (y >= top_y) {
        int blockCount = 0;
        // Conta quantos blocos existem nessa linha y
        for (int i = 0; i < staticBlocks.size(); i++) {
            if (staticBlocks.get(i).y == y) {
                blockCount++;
            }
        }

        if (blockCount == cols) {

            effectCounterOn = true;
            effectY.add(y);
            for (int i = staticBlocks.size() - 1; i >= 0; i--) {
                if (staticBlocks.get(i).y == y) {
                    staticBlocks.remove(i);
                }
            }

            // Move para baixo (y maior) todos os blocos que estavam acima dessa linha
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).y < y) {
                    staticBlocks.get(i).y += Block.SIZE;
                }
            }

            } else {
            y -= Block.SIZE;
        }
    }
}

    public void draw(java.awt.Graphics2D g2) {
        // Draw play area
        g2.setColor(Color.WHITE);
        g2.setStroke(new java.awt.BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, SCREEN_WIDTH+8, SCREEN_HEIGHT+8);

        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);
        // Draw current Mino
        if (currentMino != null) {
            currentMino.draw(g2);
        }
        //Draw next Mino
         nextMino.draw(g2);

         //Draw static blocks
         for (int i = 0; i < staticBlocks.size(); i++) {
             staticBlocks.get(i).draw(g2);
         }  

        //Draw line deletion effect
        if (effectCounterOn) {
           effectCounter++;
            
           g2.setColor(Color.red);
           for (int i = 0; i < effectY.size(); i++) {
               g2.fillRect(left_x, effectY.get(i), SCREEN_WIDTH, Block.SIZE);
           }

           if(effectCounter == 10){
            effectCounterOn = false;
            effectCounter = 0;
            effectY.clear();

           }
        }

        //Draw pause
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(50f));
        //Draw game over
        if (gameOver){
            x =left_x + 25;
            y = top_y + SCREEN_HEIGHT / 2;
            g2.drawString("GAME OVER", x , y);
        }
        else if(KeyHandler.pausePressed){
            g2.drawString("PAUSADO!", left_x + 45 , top_y + SCREEN_HEIGHT / 2);
            //Draw the game tittle
            
        }

        //Draw instructions
        g2.setFont(g2.getFont().deriveFont(20f));
        g2.drawString("Teclas:", right_x + 100, top_y + 50);
        g2.drawString("Seta Esquerda: Mover Esquerda", right_x + 100, top_y + 80);
        g2.drawString("Seta Direita: Mover Direita", right_x + 100, top_y + 110);
        g2.drawString("Seta Cima: Rotacionar", right_x + 100, top_y + 140);
        g2.drawString("Seta Baixo: Acelerar Queda", right_x + 100, top_y + 170);
        g2.drawString("Espaço: Pausar", right_x + 100, top_y + 200); 
    
        x = 35;
        y= top_y + 320;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("TETRIS", x , y);

    }
    

}