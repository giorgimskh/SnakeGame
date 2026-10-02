package snake.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A snake: its segments (head first) and the direction it is heading. */
public final class Snake {
    private final List<Point> body = new ArrayList<>();
    private Direction direction;

    public Snake(Direction direction, Point... segments) {
        this.direction = direction;
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

    /** Turns towards {@code d} unless that would reverse straight back into the body. */
    public void turn(Direction d) {
        if (d != direction.opposite()) {
            direction = d;
        }
    }

    public void addHead(Point p) {
        body.add(0, p);
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
