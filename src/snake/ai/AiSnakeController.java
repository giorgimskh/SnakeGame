package snake.ai;

import java.awt.Point;

import snake.model.Board;
import snake.model.Direction;
import snake.model.GameState;
import snake.model.Snake;

/** Steering for the level 5 AI snake. */
public final class AiSnakeController {
    private AiSnakeController() {
    }

    /**
     * Heads for the food along its longer axis. If that cell is taken by either snake, it tries
     * the two perpendicular directions. If those are blocked too, it keeps going straight.
     */
    public static Direction chooseDirection(GameState state) {
        Snake ai = state.getAi();
        Snake player = state.getPlayer();
        Point head = ai.head();
        Direction current = ai.direction();

        Point target = state.getFood() != null ? state.getFood() : Board.center();
        int dx = target.x - head.x;
        int dy = target.y - head.y;

        Direction wanted = current;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0 && current != Direction.LEFT) {
                wanted = Direction.RIGHT;
            } else if (dx < 0 && current != Direction.RIGHT) {
                wanted = Direction.LEFT;
            }
        } else {
            if (dy > 0 && current != Direction.UP) {
                wanted = Direction.DOWN;
            } else if (dy < 0 && current != Direction.DOWN) {
                wanted = Direction.UP;
            }
        }

        if (isFree(Board.stepWrapped(head, wanted), player, ai)) {
            return wanted;
        }

        for (Direction alt : Direction.values()) {
            if (alt != current && alt != current.opposite()
                    && isFree(Board.stepWrapped(head, alt), player, ai)) {
                return alt;
            }
        }
        return current;
    }

    private static boolean isFree(Point cell, Snake player, Snake ai) {
        return !player.contains(cell) && !ai.contains(cell);
    }
}
