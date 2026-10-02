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
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import snake.persistence.Settings;
import snake.ui.render.SnakeColor;

/**
 * The settings screen: music and sound effects switches, the snake color, and a list of the
 * controls. Changes are written to {@link Settings} right away and reported through
 * {@code onChange}. Back or Esc returns to the menu.
 */
final class SettingsPanel extends JPanel {
    private static final int PADDING_Y = 30;
    private static final int PADDING_X = 40;
    private static final int GAP = 12;
    // Same layout scheme as MainMenuPanel: buttons keep MenuButton.MARGIN around their face
    private static final int M = MenuButton.MARGIN;
    private static final int ROW_WIDTH = MainMenuPanel.WIDTH - PADDING_X * 2 + M * 2;

    private final Settings settings;
    private final Runnable onChange;
    private final ToggleRow musicRow;
    private final List<ColorSwatch> swatches = new ArrayList<>();

    SettingsPanel(Settings settings, Runnable onChange, Runnable onBack) {
        this.settings = settings;
        this.onChange = onChange;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.PARCHMENT);
        setOpaque(true);
        setBorder(new EmptyBorder(PADDING_Y, PADDING_X - M, PADDING_Y - M, PADDING_X - M));

        add(row(new Title()));
        add(Box.createVerticalStrut(GAP));

        add(row(sectionLabel("Sound")));
        musicRow = new ToggleRow("Music", settings.isMusic(), on -> {
            settings.setMusic(on);
            onChange.run();
        });
        add(left(musicRow));
        add(Box.createVerticalStrut(GAP - M * 2));
        add(left(new ToggleRow("Sound effects", settings.isSoundEffects(), on -> {
            settings.setSoundEffects(on);
            onChange.run();
        })));
        add(Box.createVerticalStrut(GAP - M));

        add(row(sectionLabel("Snake color")));
        add(row(createSwatchRow()));
        add(Box.createVerticalStrut(GAP - M));

        add(row(sectionLabel("Controls")));
        add(row(new ControlsCard()));

        add(Box.createVerticalStrut(GAP - M));
        add(Box.createVerticalGlue());
        PillButton back = new PillButton("Back", Theme.TITLE_GREEN, null, Color.WHITE);
        back.addActionListener(e -> onBack.run());
        back.setPreferredSize(new Dimension(ROW_WIDTH, PillButton.HEIGHT + M * 2));
        add(row(back));

