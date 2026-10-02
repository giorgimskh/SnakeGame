package snake.model;

import java.awt.Point;
import java.util.Random;

/**
 * Board geometry. Positions are pixel coordinates that are always multiples of {@link #UNIT}.
 * The edges wrap around for both snakes.
 */
public final class Board {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final int UNIT = 25;
    public static final int COLUMNS = WIDTH / UNIT;
    public static final int ROWS = HEIGHT / UNIT;

    private Board() {
    }

    /** Returns the cell one step from {@code from}, without wrapping. */
    public static Point step(Point from, Direction direction) {
        return new Point(from.x + direction.dx() * UNIT, from.y + direction.dy() * UNIT);
    }

    /** Returns the cell one step from {@code from}, wrapped onto the board. */
    public static Point stepWrapped(Point from, Direction direction) {
        Point p = step(from, direction);
        wrap(p);
        return p;
    }

    /** Moves {@code p} in place to the opposite edge if it has left the board. */
    private static void wrap(Point p) {
        if (p.x < 0) {
            p.x = WIDTH - UNIT;
        } else if (p.x >= WIDTH) {
            p.x = 0;
        }

        if (p.y < 0) {
            p.y = HEIGHT - UNIT;
        } else if (p.y >= HEIGHT) {
            p.y = 0;
        }
    }

    public static Point randomCell(Random random) {
        int x = random.nextInt(COLUMNS) * UNIT;
        int y = random.nextInt(ROWS) * UNIT;
        return new Point(x, y);
    }

    public static Point center() {
        return new Point(WIDTH / 2, HEIGHT / 2);
    }
}
