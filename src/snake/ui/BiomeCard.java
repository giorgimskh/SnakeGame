package snake.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/** A level card on the main menu: title and subtitle on the left, an illustrated scene on the right. */
final class BiomeCard extends MenuButton {
    /** Paints a scene in a {@link #SCENE_WIDTH} × {@link #SCENE_HEIGHT} local space. */
    interface Scene {
        void paint(Graphics2D g2d);
    }

    static final int WIDTH = 520;
    static final int HEIGHT = 82;
    static final int SCENE_WIDTH = 190;
    static final int SCENE_HEIGHT = 76;

    private static final int CORNER_RADIUS = 18;
    private static final int BORDER_WIDTH = 3;
    private static final int TEXT_PADDING_X = 18;
    private static final int TEXT_LINE_GAP = 2;
    private static final float TITLE_SIZE = 22;
    private static final float SUBTITLE_SIZE = 14;
    private static final float SUBTITLE_MIN_SIZE = 13;

    private final String subtitle;
    private final Color fill;
    private final Color border;
    private final Color titleColor;
    private final Color subtitleColor;
    private final Scene scene;

    BiomeCard(String title, String subtitle, Color fill, Color border, Color titleColor, Color subtitleColor,
              Scene scene) {
        super(title);
        this.subtitle = subtitle;
        this.fill = fill;
        this.border = border;
        this.titleColor = titleColor;
        this.subtitleColor = subtitleColor;
        this.scene = scene;

        Dimension size = new Dimension(WIDTH + MARGIN * 2, HEIGHT + MARGIN * 2);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }

    @Override
    int cornerArc(Rectangle face) {
        return CORNER_RADIUS * 2;
    }

    @Override
    void paintFace(Graphics2D g2d, Rectangle face, boolean hover) {
        paintRoundedFace(g2d, face, fill, null, BORDER_WIDTH, hover);

        // Scene: anchored to the right edge, vertically centered, clipped to the card
        Shape oldClip = g2d.getClip();
        AffineTransform oldTransform = g2d.getTransform();
        g2d.clip(new RoundRectangle2D.Float(face.x, face.y, face.width, face.height,
                cornerArc(face), cornerArc(face)));
        g2d.translate(face.x + face.width - SCENE_WIDTH, face.y + (face.height - SCENE_HEIGHT) / 2);
        scene.paint(g2d);
        g2d.setTransform(oldTransform);
        g2d.setClip(oldClip);

        paintBorder(g2d, face, border, hover ? BORDER_WIDTH + 1 : BORDER_WIDTH);
        paintText(g2d, face);
    }

    private void paintText(Graphics2D g2d, Rectangle face) {
        int textWidth = face.width - SCENE_WIDTH - TEXT_PADDING_X * 2;
        Font titleFont = Theme.font(Theme.Weight.BOLD, TITLE_SIZE);
        Font subtitleFont = Theme.font(Theme.Weight.REGULAR, SUBTITLE_SIZE);
        if (g2d.getFontMetrics(subtitleFont).stringWidth(subtitle) > textWidth) {
            subtitleFont = Theme.font(Theme.Weight.REGULAR, SUBTITLE_MIN_SIZE);
        }
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
        FontMetrics subtitleMetrics = g2d.getFontMetrics(subtitleFont);

        int blockHeight = titleMetrics.getAscent() + titleMetrics.getDescent() + TEXT_LINE_GAP
                + subtitleMetrics.getAscent() + subtitleMetrics.getDescent();
        int x = face.x + TEXT_PADDING_X;
        int top = face.y + (face.height - blockHeight) / 2;

        g2d.setFont(titleFont);
        g2d.setColor(titleColor);
        int titleBaseline = top + titleMetrics.getAscent();
        g2d.drawString(getText(), x, titleBaseline);

        g2d.setFont(subtitleFont);
        g2d.setColor(subtitleColor);
        g2d.drawString(subtitle, x,
                titleBaseline + titleMetrics.getDescent() + TEXT_LINE_GAP + subtitleMetrics.getAscent());
    }
}
