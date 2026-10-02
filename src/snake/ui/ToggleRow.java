package snake.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/** A settings row with a label and an on/off switch. Clicking it, or Enter/Space, flips the switch. */
final class ToggleRow extends MenuButton {
    static final int WIDTH = 520;
    static final int HEIGHT = 56;

    private static final int CORNER_RADIUS = 16;
    private static final int BORDER_WIDTH = 2;
    private static final int PADDING_X = 18;
    private static final int TRACK_WIDTH = 46;
    private static final int TRACK_HEIGHT = 26;
    private static final int KNOB_INSET = 3;
    private static final Color TRACK_ON = new Color(0x4F9A4A);
    private static final Color TRACK_OFF = new Color(0xD9CFB2);

    private boolean on;

    ToggleRow(String label, boolean on, Consumer<Boolean> onChange) {
        super(label);
        this.on = on;
        Dimension size = new Dimension(WIDTH + MARGIN * 2, HEIGHT + MARGIN * 2);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        addActionListener(e -> {
            this.on = !this.on;
            repaint();
            onChange.accept(this.on);
        });
    }

    @Override
    int cornerArc(Rectangle face) {
        return CORNER_RADIUS * 2;
    }

    @Override
    void paintFace(Graphics2D g2d, Rectangle face, boolean hover) {
        paintRoundedFace(g2d, face, Theme.PILL_FILL, Theme.PILL_BORDER, BORDER_WIDTH, hover);
        int centerY = face.y + face.height / 2;

        g2d.setFont(Theme.font(Theme.Weight.MEDIUM, 18));
        g2d.setColor(Theme.PILL_TEXT);
        FontMetrics labelMetrics = g2d.getFontMetrics();
        g2d.drawString(getText(), face.x + PADDING_X,
                centerY + (labelMetrics.getAscent() - labelMetrics.getDescent()) / 2);

        // Switch on the right, with "On"/"Off" next to it
        int trackX = face.x + face.width - PADDING_X - TRACK_WIDTH;
        int trackY = centerY - TRACK_HEIGHT / 2;
        g2d.setColor(on ? TRACK_ON : TRACK_OFF);
        g2d.fill(new RoundRectangle2D.Float(trackX, trackY, TRACK_WIDTH, TRACK_HEIGHT, TRACK_HEIGHT, TRACK_HEIGHT));
        float knob = TRACK_HEIGHT - KNOB_INSET * 2;
        float knobX = on ? trackX + TRACK_WIDTH - KNOB_INSET - knob : trackX + KNOB_INSET;
        g2d.setColor(Color.WHITE);
        g2d.fill(new Ellipse2D.Float(knobX, trackY + KNOB_INSET, knob, knob));

        String state = on ? "On" : "Off";
        g2d.setFont(Theme.font(Theme.Weight.REGULAR, 14));
        g2d.setColor(Theme.TAGLINE);
        FontMetrics stateMetrics = g2d.getFontMetrics();
        g2d.drawString(state, trackX - 10 - stateMetrics.stringWidth(state),
                centerY + (stateMetrics.getAscent() - stateMetrics.getDescent()) / 2);
    }
}
