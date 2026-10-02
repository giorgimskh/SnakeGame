package snake.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.InputStream;

/** Colors and fonts shared by the main menu. */
final class Theme {
    static final Color PARCHMENT = new Color(0xF6EFDC);
    static final Color TITLE_GREEN = new Color(0x2F6B3A);
    static final Color LOGO_GREEN = new Color(0x4F9A4A);
    static final Color LOGO_EYE = new Color(0x173D1E);
    static final Color TAGLINE = new Color(0x6F6A55);
    static final Color PILL_FILL = new Color(0xFFF8E8);
    static final Color PILL_BORDER = new Color(0xE8D9B0);
    static final Color PILL_TEXT = new Color(0x5A5440);
    static final Color STAR_GOLD = new Color(0xE0A526);
    static final Color HIGH_SCORE_TEXT = new Color(0x7A5A12);
    static final Color FOCUS_RING = TITLE_GREEN;

    enum Weight {
        REGULAR("Fredoka-Regular.ttf", Font.PLAIN),
        MEDIUM("Fredoka-Medium.ttf", Font.PLAIN),
        BOLD("Fredoka-Bold.ttf", Font.BOLD);

        private final String file;
        private final int fallbackStyle;
        private Font base; // loaded on first use; stays null if loading fails
        private boolean loaded;

        Weight(String file, int fallbackStyle) {
            this.file = file;
            this.fallbackStyle = fallbackStyle;
        }
    }

    private Theme() {
    }

    /** Fredoka at {@code weight} and {@code size} px, or SansSerif if the font can't be loaded. */
    static Font font(Weight weight, float size) {
        if (!weight.loaded) {
            weight.loaded = true;
            weight.base = load(weight.file);
        }
        if (weight.base == null) {
            return new Font("SansSerif", weight.fallbackStyle, Math.round(size));
        }
        return weight.base.deriveFont(size);
    }

    private static Font load(String file) {
        try (InputStream in = Theme.class.getResourceAsStream("/fonts/" + file)) {
            if (in == null) {
                System.out.println("Font not found: /fonts/" + file + ", using SansSerif");
                return null;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (Exception e) {
            System.out.println("Failed to load font " + file + ": " + e.getMessage() + ", using SansSerif");
            return null;
        }
    }

    /** Turns on shape and text antialiasing. */
    static void antialias(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** {@code color} blended {@code amount} (0 to 1) of the way towards white. */
    static Color lighten(Color color, float amount) {
        return new Color(
                Math.round(color.getRed() + (255 - color.getRed()) * amount),
                Math.round(color.getGreen() + (255 - color.getGreen()) * amount),
                Math.round(color.getBlue() + (255 - color.getBlue()) * amount));
    }
}
