package snake.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * The main menu's drawings: one scene per biome card, the logo and the high-score star. Scenes
 * paint in a {@link BiomeCard#SCENE_WIDTH} × {@link BiomeCard#SCENE_HEIGHT} local space. Each is a
 * direct translation of the design's SVG, with relative and smooth-curve commands converted to
 * absolute coordinates.
 */
final class BiomeScenes {
    private BiomeScenes() {
    }

    static void desert(Graphics2D g) {
        fill(g, circle(150, 22, 13), 0xFFF1C2);

        Path2D dune = new Path2D.Float();
        dune.moveTo(0, 76);
        dune.lineTo(0, 54);
        dune.curveTo(30, 38, 60, 38, 95, 54);
        dune.curveTo(130, 70, 160, 64, 190, 48);
        dune.lineTo(190, 76);
        dune.closePath();
        fill(g, dune, 0xE9A94E);

        Path2D nearDune = new Path2D.Float();
        nearDune.moveTo(40, 76);
        nearDune.lineTo(40, 60);
        nearDune.curveTo(65, 50, 90, 52, 120, 62);
        nearDune.curveTo(150, 72, 170, 68, 190, 60);
        nearDune.lineTo(190, 76);
        nearDune.closePath();
        fill(g, nearDune, 0xD48E33);

        Path2D cactus = new Path2D.Float();
        cactus.moveTo(70, 58);
        cactus.lineTo(70, 40);
        cactus.moveTo(70, 46);
        cactus.lineTo(64, 46);
        cactus.lineTo(64, 40);
        cactus.moveTo(70, 50);
        cactus.lineTo(76, 50);
        cactus.lineTo(76, 42);
        g.setColor(new Color(0x5F8A3A));
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g.draw(cactus);
    }

    static void grass(Graphics2D g) {
        Path2D hill = new Path2D.Float();
        hill.moveTo(0, 76);
        hill.lineTo(0, 50);
        hill.curveTo(40, 36, 100, 36, 190, 54);
        hill.lineTo(190, 76);
        hill.closePath();
        fill(g, hill, 0x7CC04F);

        Path2D blades = new Path2D.Float();
        blade(blades, 20, 26, 54, 30);
        blade(blades, 60, 65, 50, 70);
        blade(blades, 110, 116, 56, 120);
        blade(blades, 150, 155, 52, 160);
        fill(g, blades, 0x5EA83A);

        fill(g, circle(120, 40, 5), 0xFFFFFF);
        fill(g, circle(120, 40, 2), 0xF2C94C);
        fill(g, circle(58, 44, 4), 0xF28A8A);
    }

    /** A grass blade standing on the bottom edge, from {@code left} up to the tip and down to {@code right}. */
    private static void blade(Path2D path, float left, float tipX, float tipY, float right) {
        path.moveTo(left, 76);
        path.lineTo(tipX, tipY);
        path.lineTo(right, 76);
        path.closePath();
    }

    static void ocean(Graphics2D g) {
        fill(g, waves(46), 0x4A9FD0);
        fill(g, waves(60), 0x2F7FB3);

        Path2D sail = new Path2D.Float();
        sail.moveTo(130, 30);
        sail.lineTo(140, 14);
        sail.lineTo(150, 30);
        sail.closePath();
        fill(g, sail, 0xFFFFFF);

        Path2D hull = new Path2D.Float();
        hull.moveTo(124, 32);
        hull.lineTo(158, 32);
        hull.lineTo(152, 38);
        hull.lineTo(130, 38);
        hull.closePath();
        fill(g, hull, 0xC2723A);
    }

    /** Four wave humps with their crests at {@code y}, filled down to the bottom edge. */
    private static Path2D waves(float y) {
        Path2D path = new Path2D.Float();
        path.moveTo(0, y);
        path.curveTo(16, y - 8, 32, y + 8, 48, y);
        path.curveTo(64, y - 8, 80, y + 8, 96, y);
        path.curveTo(112, y - 8, 128, y + 8, 144, y);
        path.curveTo(160, y - 8, 176, y + 8, 190, y);
        path.lineTo(190, 76);
        path.lineTo(0, 76);
        path.closePath();
        return path;
    }

    static void forest(Graphics2D g) {
        fill(g, triangle(30, 70, 50, 18, 70, 70), 0x2F7A46);
        fill(g, triangle(80, 70, 104, 10, 128, 70), 0x256A3B);
        fill(g, triangle(134, 70, 152, 26, 170, 70), 0x3A8C52);
        fill(g, new Rectangle2D.Float(0, 68, 190, 8), 0x6A4A2A);
        fill(g, circle(78, 64, 6), 0x8A8A7A);
    }

    static void space(Graphics2D g) {
        fill(g, circle(120, 40, 20), 0xE88A5A);
        g.setColor(new Color(0xF2C49A));
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Float(120 - 34, 40 - 7, 68, 14));

        fill(g, circle(30, 16, 2), 0xFFFFFF);
        fill(g, circle(60, 56, 1.5f), 0xFFFFFF);
        fill(g, circle(170, 14, 2), 0xFFFFFF);
        fill(g, circle(80, 12, 1.5f), 0xFFFFFF);
        fill(g, circle(168, 62, 1.5f), 0xFFFFFF);
    }

    /** The snake logo in a 58 × 40 space: a thick wavy body with an eye. */
    static void logo(Graphics2D g) {
        Path2D body = new Path2D.Float();
        body.moveTo(4, 30);
        body.curveTo(12, 30, 12, 12, 22, 12);
        body.curveTo(32, 12, 32, 30, 42, 30);
        body.curveTo(52, 30, 50, 20, 54, 20);
        g.setColor(Theme.LOGO_GREEN);
        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(body);
        g.setColor(Theme.LOGO_EYE);
        g.fill(circle(52, 19, 2));
    }

    /** A five-point star filling a {@code size} × {@code size} box at ({@code x}, {@code y}). */
    static void star(Graphics2D g, float x, float y, float size) {
        float cx = x + size / 2;
        float cy = y + size / 2;
        float outer = size / 2;
        float inner = outer * 0.4f;
        Path2D star = new Path2D.Float();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            float r = i % 2 == 0 ? outer : inner;
            float px = cx + (float) (Math.cos(angle) * r);
            float py = cy + (float) (Math.sin(angle) * r);
            if (i == 0) {
                star.moveTo(px, py);
            } else {
                star.lineTo(px, py);
            }
        }
        star.closePath();
        g.setColor(Theme.STAR_GOLD);
        g.fill(star);
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
