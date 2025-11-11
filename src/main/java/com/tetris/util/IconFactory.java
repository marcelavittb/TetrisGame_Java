package com.tetris.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * IconFactory - gera ImageIcons simples (tetrominó) para usar em diálogos.
 */
public class IconFactory {

    public static ImageIcon getTetrominoIcon(Color color, int size) {
        int s = Math.max(24, size);
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0,0,0,0));
            g.fillRect(0,0,s,s);

            int pad = s/12;
            int block = (s - pad*2) / 4;
            int cx = pad + block;
            int cy = pad + block;

            g.setColor(color);
            g.fillRoundRect(cx, cy, block, block, 6,6);
            g.fillRoundRect(cx-block, cy, block, block, 6,6);
            g.fillRoundRect(cx+block, cy, block, block, 6,6);
            g.fillRoundRect(cx, cy-block, block, block, 6,6);

            g.setColor(new Color(0,0,0,90));
            g.setStroke(new BasicStroke(Math.max(1, block/8)));
            g.drawRoundRect(cx, cy, block, block, 6,6);
            g.drawRoundRect(cx-block, cy, block, block, 6,6);
            g.drawRoundRect(cx+block, cy, block, block, 6,6);
            g.drawRoundRect(cx, cy-block, block, block, 6,6);
        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }

    public static ImageIcon defaultDialogIcon() {
        return getTetrominoIcon(new Color(70,160,255), 48);
    }
}
