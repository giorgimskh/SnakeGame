package snake.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import snake.game.GameController;
import snake.model.Direction;

/**
 * Arrow keys or WASD steer the snake and Esc returns to the menu. Keys only work while a game is running.
 * They are key bindings on the root pane, so they work whichever component in the window has focus.
 */
final class KeyboardInput {
    private final GameController controller;
    private final InputMap inputs;
    private final ActionMap actions;

    private KeyboardInput(JRootPane rootPane, GameController controller) {
        this.controller = controller;
        this.inputs = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.actions = rootPane.getActionMap();
    }

    static void install(JRootPane rootPane, GameController controller, Runnable onEscape) {
        KeyboardInput input = new KeyboardInput(rootPane, controller);
        input.bind("turnUp", () -> controller.turn(Direction.UP), "UP", "W");
        input.bind("turnDown", () -> controller.turn(Direction.DOWN), "DOWN", "S");
        input.bind("turnLeft", () -> controller.turn(Direction.LEFT), "LEFT", "A");
        input.bind("turnRight", () -> controller.turn(Direction.RIGHT), "RIGHT", "D");
        input.bind("menu", onEscape, "ESCAPE");
    }

    /** Runs {@code action} when any of {@code keys} (in {@link KeyStroke#getKeyStroke(String)} form) is pressed. */
    private void bind(String name, Runnable action, String... keys) {
        for (String key : keys) {
            inputs.put(KeyStroke.getKeyStroke(key), name);
        }
        actions.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.isRunning()) {
                    action.run();
                }
            }
        });
    }
}
