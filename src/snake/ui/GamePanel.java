package snake.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

import snake.game.GameController;
import snake.model.Board;
import snake.model.GameState;
import snake.model.Level;
import snake.model.Snake;
import snake.ui.render.BoardBackground;
import snake.ui.render.Decorations;
import snake.ui.render.ItemPainter;
import snake.ui.render.SnakePalette;
import snake.ui.render.SnakeRenderer;

/** The board. Paints the current game, or the one that just ended. */
final class GamePanel extends JPanel {
    /** Cells in front of a snake's start that decorations stay off. */
    private static final int START_LOOKAHEAD = 3;

    private static final GameState.Events NO_EVENTS = new GameState.Events() {
        @Override
        public void appleEaten() {
        }

        @Override
        public void multiplierActivated() {
        }

        @Override
        public void foodSpawned() {
        }
    };

    private final GameController controller;
    // Each level's tiles and decorations, rendered once on first use
    private final Map<Level, BufferedImage> backgrounds = new EnumMap<>(Level.class);

    GamePanel(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Board.WIDTH, Board.HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Keep painting the last game after it ends, so it stays visible behind the end dialog
        GameState state = controller.getState();
        if (state == null) {
            return;
        }

        LevelTheme theme = LevelTheme.of(state.getLevel());
        g2d.drawImage(background(state.getLevel()), 0, 0, null);

        ItemPainter.Style itemStyle = theme.dark ? ItemPainter.Style.DARK : ItemPainter.Style.light(theme.tileA);
        if (state.isFoodVisible()) {
            float life = state.getLevel().hasVanishingApple() ? controller.getFoodLifeFraction() : -1;
            ItemPainter.paintFood(g2d, state.getFood(), state.getFoodType(), itemStyle, life);
        }
        if (state.getLevel().hasBomb() && state.isBombVisible()) {
            ItemPainter.paintBomb(g2d, state.getBomb(), itemStyle, System.currentTimeMillis());
        }
        if (state.getAi() != null) {
            Snake ai = state.getAi();
            SnakeRenderer.paint(g2d, ai.segments(), ai.direction(), SnakePalette.AI);
        }
        Snake player = state.getPlayer();
        SnakeRenderer.paint(g2d, player.segments(), player.direction(),
                theme.dark ? SnakePalette.PLAYER_ON_DARK : SnakePalette.PLAYER);

        if (controller.isPaused()) {
            paintPausedOverlay(g2d);
        }
    }

    private BufferedImage background(Level level) {
        BufferedImage image = backgrounds.get(level);
        if (image == null) {
            LevelTheme theme = LevelTheme.of(level);
            Decorations.CellPicker cells = new Decorations.CellPicker(LevelTheme.decorationSeed(level), startCells(level));
            image = BoardBackground.create(theme.tileA, theme.tileB, theme.decorations, cells);
            backgrounds.put(level, image);
        }
        return image;
    }

    /**
     * The cells both snakes start on, plus a few in front of each head, taken from a fresh game so
     * they always match the model. Decorations are kept off them.
     */
    static Set<Point> startCells(Level level) {
        GameState fresh = new GameState(level, new Random(0), 0, NO_EVENTS);
        Set<Point> cells = new HashSet<>();
        addStart(cells, fresh.getPlayer());
        if (fresh.getAi() != null) {
            addStart(cells, fresh.getAi());
        }
        return cells;
    }

    private static void addStart(Set<Point> cells, Snake snake) {
        cells.addAll(snake.segments());
        Point p = snake.head();
        for (int i = 0; i < START_LOOKAHEAD; i++) {
            p = Board.stepWrapped(p, snake.direction());
            cells.add(p);
        }
    }

    /** Dims the board and shows a parchment card saying the game is paused. */
    private void paintPausedOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 110));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int cardWidth = 360;
        int cardHeight = 120;
        int x = (getWidth() - cardWidth) / 2;
        int y = (getHeight() - cardHeight) / 2;
        g2d.setColor(Theme.PARCHMENT);
        g2d.fillRoundRect(x, y, cardWidth, cardHeight, 36, 36);
        g2d.setColor(Theme.PILL_BORDER);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 1, y + 1, cardWidth - 2, cardHeight - 2, 34, 34);

        g2d.setColor(Theme.TITLE_GREEN);
        g2d.setFont(Theme.font(Theme.Weight.BOLD, 40));
        drawCentered(g2d, "Paused", y + 60);
        g2d.setColor(Theme.TAGLINE);
        g2d.setFont(Theme.font(Theme.Weight.REGULAR, 15));
        drawCentered(g2d, "P or Space to resume, Esc for the menu", y + 92);
    }

    private void drawCentered(Graphics2D g2d, String text, int baseline) {
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, baseline);
    }
}
