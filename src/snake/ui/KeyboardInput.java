package snake.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import snake.game.GameController;
import snake.model.Direction;

/** Arrow keys steer the snake and Esc returns to the menu. Keys only work while a game is running. */
final class KeyboardInput extends KeyAdapter {
    private final GameController controller;
    private final Runnable onEscape;

    KeyboardInput(GameController controller, Runnable onEscape) {
        this.controller = controller;
        this.onEscape = onEscape;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!controller.isRunning()) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                controller.turn(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                controller.turn(Direction.RIGHT);
                break;
            case KeyEvent.VK_UP:
                controller.turn(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                controller.turn(Direction.DOWN);
                break;
            case KeyEvent.VK_ESCAPE:
                onEscape.run();
                break;
        }
    }
}
