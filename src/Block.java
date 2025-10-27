import java.awt.Color;

public class Block extends java.awt.Rectangle {
    public int x,y;
    public static final int SIZE = 30;
    public Color c;

    public Block(Color c){
        this.c = c;
    }
    public void draw (java.awt.Graphics2D g2){
        int margin = 2;
        g2.setColor(c);
        g2.fillRect(x+margin, y+margin, SIZE-(margin*2), SIZE-(margin*2));
    }
    
}
