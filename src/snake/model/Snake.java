package snake.model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/** A snake: its segments (head first) and the direction it is heading. */
public final class Snake {
    /** Turns pressed faster than the snake moves are kept, up to this many. */
    private static final int MAX_QUEUED_TURNS = 2;

    private final List<Point> body = new ArrayList<>();
    private final Deque<Direction> queuedTurns = new ArrayDeque<>();
    private Direction direction;
    private Direction lastMoved; // direction of the most recent step

    public Snake(Direction direction, Point... segments) {
        this.direction = direction;
        this.lastMoved = direction;
        Collections.addAll(body, segments);
    }

    public Point head() {
        return body.get(0);
    }

    /** Segments from head to tail, read-only. */
    public List<Point> segments() {
        return Collections.unmodifiableList(body);
    }

    public int size() {
        return body.size();
    }

    public boolean contains(Point p) {
        return body.contains(p);
    }

    public Direction direction() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Queues a turn towards {@code d}, to be taken on a later step. Each turn is checked against
     * the one before it (or the last step taken), so it can never reverse straight back into the
     * body, and quick presses like up-then-left are both kept instead of the first being lost.
     */
    public void turn(Direction d) {
        Direction previous = queuedTurns.isEmpty() ? lastMoved : queuedTurns.peekLast();
        if (queuedTurns.size() < MAX_QUEUED_TURNS && d != previous && d != previous.opposite()) {
            queuedTurns.addLast(d);
        }
    }

    /** Takes the next queued turn, if any, and returns the direction to step in. */
    public Direction takeTurn() {
        if (!queuedTurns.isEmpty()) {
            direction = queuedTurns.pollFirst();
        }
        return direction;
    }

    /** Adds {@code p} as the new head, a step in the current direction. */
    public void addHead(Point p) {
        body.add(0, p);
        lastMoved = direction;
    }

    public void removeTail() {
        body.remove(body.size() - 1);
    }

    /** True if the head overlaps any other segment. */
    public boolean hitsItself() {
        Point head = head();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }
}
