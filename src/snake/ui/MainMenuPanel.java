package snake.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import snake.model.Level;

/** The main menu: header with the high score, one illustrated card per level, Settings and Quit. */
final class MainMenuPanel extends JPanel {
    static final int WIDTH = 600;
    static final int HEIGHT = 720;

    private static final int PADDING_Y = 30;
    private static final int PADDING_X = 40;
    private static final int GAP = 12;
    private static final int CONTENT_WIDTH = WIDTH - PADDING_X * 2;
    // Buttons reserve MenuButton.MARGIN around their face for the focus ring, so the panel's own
    // side padding is smaller by that much, and gaps next to buttons are shortened to match.
    private static final int M = MenuButton.MARGIN;
    private static final int ROW_WIDTH = CONTENT_WIDTH + M * 2;

    private final Header header = new Header();
    private BiomeCard firstCard;

    MainMenuPanel(Consumer<Level> onLevelSelected) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.PARCHMENT);
        setOpaque(true);
        setBorder(new EmptyBorder(PADDING_Y, PADDING_X - M, PADDING_Y - M, PADDING_X - M));

        add(row(header));
        add(Box.createVerticalStrut(GAP));

        JLabel tagline = new JLabel("Where are we slithering today?");
        tagline.setFont(Theme.font(Theme.Weight.REGULAR, 16));
        tagline.setForeground(Theme.TAGLINE);
        tagline.setBorder(new EmptyBorder(0, M, 6, M));
        add(row(tagline));
        add(Box.createVerticalStrut(GAP - M));

        for (Level level : Level.values()) {
            if (level.ordinal() > 0) {
                add(Box.createVerticalStrut(GAP - M * 2));
            }
            BiomeCard card = card(level);
            card.addActionListener(e -> onLevelSelected.accept(level));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(card);
            if (firstCard == null) {
                firstCard = card;
            }
        }

        add(Box.createVerticalStrut(GAP - M * 2));
        add(Box.createVerticalGlue());
        add(createBottomRow());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    void setHighScore(int highScore) {
        header.setHighScore(highScore);
    }

    void focusFirstCard() {
        firstCard.requestFocusInWindow();
    }

    /** Fixes {@code component}'s width to the row width and left-aligns it in the column. */
    private static JComponent row(JComponent component) {
        Dimension size = new Dimension(ROW_WIDTH, component.getPreferredSize().height);
        component.setPreferredSize(size);
        component.setMaximumSize(size);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    /** Each level's look on the menu. To add a level, add its card here and its scene in {@link BiomeScenes}. */
    private static BiomeCard card(Level level) {
        String title = level.number() + " · " + level.themeName();
        switch (level) {
            case LEVEL_1:
                return new BiomeCard(title, "Time attack, 3 minutes",
                        new Color(0xF3C77A), new Color(0xD9A24A), new Color(0x4A2E05), new Color(0x7A5212),
                        BiomeScenes::desert);
            case LEVEL_2:
                return new BiomeCard(title, "Faster pace, first to 300 wins",
                        new Color(0xB6DD8A), new Color(0x86B85A), new Color(0x1F3D0E), new Color(0x3D6421),
                        BiomeScenes::grass);
            case LEVEL_3:
                return new BiomeCard(title, "Speed and vanishing food",
                        new Color(0x9FD3EE), new Color(0x5EA9D4), new Color(0x0C3350), new Color(0x275A7C),
                        BiomeScenes::ocean);
            case LEVEL_4:
                return new BiomeCard(title, "Speed, vanishing food, a bomb",
                        new Color(0x8FC3A0), new Color(0x4F8F63), new Color(0x0F2E1A), new Color(0x2C5A3A),
                        BiomeScenes::forest);
            default:
                return new BiomeCard(title, "Race an AI snake, dodge the bomb",
                        new Color(0x3A3566), new Color(0x26224A), new Color(0xF2EEFF), new Color(0xC4BDF0),
                        BiomeScenes::space);
        }
    }

    private JPanel createBottomRow() {
        PillButton settings = new PillButton("Settings", Theme.PILL_FILL, Theme.PILL_BORDER, Theme.PILL_TEXT);
        settings.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Settings are coming soon.", "Settings", JOptionPane.INFORMATION_MESSAGE));

        PillButton quit = new PillButton("Quit", Theme.TITLE_GREEN, null, Color.WHITE);
        quit.addActionListener(e -> System.exit(0));

        JPanel row = new JPanel(new GridLayout(1, 2, GAP - M * 2, 0));
        row.setOpaque(false);
        row.add(settings);
        row.add(quit);
        row.setPreferredSize(new Dimension(ROW_WIDTH, PillButton.HEIGHT + M * 2));
        return (JPanel) row(row);
    }

    /** Logo and "Snake" on the left, the high-score pill on the right. */
    private static final class Header extends JComponent {
        private static final int LOGO_WIDTH = 58;
        private static final int LOGO_HEIGHT = 40;
        private static final int TITLE_GAP = 12;
        private static final int PILL_PADDING_X = 14;
        private static final int PILL_PADDING_Y = 8;
        private static final int PILL_BORDER = 2;
        private static final int STAR_SIZE = 18;
        private static final int STAR_GAP = 8;

        private final Font titleFont = Theme.font(Theme.Weight.BOLD, 46);
        private final Font scoreFont = Theme.font(Theme.Weight.MEDIUM, 18);
        private int highScore;

        Header() {
            FontMetrics fm = getFontMetrics(titleFont);
            setPreferredSize(new Dimension(ROW_WIDTH, Math.max(LOGO_HEIGHT, fm.getAscent() + fm.getDescent())));
        }

        void setHighScore(int highScore) {
            this.highScore = highScore;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            Theme.antialias(g2d);
            int left = M;
            int right = getWidth() - M;
            int height = getHeight();

            AffineTransform old = g2d.getTransform();
            g2d.translate(left, (height - LOGO_HEIGHT) / 2);
            BiomeScenes.logo(g2d);
            g2d.setTransform(old);

            g2d.setFont(titleFont);
            g2d.setColor(Theme.TITLE_GREEN);
            FontMetrics titleMetrics = g2d.getFontMetrics();
            g2d.drawString("Snake", left + LOGO_WIDTH + TITLE_GAP,
                    (height + titleMetrics.getAscent() - titleMetrics.getDescent()) / 2);

            // High-score pill
            g2d.setFont(scoreFont);
            FontMetrics scoreMetrics = g2d.getFontMetrics();
            String score = String.valueOf(highScore);
            int contentHeight = Math.max(STAR_SIZE, scoreMetrics.getAscent() + scoreMetrics.getDescent());
            int pillWidth = PILL_PADDING_X * 2 + STAR_SIZE + STAR_GAP + scoreMetrics.stringWidth(score);
            int pillHeight = PILL_PADDING_Y * 2 + contentHeight;
            int pillX = right - pillWidth;
            int pillY = (height - pillHeight) / 2;

            g2d.setColor(Theme.PILL_FILL);
            g2d.fill(new RoundRectangle2D.Float(pillX, pillY, pillWidth, pillHeight, pillHeight, pillHeight));
            float inset = PILL_BORDER / 2f;
            g2d.setColor(Theme.PILL_BORDER);
            g2d.setStroke(new BasicStroke(PILL_BORDER));
            g2d.draw(new RoundRectangle2D.Float(pillX + inset, pillY + inset, pillWidth - PILL_BORDER,
                    pillHeight - PILL_BORDER, pillHeight - PILL_BORDER, pillHeight - PILL_BORDER));

            int starX = pillX + PILL_PADDING_X;
            BiomeScenes.star(g2d, starX, (height - STAR_SIZE) / 2f, STAR_SIZE);

            g2d.setColor(Theme.HIGH_SCORE_TEXT);
            g2d.drawString(score, starX + STAR_SIZE + STAR_GAP,
                    (height + scoreMetrics.getAscent() - scoreMetrics.getDescent()) / 2);
            g2d.dispose();
        }
    }
}