        // Esc goes back too. This binding is checked before the game's window-wide Esc.
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "back");
        getActionMap().put("back", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBack.run();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MainMenuPanel.WIDTH, MainMenuPanel.HEIGHT);
    }

    void focusFirst() {
        musicRow.requestFocusInWindow();
    }

    private JPanel createSwatchRow() {
        JPanel row = new JPanel(new GridLayout(1, SnakeColor.values().length, GAP - M * 2, 0));
        row.setOpaque(false);
        SnakeColor current = SnakeColor.fromName(settings.getSnakeColor());
        for (SnakeColor color : SnakeColor.values()) {
            ColorSwatch swatch = new ColorSwatch(color);
            swatch.setChosen(color == current);
            swatch.addActionListener(e -> chooseColor(color));
            swatches.add(swatch);
            row.add(swatch);
        }
        row.setPreferredSize(new Dimension(ROW_WIDTH, ColorSwatch.HEIGHT + M * 2));
        return row;
    }

    private void chooseColor(SnakeColor color) {
        for (ColorSwatch swatch : swatches) {
            swatch.setChosen(swatch.color() == color);
        }
        settings.setSnakeColor(color.name());
        onChange.run();
    }

    private static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(Theme.Weight.MEDIUM, 16));
        label.setForeground(Theme.TAGLINE);
        label.setBorder(new EmptyBorder(0, M, 6, M));
        return label;
    }

    /** Fixes {@code component}'s width to the row width and left-aligns it in the column. */
    private static JComponent row(JComponent component) {
        Dimension size = new Dimension(ROW_WIDTH, component.getPreferredSize().height);
        component.setPreferredSize(size);
        component.setMaximumSize(size);
        return left(component);
    }

    private static JComponent left(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    /** The logo and "Settings". */
    private static final class Title extends JComponent {
        private final Font font = Theme.font(Theme.Weight.BOLD, 46);

        Title() {
            FontMetrics fm = getFontMetrics(font);
            setPreferredSize(new Dimension(ROW_WIDTH, fm.getAscent() + fm.getDescent()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            Theme.antialias(g2d);
            AffineTransform old = g2d.getTransform();
            g2d.translate(M, (getHeight() - 40) / 2);
            BiomeScenes.logo(g2d);
            g2d.setTransform(old);
            g2d.setFont(font);
            g2d.setColor(Theme.TITLE_GREEN);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("Settings", M + 58 + 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2d.dispose();
        }
    }

    /** A read-only card listing the keys. */
    private static final class ControlsCard extends JComponent {
        private static final String[][] ROWS = {
            {"Arrows", "WASD", "Steer the snake"},
            {"P", "Space", "Pause and resume"},
            {"Esc", null, "Back to the menu"},
            {"Up / Down", "Enter", "Move and pick in menus"},
        };
        private static final int PADDING = 14;
        private static final int ROW_HEIGHT = 26;
        private static final int CHIP_HEIGHT = 22;
        private static final int CHIP_PADDING_X = 8;
        private static final int DESCRIPTION_X = 190;

        ControlsCard() {
            setPreferredSize(new Dimension(ROW_WIDTH, PADDING * 2 + ROWS.length * ROW_HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            Theme.antialias(g2d);
            int x = M;
            int width = getWidth() - M * 2;
            int height = getHeight();
            g2d.setColor(Theme.PILL_FILL);
            g2d.fill(new RoundRectangle2D.Float(x, 0, width, height, 32, 32));
            g2d.setColor(Theme.PILL_BORDER);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new RoundRectangle2D.Float(x + 1, 1, width - 2, height - 2, 30, 30));

            Font chipFont = Theme.font(Theme.Weight.MEDIUM, 13);
            Font textFont = Theme.font(Theme.Weight.REGULAR, 15);
            for (int i = 0; i < ROWS.length; i++) {
                int centerY = PADDING + i * ROW_HEIGHT + ROW_HEIGHT / 2;
                int chipX = x + PADDING + 4;
                for (int k = 0; k < 2; k++) {
                    if (ROWS[i][k] != null) {
                        chipX = paintChip(g2d, chipFont, ROWS[i][k], chipX, centerY) + 6;
                    }
                }
                g2d.setFont(textFont);
                g2d.setColor(Theme.PILL_TEXT);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(ROWS[i][2], x + DESCRIPTION_X, centerY + (fm.getAscent() - fm.getDescent()) / 2);
            }
            g2d.dispose();
        }

        /** A key label in a small rounded box; returns its right edge. */
        private static int paintChip(Graphics2D g2d, Font font, String text, int x, int centerY) {
            FontMetrics fm = g2d.getFontMetrics(font);
            int w = fm.stringWidth(text) + CHIP_PADDING_X * 2;
            int y = centerY - CHIP_HEIGHT / 2;
            g2d.setColor(Theme.PARCHMENT);
            g2d.fill(new RoundRectangle2D.Float(x, y, w, CHIP_HEIGHT, 10, 10));
            g2d.setColor(Theme.PILL_BORDER);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(new RoundRectangle2D.Float(x + 0.75f, y + 0.75f, w - 1.5f, CHIP_HEIGHT - 1.5f, 10, 10));
            g2d.setFont(font);
            g2d.setColor(Theme.HIGH_SCORE_TEXT);
            g2d.drawString(text, x + CHIP_PADDING_X, centerY + (fm.getAscent() - fm.getDescent()) / 2);
            return x + w;
        }
    }
}
