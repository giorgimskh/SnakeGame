package snake.ui;

import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.*;

import snake.model.Board;
import snake.model.Level;

/** The main menu: title, one button per level, and Quit. */
final class MenuPanel extends JPanel {
    private static final Color BACKGROUND_TOP = new Color(25, 25, 112); // Dark blue
    private static final Color BACKGROUND_BOTTOM = new Color(0, 0, 0);
    private static final int STAR_COUNT = 50;

    // Fixed so the stars don't jump around on every repaint, e.g. when a button is hovered
    private final Point[] stars = new Point[STAR_COUNT];

    MenuPanel(Consumer<Level> onLevelSelected) {
        super(new BorderLayout());
        Random random = new Random(42);
        for (int i = 0; i < STAR_COUNT; i++) {
            stars[i] = new Point(random.nextInt(Board.WIDTH), random.nextInt(Board.HEIGHT));
        }
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createButtonsPanel(onLevelSelected), BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Board.WIDTH, Board.HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setPaint(new GradientPaint(0, 0, BACKGROUND_TOP, getWidth(), getHeight(), BACKGROUND_BOTTOM));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(255, 255, 255, 100));
        for (Point star : stars) {
            g2d.fillOval(star.x, star.y, 2, 2);
        }

        g2d.dispose();
    }

    private static JPanel createTitlePanel() {
        // Transparent, so the menu's own gradient and stars show through without a seam
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setPreferredSize(new Dimension(Board.WIDTH, 120));

        JLabel titleLabel = new JLabel("SNAKE GAME") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));

                String text = getText();
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(text, x + 3, y + 3);

                GradientPaint textGradient = new GradientPaint(
                    0, 0, new Color(135, 206, 250), // Light sky blue
                    getWidth(), getHeight(), new Color(255, 255, 255)
                );
                g2d.setPaint(textGradient);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }

    private static JPanel createButtonsPanel(Consumer<Level> onLevelSelected) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(Box.createVerticalGlue());
        for (Level level : Level.values()) {
            if (level.ordinal() > 0) {
                buttonsPanel.add(Box.createVerticalStrut(20));
            }
            Color[] colors = buttonColors(level);
            JButton button = new StyledButton(level.menuTitle(), colors[0], colors[1]);
            button.addActionListener(e -> onLevelSelected.accept(level));
            buttonsPanel.add(button);
        }

        JButton quitButton = new StyledButton("QUIT", new Color(128, 128, 128), new Color(100, 100, 100));
        quitButton.addActionListener(e -> System.exit(0));
        buttonsPanel.add(Box.createVerticalStrut(30));
        buttonsPanel.add(quitButton);
        buttonsPanel.add(Box.createVerticalGlue());
        return buttonsPanel;
    }

    /** Top and bottom gradient colors for a level's menu button. */
    private static Color[] buttonColors(Level level) {
        switch (level) {
            case LEVEL_1: return new Color[] {new Color(0, 150, 255), new Color(0, 100, 200)};
            case LEVEL_2: return new Color[] {new Color(255, 150, 0), new Color(200, 100, 0)};
            case LEVEL_3: return new Color[] {new Color(255, 100, 100), new Color(200, 50, 50)};
            case LEVEL_4: return new Color[] {new Color(128, 0, 128), new Color(100, 0, 100)};
            default:      return new Color[] {new Color(255, 20, 147), new Color(199, 21, 133)};
        }
    }
}
