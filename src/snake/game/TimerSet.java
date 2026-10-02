package snake.game;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

/** Creates Swing timers and keeps track of the ones that are still pending, so they can be stopped together. */
final class TimerSet {
    private final List<Timer> timers = new ArrayList<>();

    Timer repeat(int delayMs, Runnable action) {
        return start(delayMs, true, action);
    }

    Timer once(int delayMs, Runnable action) {
        return start(delayMs, false, action);
    }

    private Timer start(int delayMs, boolean repeats, Runnable action) {
        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            if (!repeats) {
                timers.remove(timer);
            }
            action.run();
        });
        timer.setRepeats(repeats);
        timers.add(timer);
        timer.start();
        return timer;
    }

    /** Stops the given timers. Null entries are skipped. */
    void stop(Timer... toStop) {
        for (Timer timer : toStop) {
            if (timer != null) {
                timer.stop();
                timers.remove(timer);
            }
        }
    }
}
