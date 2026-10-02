package snake.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** A fully rounded menu button with centered text. */
final class PillButton extends MenuButton {
    static final int HEIGHT = 50;

    private static final int BORDER_WIDTH = 2;
    private static final float TEXT_SIZE = 17;

    private final Color fill;
    private final Color border; // null for no border
    private final Color textColor;

    PillButton(String text, Color fill, Color border, Color textColor) {
        super(text);
        this.fill = fill;
        this.border = border;
        this.textColor = textColor;
    }

    @Override
    int cornerArc(Rectangle face) {
        return face.height;
    }

    @Override
    void paintFace(Graphics2D g2d, Rectangle face, boolean hover) {
        paintRoundedFace(g2d, face, fill, border, BORDER_WIDTH, hover);

        g2d.setFont(Theme.font(Theme.Weight.MEDIUM, TEXT_SIZE));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        g2d.drawString(text, face.x + (face.width - fm.stringWidth(text)) / 2,
                face.y + (face.height + fm.getAscent() - fm.getDescent()) / 2);
    }
}
