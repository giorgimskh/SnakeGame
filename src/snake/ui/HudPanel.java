package snake.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import snake.model.Board;
import snake.model.GameState;
import snake.model.Level;

/**
 * The bar above the board during a game: the level chip, score progress (you and the AI on
 * level 5), the time left on level 1, the 2x countdown while it's active, and the high score.
 */
final class HudPanel extends JComponent {
    static final int HEIGHT = 60;

    private static final int PADDING_X = 16;
    private static final int GAP = 12;
    private static final int BOTTOM_BORDER = 2;
    private static final int PILL_HEIGHT = 30;
    private static final int PILL_PADDING_X = 12;
    private static final int PILL_BORDER = 2;
    private static final int ICON_SIZE = 14;
    private static final int ICON_GAP = 6;

    private static final Color SCORE_TEXT = new Color(0x2B2A22);
    private static final Color PROGRESS_GREEN = new Color(0x4F9A4A);
    private static final Color AI_RED = new Color(0xD8434A);
    private static final Color TIME_TEXT = new Color(0x7A5212);
    private static final Color MULTIPLIER_TEXT = new Color(0xB7791F);

    // Snapshot of what to show, taken in update()
    private Level level;
    private int score;
    private int aiScore;
    private int highScore;
    private long remainingMs;
    private boolean multiplierActive;
    private long multiplierSecondsLeft;

    HudPanel() {
        setPreferredSize(new Dimension(Board.WIDTH, HEIGHT));
        setOpaque(true);
    }

    void update(GameState state, int highScore, long now) {
        this.level = state.getLevel();
        this.score = state.getScore();
        this.aiScore = state.getAiScore();
        this.highScore = highScore;
        this.remainingMs = state.getRemainingTimeMs(now);
        this.multiplierActive = state.isMultiplierActive();
        this.multiplierSecondsLeft = state.getMultiplierSecondsLeft(now);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Theme.antialias(g2d);
        int width = getWidth();
        g2d.setColor(Theme.PARCHMENT);
        g2d.fillRect(0, 0, width, HEIGHT);
        g2d.setColor(Theme.PILL_BORDER);
        g2d.fillRect(0, HEIGHT - BOTTOM_BORDER, width, BOTTOM_BORDER);
        if (level == null) {
            g2d.dispose();
            return;
        }
        int centerY = (HEIGHT - BOTTOM_BORDER) / 2;

        // Level chip on the left
        LevelTheme theme = LevelTheme.of(level);
        int chipRight = paintChip(g2d, PADDING_X, centerY, LevelTheme.title(level), theme);

        // Pills from the right edge inwards
        int right = width - PADDING_X;
        right = paintPill(g2d, right, centerY, String.valueOf(highScore), Theme.HIGH_SCORE_TEXT,
                HudPanel::starIcon) - GAP;
        if (multiplierActive) {
            right = paintPill(g2d, right, centerY, multiplierSecondsLeft + "s", Theme.HIGH_SCORE_TEXT,
                    HudPanel::multiplierIcon) - GAP;
        }
        if (level.hasTimeLimit()) {
            String time = String.format("%02d:%02d", remainingMs / 60000, (remainingMs % 60000) / 1000);
            right = paintPill(g2d, right, centerY, time, TIME_TEXT, HudPanel::clockIcon) - GAP;
        }

        // Score progress fills the space in between
        int left = chipRight + GAP;
        if (level.hasAiSnake()) {
            paintVersusRows(g2d, left, right, centerY);
        } else {
            paintScore(g2d, left, right, centerY);
        }
        g2d.dispose();
    }

    /** Draws the level chip with its left edge at {@code x}; returns its right edge. */
    private static int paintChip(Graphics2D g, int x, int centerY, String text, LevelTheme theme) {
        Font font = Theme.font(Theme.Weight.BOLD, 15);
        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(text) + PILL_PADDING_X * 2;
        int y = centerY - PILL_HEIGHT / 2;
        pillShape(g, x, y, w, theme.cardFill, theme.cardBorder);
        g.setFont(font);
        g.setColor(theme.cardTitle);
        g.drawString(text, x + PILL_PADDING_X, baseline(fm, centerY));
        return x + w;
    }

    private interface Icon {
        void paint(Graphics2D g, float x, float y);
    }

    /** Draws an icon-and-text pill with its right edge at {@code right}; returns its left edge. */
    private static int paintPill(Graphics2D g, int right, int centerY, String text, Color textColor, Icon icon) {
        Font font = Theme.font(Theme.Weight.MEDIUM, 15);
        FontMetrics fm = g.getFontMetrics(font);
        int w = PILL_PADDING_X * 2 + ICON_SIZE + ICON_GAP + fm.stringWidth(text);
        int x = right - w;
        pillShape(g, x, centerY - PILL_HEIGHT / 2, w, Theme.PILL_FILL, Theme.PILL_BORDER);
        icon.paint(g, x + PILL_PADDING_X, centerY - ICON_SIZE / 2f);
        g.setFont(font);
        g.setColor(textColor);
        g.drawString(text, x + PILL_PADDING_X + ICON_SIZE + ICON_GAP, baseline(fm, centerY));
        return x;
    }

