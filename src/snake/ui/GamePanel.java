package snake.ui;

import java.awt.*;

import javax.swing.JPanel;

import snake.game.GameController;
import snake.model.Board;
import snake.model.GameState;
import snake.ui.render.ItemPainter;
import snake.ui.render.SnakePainter;
import snake.ui.render.ThemePainter;

/** The board. Paints the current game while it is running. */
final class GamePanel extends JPanel {
    private final GameController controller;

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

        if (!controller.isRunning()) {
            return;
        }
        GameState state = controller.getState();

        ThemePainter.paintBackground(g2d, state.getLevel(), getWidth(), getHeight());
        ThemePainter.paintVignette(g2d, getWidth(), getHeight());

        if (state.isFoodVisible()) {
            ItemPainter.paintFood(g2d, state.getFood(), state.getFoodType());
        }
        if (state.getLevel().hasBomb() && state.isBombVisible()) {
            ItemPainter.paintBomb(g2d, state.getBomb());
        }
        if (state.isAiAlive()) {
            SnakePainter.paintAi(g2d, state.getAi());
        }
        SnakePainter.paintPlayer(g2d, state.getPlayer());
    }
}
