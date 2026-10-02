package snake.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import snake.model.Board;
import snake.model.Direction;
import snake.ui.render.SnakeColor;
import snake.ui.render.SnakeRenderer;

/** A snake color choice in Settings: a small snake in that color and its name. */
final class ColorSwatch extends MenuButton {
    static final int HEIGHT = 76;

    private static final int CORNER_RADIUS = 16;
    private static final int BORDER_WIDTH = 2;
    private static final int SELECTED_BORDER_WIDTH = 3;

    private final SnakeColor color;
    private boolean chosen; // the color currently in use

    ColorSwatch(SnakeColor color) {
        super(color.displayName());
        this.color = color;
        // Left/Right also move between swatches, since they sit in a row
        InputMap inputs = getInputMap(JComponent.WHEN_FOCUSED);
        inputs.put(KeyStroke.getKeyStroke("LEFT"), "focusPrevious");
        inputs.put(KeyStroke.getKeyStroke("RIGHT"), "focusNext");
    }

    SnakeColor color() {
        return color;
    }

    void setChosen(boolean chosen) {
        this.chosen = chosen;
        repaint();
    }

    @Override
    int cornerArc(Rectangle face) {
        return CORNER_RADIUS * 2;
    }

    @Override
    void paintFace(Graphics2D g2d, Rectangle face, boolean hover) {
        paintRoundedFace(g2d, face, Theme.PILL_FILL, null, BORDER_WIDTH, hover);
        if (chosen) {
            paintBorder(g2d, face, Theme.TITLE_GREEN, SELECTED_BORDER_WIDTH);
        } else {
            paintBorder(g2d, face, Theme.PILL_BORDER, hover ? BORDER_WIDTH + 1 : BORDER_WIDTH);
        }

        // A three-segment snake, centered, facing right. SnakeRenderer draws at cell centers.
        int u = Board.UNIT;
        int headCenterX = face.x + face.width / 2 + u;
        int centerY = face.y + 28;
        int cellX = headCenterX - u / 2;
        int cellY = centerY - u / 2;
        List<Point> segments = Arrays.asList(new Point(cellX, cellY), new Point(cellX - u, cellY),
                new Point(cellX - u * 2, cellY));
        SnakeRenderer.paint(g2d, segments, Direction.RIGHT, color.palette(false));

        g2d.setFont(Theme.font(Theme.Weight.MEDIUM, 14));
        g2d.setColor(Theme.PILL_TEXT);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(getText(), face.x + (face.width - fm.stringWidth(getText())) / 2, face.y + face.height - 12);

        if (chosen) {
            paintCheck(g2d, face.x + face.width - 16, face.y + 15);
        }
    }

    /** A green circle with a white tick, centered at (cx, cy). */
    private static void paintCheck(Graphics2D g2d, float cx, float cy) {
        float r = 8;
        g2d.setColor(Theme.TITLE_GREEN);
        g2d.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        Path2D tick = new Path2D.Float();
        tick.moveTo(cx - 3.8f, cy + 0.2f);
        tick.lineTo(cx - 1, cy + 3);
        tick.lineTo(cx + 4, cy - 2.6f);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(tick);
    }
}
