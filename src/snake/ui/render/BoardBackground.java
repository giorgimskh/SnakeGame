package snake.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import snake.model.Board;

/**
 * Builds a level's static board image: a flat two-tone checkerboard, one tile per grid cell, with
 * the biome's decorations on top. It is rendered once per level and drawn every frame.
 */
public final class BoardBackground {
    private BoardBackground() {
    }

    public static BufferedImage create(Color tileA, Color tileB, Decorations.Decorator decorations,
                                       Decorations.CellPicker cells) {
        BufferedImage image = new BufferedImage(Board.WIDTH, Board.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        for (int c = 0; c < Board.COLUMNS; c++) {
            for (int r = 0; r < Board.ROWS; r++) {
                g.setColor((c + r) % 2 == 0 ? tileA : tileB);
                g.fillRect(c * Board.UNIT, r * Board.UNIT, Board.UNIT, Board.UNIT);
            }
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        decorations.paint(g, cells);
        g.dispose();
        return image;
    }
}
