package snake.ui.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

import snake.model.Board;
import snake.model.Direction;

/**
 * Draws a snake as one continuous tube through its segments' cell centers, with a round head,
 * eyes facing the direction of travel and a small tongue. Used for the player and the AI.
 */
public final class SnakeRenderer {
    private static final float HALF = Board.UNIT / 2f;
    private static final float OUTLINE_WIDTH = 21;
    private static final float BODY_WIDTH = 16;
    private static final float BELLY_WIDTH = 4;
    private static final float BELLY_ALPHA = 0.8f;
    private static final float BELLY_HEAD_GAP = 7;
    private static final float HEAD_RADIUS = 12;
    private static final float HEAD_OUTLINE = 2.5f;
    private static final Color TONGUE = new Color(0xE8484F);

    private SnakeRenderer() {
    }

    /** {@code segments} go from head to tail, as in {@code Snake.segments()}. */
    public static void paint(Graphics2D g, List<Point> segments, Direction direction, SnakePalette palette) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Path2D body = tube(segments, 0);
        stroke(g2d, body, OUTLINE_WIDTH, palette.outline);
        stroke(g2d, body, BODY_WIDTH, palette.body);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BELLY_ALPHA));
        stroke(g2d, tube(segments, BELLY_HEAD_GAP), BELLY_WIDTH, palette.belly);
        g2d.setComposite(AlphaComposite.SrcOver);

        paintHead(g2d, segments.get(0), direction, palette);
        g2d.dispose();
    }

    /**
     * A path from the tail to the head through the cell centers, ending {@code trimHead} px short
     * of the head. Where the snake wraps through an edge, the path runs off that edge and starts
     * again from the opposite one, instead of cutting across the board.
     */
    private static Path2D tube(List<Point> segments, float trimHead) {
        Path2D path = new Path2D.Float();
        int last = segments.size() - 1;
        path.moveTo(cx(segments.get(last)), cy(segments.get(last)));
        for (int i = last - 1; i >= 0; i--) {
            Point from = segments.get(i + 1);
            Point to = segments.get(i);
            float trim = i == 0 ? trimHead : 0;
            int dx = to.x - from.x;
            int dy = to.y - from.y;
            int shiftX = Math.abs(dx) > Board.UNIT ? -Integer.signum(dx) * Board.WIDTH : 0;
            int shiftY = Math.abs(dy) > Board.UNIT ? -Integer.signum(dy) * Board.HEIGHT : 0;
            if (shiftX != 0 || shiftY != 0) {
                // Run off the edge to where "to" would be without wrapping, then come back in
                lineTo(path, cx(from), cy(from), cx(to) + shiftX, cy(to) + shiftY, trim);
                path.moveTo(cx(from) - shiftX, cy(from) - shiftY);
                lineTo(path, cx(from) - shiftX, cy(from) - shiftY, cx(to), cy(to), trim);
            } else {
                lineTo(path, cx(from), cy(from), cx(to), cy(to), trim);
            }
        }
        return path;
    }

    /** Line from (x1, y1) towards (x2, y2), stopping {@code trim} px short of it. */
    private static void lineTo(Path2D path, float x1, float y1, float x2, float y2, float trim) {
        if (trim > 0) {
            float length = (float) Math.hypot(x2 - x1, y2 - y1);
            if (length > 0) {
                float keep = Math.max(0, length - trim) / length;
                x2 = x1 + (x2 - x1) * keep;
                y2 = y1 + (y2 - y1) * keep;
            }
        }
        path.lineTo(x2, y2);
    }

    private static void stroke(Graphics2D g2d, Path2D path, float width, Color color) {
        Stroke old = g2d.getStroke();
        g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(color);
        g2d.draw(path);
        g2d.setStroke(old);
    }

    /** Head, eyes and tongue, drawn in a frame where +x points the way the snake is going. */
    private static void paintHead(Graphics2D g, Point head, Direction direction, SnakePalette palette) {
        Graphics2D h = (Graphics2D) g.create();
        h.translate(cx(head), cy(head));
        h.rotate(Math.atan2(direction.dy(), direction.dx()));

        // Forked tongue, mostly hidden under the head
        h.setColor(TONGUE);
        h.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D tongue = new Path2D.Float();
        tongue.moveTo(9, 0);
        tongue.lineTo(16, 0);
        tongue.lineTo(18.5f, -2);
        tongue.moveTo(16, 0);
        tongue.lineTo(18.5f, 2);
        h.draw(tongue);

        Ellipse2D face = circle(0, 0, HEAD_RADIUS);
        h.setColor(palette.body);
        h.fill(face);
        h.setColor(palette.outline);
        h.setStroke(new BasicStroke(HEAD_OUTLINE));
        h.draw(face);

        for (int side = -1; side <= 1; side += 2) {
            float eyeY = side * 5.5f;
            h.setColor(Color.WHITE);
            h.fill(circle(4, eyeY, 3.2f));
            h.setColor(palette.pupil);
            h.fill(circle(5.2f, eyeY, 1.6f));
            if (palette.eyebrows) {
                // Slanted over the front of each eye like a lid, so the rival looks cross
                h.setColor(palette.pupil);
                h.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                h.draw(new Line2D.Float(8.4f, side * 2.2f, 6.0f, side * 8.8f));
            }
        }
        h.dispose();
    }

    private static float cx(Point p) {
        return p.x + HALF;
    }

    private static float cy(Point p) {
        return p.y + HALF;
    }

    private static Ellipse2D circle(float cx, float cy, float r) {
        return new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2);
    }
}
