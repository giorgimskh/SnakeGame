package snake.ui.render;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

import snake.model.Direction;
import snake.model.Snake;

import static snake.model.Board.UNIT;

/** Paints the player's snake and the AI snake. */
public final class SnakePainter {
    private SnakePainter() {
    }

    public static void paintPlayer(Graphics g, Snake snake) {
        List<Point> segments = snake.segments();
        // Tail first and head last, so the head is never covered
        for (int i = segments.size() - 1; i > 0; i--) {
            drawBodySegment(g, segments.get(i));
        }
        drawHead(g, segments.get(0), snake.direction());
    }

    /** A green circle with eyes that face the direction of travel. */
    private static void drawHead(Graphics g, Point head, Direction direction) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint headGradient = new GradientPaint(
            head.x, head.y, new Color(0, 255, 127), // Spring green
            head.x + UNIT, head.y + UNIT, new Color(0, 200, 100) // Darker green
        );
        g2d.setPaint(headGradient);
        g2d.fillOval(head.x, head.y, UNIT, UNIT);

        g2d.setColor(new Color(0, 150, 75));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(head.x + 1, head.y + 1, UNIT - 2, UNIT - 2);

        int eyeSize = UNIT / 5;
        int eyeOffset = UNIT / 3;
        int leftEyeX, leftEyeY, rightEyeX, rightEyeY;

        switch (direction) {
            case RIGHT:
                leftEyeX = head.x + UNIT - eyeOffset;
                leftEyeY = head.y + eyeOffset;
                rightEyeX = head.x + UNIT - eyeOffset;
                rightEyeY = head.y + UNIT - eyeOffset;
                break;
            case LEFT:
                leftEyeX = head.x + eyeOffset;
                leftEyeY = head.y + eyeOffset;
                rightEyeX = head.x + eyeOffset;
                rightEyeY = head.y + UNIT - eyeOffset;
                break;
            case UP:
                leftEyeX = head.x + eyeOffset;
                leftEyeY = head.y + eyeOffset;
                rightEyeX = head.x + UNIT - eyeOffset;
                rightEyeY = head.y + eyeOffset;
                break;
            default: // DOWN
                leftEyeX = head.x + eyeOffset;
                leftEyeY = head.y + UNIT - eyeOffset;
                rightEyeX = head.x + UNIT - eyeOffset;
                rightEyeY = head.y + UNIT - eyeOffset;
                break;
        }

        g2d.setColor(Color.BLACK);
        g2d.fillOval(leftEyeX, leftEyeY, eyeSize, eyeSize);
        g2d.fillOval(rightEyeX, rightEyeY, eyeSize, eyeSize);

        g2d.dispose();
    }

    /** A blue circle with a green stripe, centered in its cell. Body and tail segments look the same. */
    private static void drawBodySegment(Graphics g, Point body) {
        int inset = 1;
        int size = UNIT - inset * 2;
        int x = body.x + inset;
        int y = body.y + inset;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bodyGradient = new GradientPaint(
            x, y, new Color(30, 144, 255), // Dodger blue
            x + size, y + size, new Color(25, 25, 112) // Midnight blue
        );
        g2d.setPaint(bodyGradient);
        g2d.fillOval(x, y, size, size);

        // Stripe across the middle, clipped to the circle
        Shape oldClip = g2d.getClip();
        g2d.clip(new Ellipse2D.Float(x, y, size, size));
        int lineHeight = Math.max(4, size / 4);
        int lineY = y + size / 2 - lineHeight / 2;
        GradientPaint accentGradient = new GradientPaint(
            x, lineY, new Color(0, 255, 127), // Spring green
            x + size, lineY + lineHeight, new Color(0, 200, 100) // Darker green
        );
        g2d.setPaint(accentGradient);
        g2d.fillRect(x, lineY, size, lineHeight);
        g2d.setClip(oldClip);

        g2d.setColor(new Color(25, 25, 112)); // Midnight blue
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x + 1, y + 1, size - 2, size - 2);

        g2d.dispose();
    }

    public static void paintAi(Graphics g, Snake ai) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Point> segments = ai.segments();
        for (int i = segments.size() - 1; i >= 0; i--) { // Head last
            Point segment = segments.get(i);

            if (i == 0) {
                GradientPaint headGradient = new GradientPaint(
                    segment.x, segment.y, new Color(255, 0, 0), // Bright red
                    segment.x + UNIT, segment.y + UNIT, new Color(139, 0, 0) // Dark red
                );
                g2d.setPaint(headGradient);
                g2d.fillOval(segment.x, segment.y, UNIT, UNIT);

                g2d.setColor(new Color(100, 0, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(segment.x + 1, segment.y + 1, UNIT - 2, UNIT - 2);

                // Eyes
                g2d.setColor(Color.WHITE);
                int eyeSize = UNIT / 5;
                int eyeOffset = UNIT / 3;
                g2d.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                g2d.fillOval(segment.x + UNIT - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize, eyeSize);

                // Pupils
                g2d.setColor(Color.BLACK);
                g2d.fillOval(segment.x + eyeOffset + 1, segment.y + eyeOffset + 1, eyeSize - 2, eyeSize - 2);
                g2d.fillOval(segment.x + UNIT - eyeOffset - eyeSize + 1, segment.y + eyeOffset + 1, eyeSize - 2, eyeSize - 2);
            } else {
                GradientPaint bodyGradient = new GradientPaint(
                    segment.x, segment.y, new Color(139, 0, 0), // Dark red
                    segment.x + UNIT, segment.y + UNIT, new Color(100, 0, 0) // Darker red
                );
                g2d.setPaint(bodyGradient);
                g2d.fillOval(segment.x, segment.y, UNIT, UNIT);

                g2d.setColor(new Color(80, 0, 0));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawOval(segment.x + 1, segment.y + 1, UNIT - 2, UNIT - 2);
            }
        }

        g2d.dispose();
    }
}
