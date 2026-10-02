package snake.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/** A rounded menu button with a vertical gradient fill and shadowed text. */
final class StyledButton extends JButton {
    private final Color primaryColor;
    private final Color secondaryColor;

    StyledButton(String text, Color primaryColor, Color secondaryColor) {
        super(text);
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;

        setPreferredSize(new Dimension(350, 50));
        setMaximumSize(new Dimension(350, 50));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFocusable(false); // Mouse only, so a focused button never takes the game keys
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint buttonGradient = new GradientPaint(
            0, 0, primaryColor,
            0, getHeight(), secondaryColor
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Glow border
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);

        // Text shadow
        String text = getText();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2 + 2;
        int textY = (getHeight() + fm.getAscent()) / 2 + 2;
        g2d.drawString(text, textX, textY);

        g2d.setColor(Color.WHITE);
        textX = (getWidth() - fm.stringWidth(text)) / 2;
        textY = (getHeight() + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);

        g2d.dispose();
    }
}
