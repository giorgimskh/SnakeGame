package snake.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Base for the main menu's custom-painted buttons. It handles hover, press and the focus ring,
 * and the keyboard: Up/Down move focus, Enter or Space activates. Subclasses paint the face.
 *
 * <p>The face is inset by {@link #MARGIN} on every side so the focus ring, drawn outside the
 * face, still fits inside the component.
 */
abstract class MenuButton extends JButton {
    /** Space around the face, reserved for the focus ring. */
    static final int MARGIN = 5;

    private static final int RING_GAP = 3;
    private static final int RING_WIDTH = 2;
    private static final int PRESSED_SHIFT = 2;
    private static final float HOVER_LIGHTEN = 0.06f;

    MenuButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Space already activates a focused JButton
        InputMap inputs = getInputMap(JComponent.WHEN_FOCUSED);
        inputs.put(KeyStroke.getKeyStroke("ENTER"), "activate");
        inputs.put(KeyStroke.getKeyStroke("UP"), "focusPrevious");
        inputs.put(KeyStroke.getKeyStroke("DOWN"), "focusNext");
        getActionMap().put("activate", action(this::doClick));
        getActionMap().put("focusPrevious", action(this::transferFocusBackward));
        getActionMap().put("focusNext", action(this::transferFocus));
    }

    private static AbstractAction action(Runnable runnable) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
    }

    /** The face's corner arc diameter, used for the focus ring too. */
    abstract int cornerArc(Rectangle face);

    /** Paints the face into {@code face}. {@code hover} is true while the mouse is over the button. */
    abstract void paintFace(Graphics2D g2d, Rectangle face, boolean hover);

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Theme.antialias(g2d);
        Rectangle face = new Rectangle(MARGIN, MARGIN, getWidth() - MARGIN * 2, getHeight() - MARGIN * 2);

        if (isFocusOwner()) {
            // Stroke centered RING_GAP + RING_WIDTH/2 outside the face
            float grow = RING_GAP + RING_WIDTH / 2f;
            float arc = cornerArc(face) + grow * 2;
            g2d.setColor(Theme.FOCUS_RING);
            g2d.setStroke(new BasicStroke(RING_WIDTH));
            g2d.draw(new RoundRectangle2D.Float(face.x - grow, face.y - grow,
                    face.width + grow * 2, face.height + grow * 2, arc, arc));
        }

        ButtonModel model = getModel();
        if (model.isArmed() && model.isPressed()) {
            g2d.translate(0, PRESSED_SHIFT);
        }
        paintFace(g2d, face, model.isRollover());
        g2d.dispose();
    }

    /**
     * Fills {@code face} as a rounded rectangle and draws its border inside it. On hover the fill
     * is slightly lighter and the border 1 px thicker. {@code border} may be null for no border.
     */
    void paintRoundedFace(Graphics2D g2d, Rectangle face, Color fill, Color border, int borderWidth, boolean hover) {
        float arc = cornerArc(face);
        g2d.setColor(hover ? Theme.lighten(fill, HOVER_LIGHTEN) : fill);
        g2d.fill(new RoundRectangle2D.Float(face.x, face.y, face.width, face.height, arc, arc));
        if (border != null) {
            paintBorder(g2d, face, border, hover ? borderWidth + 1 : borderWidth);
        }
    }

    /** Draws a {@code width} px border just inside {@code face}. */
    void paintBorder(Graphics2D g2d, Rectangle face, Color color, int width) {
        float inset = width / 2f;
        float arc = cornerArc(face) - width;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.draw(new RoundRectangle2D.Float(face.x + inset, face.y + inset,
                face.width - width, face.height - width, arc, arc));
    }
}
