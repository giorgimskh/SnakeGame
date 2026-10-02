package snake.ui;

import java.awt.*;

import javax.swing.JPanel;

import snake.game.GameController;
import snake.model.Board;
import snake.model.GameState;
import snake.ui.render.ItemPainter;
import snake.ui.render.SnakePainter;
import snake.ui.render.ThemePainter;

/** The board. Paints the current game, or the one that just ended. */
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

        // Keep painting the last game after it ends, so it stays visible behind the end dialog
        GameState state = controller.getState();
        if (state == null) {
            return;
        }

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

        if (controller.isPaused()) {
            paintPausedOverlay(g2d);
        }
    }

    private void paintPausedOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        drawCentered(g2d, "PAUSED", getHeight() / 2);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCentered(g2d, "Press P or Space to resume, Esc for the menu", getHeight() / 2 + 40);
    }

    private void drawCentered(Graphics2D g2d, String text, int baseline) {
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, baseline);
    }
}
