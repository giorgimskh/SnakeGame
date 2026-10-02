package snake.ai;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import snake.model.Board;
import snake.model.Direction;
import snake.model.GameState;
import snake.model.Snake;

/**
 * Steering for the level 5 AI snake. It goes for the food but keeps away from the player: it
 * never enters the cells next to the player's head when it has another choice, keeps a few cells
 * of distance, and avoids dead ends it could trap itself in.
 */
public final class AiSnakeController {
    /** For a cell the player's head could move into next. */
    private static final double NEXT_TO_PLAYER_PENALTY = 1000;
    /** For a cell with less free space behind it than the AI is long. */
    private static final double DEAD_END_PENALTY = 500;
    /** Bonus per cell of distance from the player's head, up to {@link #KEEP_AWAY_RANGE}. */
    private static final double KEEP_AWAY_WEIGHT = 1.5;
    private static final int KEEP_AWAY_RANGE = 4;
    /** Small preference for going straight, to break ties. */
    private static final double STRAIGHT_BONUS = 0.1;

    private AiSnakeController() {
    }

    /**
     * Picks the AI's next direction, or returns null if every move is blocked, in which case the
     * AI waits a turn. Each free direction is scored by how close it gets to the food (or the
     * center while the food is hidden), minus the penalties above.
     */
    public static Direction chooseDirection(GameState state) {
        Snake ai = state.getAi();
        Point head = ai.head();
        Direction current = ai.direction();
        Point playerHead = state.getPlayer().head();
        Point target = state.isFoodVisible() && state.getFood() != null ? state.getFood() : Board.center();

        Direction best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Direction direction : Direction.values()) {
            if (direction == current.opposite()) {
                continue;
            }
            Point next = Board.stepWrapped(head, direction);
            if (!state.isFreeForAi(next)) {
                continue;
            }

            double score = -Board.distance(next, target);
            int fromPlayer = Board.distance(next, playerHead);
            if (fromPlayer <= 1) {
                score -= NEXT_TO_PLAYER_PENALTY;
            }
            score += KEEP_AWAY_WEIGHT * Math.min(fromPlayer, KEEP_AWAY_RANGE);
            if (freeSpace(state, next, ai.size()) < ai.size()) {
                score -= DEAD_END_PENALTY;
            }
            if (direction == current) {
                score += STRAIGHT_BONUS;
            }

            if (score > bestScore) {
                bestScore = score;
                best = direction;
            }
        }
        return best;
    }

    /** Counts the free cells reachable from {@code start}, stopping once {@code limit} is reached. */
    private static int freeSpace(GameState state, Point start, int limit) {
        Set<Point> seen = new HashSet<>();
        Deque<Point> queue = new ArrayDeque<>();
        seen.add(start);
        queue.add(start);
        while (!queue.isEmpty() && seen.size() < limit) {
            Point cell = queue.poll();
            for (Direction direction : Direction.values()) {
                Point next = Board.stepWrapped(cell, direction);
                if (!seen.contains(next) && state.isFreeForAi(next)) {
                    seen.add(next);
                    queue.add(next);
                }
            }
        }
        return seen.size();
    }
}
