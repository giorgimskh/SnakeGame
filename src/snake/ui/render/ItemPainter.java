package snake.ui.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import snake.model.FoodType;

/** Paints the apples and the bomb, each in its 25 × 25 grid cell. */
public final class ItemPainter {
    /** How items adapt to the board under them. */
    public static final class Style {
        /** Style for the dark Space board: apple glow, rimmed bomb, no shadows. */
        public static final Style DARK = new Style(null, true);

        final Color shadow; // null for no shadow
        final boolean dark;

        private Style(Color shadow, boolean dark) {
            this.shadow = shadow;
            this.dark = dark;
        }

        /** Style for a light board whose tiles are {@code tile}: items cast a darker-tile shadow. */
        public static Style light(Color tile) {
            return new Style(new Color(
                    Math.round(tile.getRed() * 0.82f),
                    Math.round(tile.getGreen() * 0.82f),
                    Math.round(tile.getBlue() * 0.82f)), false);
        }
    }

    private static final Color APPLE = new Color(0xE8484F);
    private static final Color APPLE_HIGHLIGHT = new Color(0xF7898D);
    private static final Color GOLD_APPLE = new Color(0xF2B53A);
    private static final Color GOLD_HIGHLIGHT = new Color(0xFBE29A);
    private static final Color GOLD_TEXT = new Color(0x7A5212);
    private static final Color STEM = new Color(0x6A4A2A);
    private static final Color LEAF = new Color(0x5FA83A);
    private static final Color GLOW = new Color(0xFFF1C2);
    private static final Color DANGER = new Color(0xE8484F);
    private static final Color SPARK = new Color(0xF7B13A);
    private static final Color SPARK_CORE = new Color(0xFFF1C2);

    /** Below this share of its life, a vanishing apple starts to fade. */
    private static final float FADE_START = 0.3f;
    private static final float FADE_MIN_ALPHA = 0.4f;

    private ItemPainter() {
    }

    /**
     * Paints the food in its cell. {@code lifeFraction} is how much of a vanishing apple's time is
     * left (1 to 0), shown as a shrinking ring and a fade near the end; pass a negative value on
     * levels where the apple doesn't vanish.
     */
    public static void paintFood(Graphics2D g, Point cell, FoodType type, Style style, float lifeFraction) {
        if (cell == null) {
            return;
        }
        Graphics2D a = cellGraphics(g, cell);
        float cx = 12.5f;
        float cy = 14;

        if (lifeFraction >= 0) {
            a.setColor(Color.WHITE);
            a.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // From 12 o'clock, shrinking clockwise as the time runs out
            a.draw(new Arc2D.Float(cx - 15, cy - 15, 30, 30, 90, -360 * lifeFraction, Arc2D.OPEN));
            if (lifeFraction < FADE_START) {
                // The ring stays solid; the apple, its shadow and glow fade
                float alpha = FADE_MIN_ALPHA + (1 - FADE_MIN_ALPHA) * lifeFraction / FADE_START;
                a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
        }
        float alpha = ((AlphaComposite) a.getComposite()).getAlpha();

        if (style.shadow != null) {
            a.setColor(style.shadow);
            a.fill(new Ellipse2D.Float(cx - 7.5f, 21, 15, 4.5f));
        }
        if (style.dark) {
            a.setColor(GLOW);
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f * alpha));
            a.fill(circle(cx, cy, 14));
            a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        boolean gold = type == FoodType.MULTIPLIER;
        a.setColor(gold ? GOLD_APPLE : APPLE);
        a.fill(circle(cx, cy, 9));
        a.setColor(gold ? GOLD_HIGHLIGHT : APPLE_HIGHLIGHT);
        a.fill(circle(cx - 3.5f, cy - 3.5f, 2.5f));

        a.setColor(STEM);
        a.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D stem = new Path2D.Float();
        stem.moveTo(cx, cy - 8);
        stem.quadTo(cx, cy - 11, cx + 1.5f, cy - 12.5f);
        a.draw(stem);

        Graphics2D leaf = (Graphics2D) a.create();
        leaf.rotate(Math.toRadians(-25), cx + 4.5f, cy - 10.5f);
        leaf.setColor(LEAF);
        leaf.fill(new Ellipse2D.Float(cx + 1.5f, cy - 12.2f, 6, 3.4f));
        leaf.dispose();

        if (gold) {
            a.setColor(GOLD_TEXT);
            a.setFont(new Font("SansSerif", Font.BOLD, 8));
            FontMetrics fm = a.getFontMetrics();
            a.drawString("2x", cx - fm.stringWidth("2x") / 2f, cy + fm.getAscent() / 2f - 0.5f);
        }
        a.dispose();
    }

    /** Paints the bomb and its danger zone. {@code now} makes the spark flicker. */
    public static void paintBomb(Graphics2D g, Point cell, Style style, long now) {
        if (cell == null) {
            return;
        }
        Graphics2D b = cellGraphics(g, cell);
        float cx = 12.5f;
        float cy = 12.5f;

        // Danger zone
        b.setColor(DANGER);
        b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
        b.fill(circle(cx, cy, 15));
        b.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        b.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10, new float[] {4, 4}, 0));
        b.draw(circle(cx, cy, 15));
        b.setComposite(AlphaComposite.SrcOver);

        float bodyY = cy + 1;
        Ellipse2D body = circle(cx, bodyY, 8);
        b.setColor(style.dark ? new Color(0x14121F) : new Color(0x2B2A22));
        b.fill(body);
        if (style.dark) {
            b.setColor(new Color(0x6A6590));
            b.setStroke(new BasicStroke(1));
            b.draw(body);
        }
        b.setColor(new Color(0x77756A));
        b.fill(circle(cx - 3, bodyY - 3, 1.8f));

        b.setColor(new Color(0x8A5A2B));
        b.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D fuse = new Path2D.Float();
        fuse.moveTo(cx + 2, bodyY - 7.5f);
        fuse.quadTo(cx + 3, bodyY - 11, cx + 5.5f, bodyY - 11.5f);
        b.draw(fuse);

        float sparkRadius = (now / 150) % 2 == 0 ? 2.6f : 3.4f;
        b.setColor(SPARK);
        b.fill(circle(cx + 6, bodyY - 12, sparkRadius));
        b.setColor(SPARK_CORE);
        b.fill(circle(cx + 6, bodyY - 12, 1.2f));
        b.dispose();
    }

    /** A copy of {@code g} with antialiasing on and the origin at the cell's top-left corner. */
    private static Graphics2D cellGraphics(Graphics2D g, Point cell) {
        Graphics2D c = (Graphics2D) g.create();
        c.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        c.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        c.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        c.translate(cell.x, cell.y);
        return c;
    }

    private static Ellipse2D circle(float cx, float cy, float r) {
        return new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2);
    }
}
