package snake.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import snake.model.Board;

/**
 * Builds a level's static board image: a flat two-tone checkerboard, one tile per grid cell, with
 * the biome's decorations on top. It is rendered once per level and board size, and drawn every frame.
 */
public final class BoardBackground {
    private BoardBackground() {
    }

    /** Renders the board {@code scale} times its 600×600 size, so a bigger window stays sharp. */
    public static BufferedImage create(Color tileA, Color tileB, Decorations.Decorator decorations,
                                       Decorations.CellPicker cells, double scale) {
        BufferedImage image = new BufferedImage(scaled(Board.WIDTH, scale), scaled(Board.HEIGHT, scale),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.scale(scale, scale);
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

    /** A board length in pixels at {@code scale}, rounded the same way for the image and its placement. */
    public static int scaled(int length, double scale) {
        return Math.max(1, (int) Math.round(length * scale));
    }
}