    private static void pillShape(Graphics2D g, int x, int y, int w, Color fill, Color border) {
        g.setColor(fill);
        g.fill(new RoundRectangle2D.Float(x, y, w, PILL_HEIGHT, PILL_HEIGHT, PILL_HEIGHT));
        float inset = PILL_BORDER / 2f;
        g.setColor(border);
        g.setStroke(new BasicStroke(PILL_BORDER));
        g.draw(new RoundRectangle2D.Float(x + inset, y + inset, w - PILL_BORDER, PILL_HEIGHT - PILL_BORDER,
                PILL_HEIGHT - PILL_BORDER, PILL_HEIGHT - PILL_BORDER));
    }

    /** "Score" and "120 / 300" above a progress bar. */
    private void paintScore(Graphics2D g, int left, int right, int centerY) {
        Font labelFont = Theme.font(Theme.Weight.REGULAR, 13);
        Font valueFont = Theme.font(Theme.Weight.MEDIUM, 13);
        FontMetrics labelMetrics = g.getFontMetrics(labelFont);
        FontMetrics valueMetrics = g.getFontMetrics(valueFont);
        int barHeight = 8;
        int textHeight = labelMetrics.getAscent() + labelMetrics.getDescent();
        int top = centerY - (textHeight + 4 + barHeight) / 2;
        int textBaseline = top + labelMetrics.getAscent();

        g.setFont(labelFont);
        g.setColor(Theme.TAGLINE);
        g.drawString("Score", left, textBaseline);
        String value = score + " / " + Level.WIN_SCORE;
        g.setFont(valueFont);
        g.setColor(SCORE_TEXT);
        g.drawString(value, right - valueMetrics.stringWidth(value), textBaseline);

        paintBar(g, left, top + textHeight + 4, right - left, barHeight, score, PROGRESS_GREEN);
    }

    /** Level 5: a "You" row and an "AI" row, each with a dot, a bar and the score. */
    private void paintVersusRows(Graphics2D g, int left, int right, int centerY) {
        int rowHeight = 16;
        int rowGap = 4;
        int top = centerY - (rowHeight * 2 + rowGap) / 2;
        paintVersusRow(g, left, right, top, rowHeight, "You", score, PROGRESS_GREEN);
        paintVersusRow(g, left, right, top + rowHeight + rowGap, rowHeight, "AI", aiScore, AI_RED);
    }

    private static void paintVersusRow(Graphics2D g, int left, int right, int top, int height, String label,
                                       int value, Color color) {
        Font labelFont = Theme.font(Theme.Weight.MEDIUM, 13);
        FontMetrics fm = g.getFontMetrics(labelFont);
        int centerY = top + height / 2;
        float dot = 8;

        g.setColor(color);
        g.fill(new Ellipse2D.Float(left, centerY - dot / 2, dot, dot));
        g.setFont(labelFont);
        g.setColor(SCORE_TEXT);
        int labelX = left + (int) dot + 6;
        g.drawString(label, labelX, baseline(fm, centerY));

        // Bars start at the same x in both rows, after the wider label
        int barLeft = labelX + fm.stringWidth("You") + 8;
        String text = String.valueOf(value);
        int textX = right - fm.stringWidth(text);
        g.drawString(text, textX, baseline(fm, centerY));
        int barHeight = 6;
        paintBar(g, barLeft, centerY - barHeight / 2, textX - 8 - barLeft, barHeight, value, color);
    }

    /** A fully rounded bar filled to {@code value} out of the win score. */
    private static void paintBar(Graphics2D g, int x, int y, int width, int height, int value, Color fill) {
        g.setColor(Theme.PILL_BORDER);
        g.fill(new RoundRectangle2D.Float(x, y, width, height, height, height));
        float fraction = Math.min(1f, value / (float) Level.WIN_SCORE);
        if (fraction > 0) {
            // At least a dot's worth, so a few points still show
            float filled = Math.max(height, width * fraction);
            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(x, y, filled, height, height, height));
        }
    }

    private static void starIcon(Graphics2D g, float x, float y) {
        BiomeScenes.star(g, x, y, ICON_SIZE);
    }

    private static void clockIcon(Graphics2D g, float x, float y) {
        float c = ICON_SIZE / 2f;
        g.setColor(TIME_TEXT);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Float(x + 0.8f, y + 0.8f, ICON_SIZE - 1.6f, ICON_SIZE - 1.6f));
        g.draw(new Line2D.Float(x + c, y + c, x + c, y + 3.5f));
        g.draw(new Line2D.Float(x + c, y + c, x + c + 3, y + c + 1.5f));
    }

    /** "2x" in gold, for the multiplier countdown. */
    private static void multiplierIcon(Graphics2D g, float x, float y) {
        Font font = Theme.font(Theme.Weight.BOLD, 13);
        FontMetrics fm = g.getFontMetrics(font);
        g.setFont(font);
        g.setColor(MULTIPLIER_TEXT);
        g.drawString("2x", x + (ICON_SIZE - fm.stringWidth("2x")) / 2f, y + (ICON_SIZE + fm.getAscent() - fm.getDescent()) / 2f);
    }

    private static int baseline(FontMetrics fm, int centerY) {
        return centerY + (fm.getAscent() - fm.getDescent()) / 2;
    }
}
