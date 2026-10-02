package snake.ui.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import snake.model.Board;

/**
 * Each biome's background decorations. They are purely visual (nothing on the board collides
 * except the snakes and the bomb), so they are drawn faint enough never to look like obstacles.
 * Each one is drawn inside a single grid cell picked by a {@link CellPicker}.
 */
public final class Decorations {
    /** Paints one biome's decorations, taking cells from {@code cells}. */
    public interface Decorator {
        void paint(Graphics2D g, CellPicker cells);
    }

    private static final int U = Board.UNIT;

    private Decorations() {
    }

    /**
     * Hands out scattered cells from a seeded {@link Random}, so a level looks the same every time.
     * A picked cell is never in the outer row or column, never a reserved cell (the snakes' start
     * and the cells in front of them), and never next to an earlier pick.
     */
    public static final class CellPicker {
        private final Random random;
        private final boolean[][] reserved = new boolean[Board.COLUMNS][Board.ROWS];
        private final List<Point> picked = new ArrayList<>(); // grid coordinates

        public CellPicker(long seed, Set<Point> reservedCells) {
            random = new Random(seed);
            for (Point p : reservedCells) {
                reserved[p.x / U][p.y / U] = true;
            }
        }

        public Random random() {
            return random;
        }

        /**
         * A random free cell's top-left corner in pixels, at least {@code clearance} cells from every
         * earlier pick in each direction (1 leaves one empty cell between), or null if none is left.
         */
        public Point next(int clearance) {
            List<Point> free = new ArrayList<>();
            for (int c = 1; c < Board.COLUMNS - 1; c++) {
                for (int r = 1; r < Board.ROWS - 1; r++) {
                    if (!reserved[c][r] && clearOfPicks(c, r, clearance)) {
                        free.add(new Point(c, r));
                    }
                }
            }
            if (free.isEmpty()) {
                return null;
            }
            Point cell = free.get(random.nextInt(free.size()));
            picked.add(cell);
            return new Point(cell.x * U, cell.y * U);
        }

        private boolean clearOfPicks(int c, int r, int clearance) {
            for (Point p : picked) {
                if (Math.abs(p.x - c) <= clearance && Math.abs(p.y - r) <= clearance) {
                    return false;
                }
            }
            return true;
        }

        /** Every picked cell so far, in grid coordinates. */
        public List<Point> picked() {
            return Collections.unmodifiableList(picked);
        }
    }

    // ---- Desert ----

    public static void desert(Graphics2D g, CellPicker cells) {
        place(g, cells, 6, 0.6f, Decorations::cactus);
        place(g, cells, 8, 0.7f, Decorations::ripple);
        place(g, cells, 8, 0.8f, Decorations::pebbles);
    }

