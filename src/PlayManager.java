import java.awt.Color;
import java.awt.Font;

public class PlayManager {
    //Main Play Area

    final int SCREEN_WIDTH = 360;
    final int SCREEN_HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;


    public PlayManager() {
        left_x = (GamePanel.SCREEN_WIDTH/2) - (SCREEN_WIDTH/2);
        right_x = left_x + SCREEN_WIDTH;
        top_y = 50;
        bottom_y = top_y + SCREEN_HEIGHT;
    }

    public void update() {
        // Update game state
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
        
    }

}