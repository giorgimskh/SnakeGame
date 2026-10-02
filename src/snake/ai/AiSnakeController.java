package snake.ai;

import java.awt.Point;
import java.util.List;

import snake.model.Board;
import snake.model.Direction;
import snake.model.GameState;
import snake.model.Snake;

/** Steering for the level 5 AI snake. */
public final class AiSnakeController {
    private AiSnakeController() {
    }

    /**
     * Heads for the food along its longer axis, or for the center while the food is hidden. If
     * that cell is blocked, it tries the other directions except straight back. If every one is
     * blocked, it keeps going straight.
     */
    public static Direction chooseDirection(GameState state) {
        Snake ai = state.getAi();
        Point head = ai.head();
        Direction current = ai.direction();

        Point target = state.isFoodVisible() && state.getFood() != null ? state.getFood() : Board.center();
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

        if (isFree(Board.stepWrapped(head, wanted), state)) {
            return wanted;
        }
        for (Direction alt : Direction.values()) {
            if (alt != wanted && alt != current.opposite()
                    && isFree(Board.stepWrapped(head, alt), state)) {
                return alt;
            }
        }
        return current;
    }

    /** True if the AI can move into {@code cell} without hitting a snake or a visible bomb. */
    private static boolean isFree(Point cell, GameState state) {
        if (state.getPlayer().contains(cell)) {
            return false;
        }
        if (state.isBombVisible() && cell.equals(state.getBomb())) {
            return false;
        }
        // The AI's own tail moves out of the way on this step
        List<Point> segments = state.getAi().segments();
        return !segments.contains(cell) || cell.equals(segments.get(segments.size() - 1));
    }
}