    private static void cactus(Graphics2D g, float x, float y, Random random) {
        fill(g, new Ellipse2D.Float(x + 5, y + 20, 16, 4), 0xD9B06A); // sand shadow
        Color green = new Color(0x6FA34A);
        g.setColor(green);
        g.fill(new RoundRectangle2D.Float(x + 10, y + 3, 5, 19, 5, 5));   // trunk
        g.fill(new RoundRectangle2D.Float(x + 5, y + 7, 4, 8, 4, 4));     // left arm
        g.fill(new Rectangle2D.Float(x + 5, y + 12, 6, 3));
        g.fill(new RoundRectangle2D.Float(x + 16, y + 5, 4, 8, 4, 4));    // right arm
        g.fill(new Rectangle2D.Float(x + 14, y + 10, 6, 3));
        g.setColor(new Color(0x9CCB6E));                                  // center stripe
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(x + 12.5f, y + 6, x + 12.5f, y + 19));
    }

    private static void ripple(Graphics2D g, float x, float y, Random random) {
        g.setColor(new Color(0xD9B06A));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Float(x + 2, y + 7, 21, 10, 25, 130, Arc2D.OPEN));
        g.draw(new Arc2D.Float(x + 6, y + 13, 15, 8, 25, 130, Arc2D.OPEN));
    }

    private static void pebbles(Graphics2D g, float x, float y, Random random) {
        fill(g, new Ellipse2D.Float(x + 7, y + 11, 6, 4.5f), 0xC99A55);
        fill(g, new Ellipse2D.Float(x + 14, y + 14, 4, 3), 0xC99A55);
    }

    // ---- Grass ----

    public static void grass(Graphics2D g, CellPicker cells) {
        place(g, cells, 10, 0.7f, Decorations::tuft);
        place(g, cells, 8, 0.85f, Decorations::daisy);
    }

    private static void tuft(Graphics2D g, float x, float y, Random random) {
        g.setColor(new Color(0x5EA83A));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float[][] blades = {{8, 7, 5}, {11, 10, 3}, {14, 15, 4}, {17, 19, 7}}; // base x, tip x, tip y
        for (float[] b : blades) {
            Path2D blade = new Path2D.Float();
            blade.moveTo(x + b[0], y + 20);
            blade.quadTo(x + (b[0] + b[1]) / 2 + 1, y + 13, x + b[1], y + b[2] + 4);
            g.draw(blade);
        }
    }

    private static void daisy(Graphics2D g, float x, float y, Random random) {
        float cx = x + 12.5f;
        float cy = y + 12.5f;
        g.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            double a = i * 2 * Math.PI / 5;
            float px = cx + (float) Math.cos(a) * 3;
            float py = cy + (float) Math.sin(a) * 3;
            g.fill(new Ellipse2D.Float(px - 2.2f, py - 2.2f, 4.4f, 4.4f));
        }
        fill(g, circle(cx, cy, 1.8f), 0xF2C94C);
    }

    // ---- Ocean ----

    public static void ocean(Graphics2D g, CellPicker cells) {
        place(g, cells, 8, 0.8f, Decorations::waveLine);
        place(g, cells, 8, 0.8f, Decorations::bubbles);
        place(g, cells, 4, 0.8f, Decorations::fish);
    }

    private static void waveLine(Graphics2D g, float x, float y, Random random) {
        Path2D wave = new Path2D.Float();
        wave.moveTo(x + 2, y + 12);
        wave.quadTo(x + 7, y + 8, x + 12, y + 12);
        wave.quadTo(x + 17, y + 16, x + 22, y + 12);
        g.setColor(new Color(0xD6F0FB));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(wave);
    }

    private static void bubbles(Graphics2D g, float x, float y, Random random) {
        g.setColor(new Color(0xD6F0FB));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(circle(x + 10, y + 15, 3));
        g.draw(circle(x + 16, y + 9, 2));
    }

    private static void fish(Graphics2D g, float x, float y, Random random) {
        boolean facingLeft = random.nextBoolean();
        Graphics2D f = (Graphics2D) g.create();
        if (facingLeft) {
            // Mirror around the cell's vertical center line
            f.translate(x * 2 + U, 0);
            f.scale(-1, 1);
        }
        Path2D tail = new Path2D.Float();
        tail.moveTo(x + 8, y + 12.5f);
        tail.lineTo(x + 3, y + 8.5f);
        tail.lineTo(x + 3, y + 16.5f);
        tail.closePath();
        fill(f, tail, 0xF28C38);
        fill(f, new Ellipse2D.Float(x + 7, y + 9, 13, 7), 0xF28C38);
        fill(f, circle(x + 16.5f, y + 11.5f, 1), 0x0C3350);
        f.dispose();
    }

    // ---- Forest ----

    public static void forest(Graphics2D g, CellPicker cells) {
        place(g, cells, 7, 0.6f, Decorations::pine);
        place(g, cells, 10, 0.75f, Decorations::leaf);
    }

    private static void pine(Graphics2D g, float x, float y, Random random) {
        Composite old = g.getComposite();
        g.setComposite(multiply(old, 0.5f));
        fill(g, new Ellipse2D.Float(x + 5, y + 19, 15, 4), 0x256A3B); // soft shadow
        g.setComposite(old);
        fill(g, new Rectangle2D.Float(x + 11, y + 17, 3, 5), 0x6A4A2A);
        fill(g, triangle(x + 4, y + 19, x + 12.5f, y + 7, x + 21, y + 19), 0x2F7A46);
        fill(g, triangle(x + 6.5f, y + 12, x + 12.5f, y + 2, x + 18.5f, y + 12), 0x256A3B);
    }

    private static void leaf(Graphics2D g, float x, float y, Random random) {
        Graphics2D l = (Graphics2D) g.create();
        l.rotate(random.nextDouble() * Math.PI, x + 12.5, y + 12.5);
        fill(l, new Ellipse2D.Float(x + 9.5f, y + 10.75f, 7, 3.5f), 0xD98A3A);
        l.dispose();
    }

    // ---- Space ----

    public static void space(Graphics2D g, CellPicker cells) {
        // Big background bodies first, with more room around them
        place(g, cells, 1, 2, 0.32f, Decorations::planet);
        place(g, cells, 1, 1, 0.35f, Decorations::moon);
        place(g, cells, 2, 1, 0.85f, Decorations::sparkle);
        place(g, cells, 30, 1, 0.8f, Decorations::starDot);
    }

    private static void planet(Graphics2D g, float x, float y, Random random) {
        float cx = x + 12.5f;
        float cy = y + 12.5f;
        fill(g, circle(cx, cy, 22), 0xE88A5A);
        g.setColor(new Color(0xF2C49A));
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Float(cx - 38, cy - 8, 76, 16));
    }

    private static void moon(Graphics2D g, float x, float y, Random random) {
        fill(g, circle(x + 12.5f, y + 12.5f, 7), 0x7F77DD);
    }

    private static void sparkle(Graphics2D g, float x, float y, Random random) {
        float cx = x + 12.5f;
        float cy = y + 12.5f;
        float outer = 5.5f;
        float inner = 1.3f;
        Path2D star = new Path2D.Float();
        star.moveTo(cx, cy - outer);
        star.lineTo(cx + inner, cy - inner);
        star.lineTo(cx + outer, cy);
        star.lineTo(cx + inner, cy + inner);
        star.lineTo(cx, cy + outer);
        star.lineTo(cx - inner, cy + inner);
        star.lineTo(cx - outer, cy);
        star.lineTo(cx - inner, cy - inner);
        star.closePath();
        fill(g, star, 0xFFFFFF);
    }

    private static void starDot(Graphics2D g, float x, float y, Random random) {
        float r = random.nextBoolean() ? 1 : 1.5f;
        fill(g, circle(x + 4 + random.nextInt(17), y + 4 + random.nextInt(17), r), 0xFFFFFF);
    }

    // ---- Helpers ----

    /** Draws one decoration at its top-left corner; {@code random} is for small per-item variation. */
    private interface Item {
        void paint(Graphics2D g, float x, float y, Random random);
    }

    private static void place(Graphics2D g, CellPicker cells, int count, float alpha, Item item) {
        place(g, cells, count, 1, alpha, item);
    }

    /** Draws {@code count} items at {@code alpha} on cells picked with {@code clearance}. */
    private static void place(Graphics2D g, CellPicker cells, int count, int clearance, float alpha, Item item) {
        Graphics2D d = (Graphics2D) g.create();
        d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        for (int i = 0; i < count; i++) {
            Point cell = cells.next(clearance);
            if (cell == null) {
                break;
            }
            item.paint(d, cell.x, cell.y, cells.random());
        }
        d.dispose();
    }

    private static Composite multiply(Composite current, float alpha) {
        float base = current instanceof AlphaComposite ? ((AlphaComposite) current).getAlpha() : 1f;
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, base * alpha);
    }

    private static Ellipse2D circle(float cx, float cy, float r) {
        return new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2);
    }

    private static Path2D triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        Path2D path = new Path2D.Float();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.closePath();
        return path;
    }

    private static void fill(Graphics2D g, Shape shape, int rgb) {
        g.setColor(new Color(rgb));
        g.fill(shape);
    }
}
