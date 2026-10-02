package snake.game;

import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import snake.ai.AiSnakeController;
import snake.audio.SoundManager;
import snake.model.Direction;
import snake.model.GameState;
import snake.model.Level;
import snake.model.TickResult;
import snake.persistence.HighScoreStore;

/**
 * Runs games: it owns the current {@link GameState}, drives it from Swing timers according to
 * the level's rules, plays sounds and keeps track of the high score. The UI listens through
 * {@link Listener}.
 */
public final class GameController {
    public interface Listener {
        /** The game state changed. Refresh the HUD and the board. */
        void onUpdate();

        /** The game stopped with {@code result}. The listener decides what happens next. */
        void onGameEnded(TickResult result);
    }

    private static final int AI_MOVE_DELAY_MS = 150;
    private static final int ITEM_VISIBLE_MS = 4000;
    private static final int ITEM_RESPAWN_DELAY_MS = 1000;

    private final SoundManager sounds;
    private final HighScoreStore highScores;
    private final Listener listener;
    private final TimerSet timers = new TimerSet();
    private final Random random = new Random();
    private final GameState.Events events = new StateEvents();

    private GameState state;
    private boolean running;
    private boolean paused;
    private long pausedAt;
    private long foodShownAt; // when the vanishing apple's visible time last started
    private int highScore;

    private Timer appleTimer;
    private Timer aiTimer;
    private Timer multiplierTimer;

    public GameController(SoundManager sounds, HighScoreStore highScores, Listener listener) {
        this.sounds = sounds;
        this.highScores = highScores;
        this.listener = listener;
        this.highScore = highScores.resetOnStartup();
    }

    public void start(Level level) {
        timers.stopAll();
        state = new GameState(level, random, System.currentTimeMillis(), events);
        state.spawnFood(); // also starts the vanishing-apple cycle on levels that have one
        running = true;
        paused = false;

        sounds.startMusic();

        timers.repeat(level.tickDelayMs(), this::tick);

        if (level.hasBomb()) {
            startBombCycle();
        }
        if (level.hasAiSnake()) {
            aiTimer = timers.repeat(AI_MOVE_DELAY_MS, this::aiTick);
        }

        listener.onUpdate();
    }

    /** Stops the current game without a result, e.g. when the player returns to the menu. */
    public void stop() {
        // A game abandoned part-way still counts towards the high score
        if (running) {
            recordHighScore();
        }
        running = false;
        paused = false;
        timers.stopAll();
        sounds.stopMusic();
    }

    public void turn(Direction direction) {
        if (!paused) {
            state.getPlayer().turn(direction);
        }
    }

    /** Freezes or unfreezes the running game, including every timer and countdown. */
    public void togglePause() {
        if (!running) {
            return;
        }
        long now = System.currentTimeMillis();
        if (paused) {
            paused = false;
            state.shiftClock(now - pausedAt);
            timers.resumeAll();
            // resumeAll() restarts the apple's hide timer with its full delay, so restart its countdown too
            if (state.getLevel().hasVanishingApple() && state.isFoodVisible()) {
                foodShownAt = now;
            }
            // A resumed one-shot timer waits its full delay again, so give the 2x the time it had left
            if (state.isMultiplierActive()) {
                startMultiplierTimer((int) state.getMultiplierRemainingMs(now));
            }
            sounds.resumeMusic();
        } else {
            paused = true;
            pausedAt = now;
            timers.pauseAll();
            sounds.stopMusic();
        }
        listener.onUpdate();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    /** How much of the vanishing apple's visible time is left, from 1 down to 0. Frozen while paused. */
    public float getFoodLifeFraction() {
        long now = paused ? pausedAt : System.currentTimeMillis();
        float left = 1f - (float) (now - foodShownAt) / ITEM_VISIBLE_MS;
        return Math.max(0f, Math.min(1f, left));
    }

    /** The current or most recent game. Null before the first game starts. */
    public GameState getState() {
        return state;
    }

    public int getHighScore() {
        return highScore;
    }

    private void tick() {
        if (!running) {
            return;
        }
        TickResult result = state.tick(System.currentTimeMillis());
        if (result != TickResult.CONTINUE) {
            end(result);
            return;
        }
        listener.onUpdate();
    }

    private void aiTick() {
        if (!state.isAiAlive()) {
            return;
        }
        state.moveAi(AiSnakeController.chooseDirection(state));
        listener.onUpdate();
    }

    private void end(TickResult result) {
        running = false;
        timers.stopAll();
        if (result == TickResult.LEVEL_COMPLETE) {
            sounds.playLevelComplete();
        } else {
            sounds.playGameOver();
        }

        // A failed timed level does not count towards the high score
        if (result != TickResult.LEVEL_FAILED) {
            recordHighScore();
        }
        // Let the timer callback that ended the game return before the listener opens its modal
        // dialog. Otherwise every replay runs inside the previous game's dialog loop and they nest.
        SwingUtilities.invokeLater(() -> listener.onGameEnded(result));
    }

    private void recordHighScore() {
        if (state.getScore() > highScore) {
            highScore = state.getScore();
            highScores.save(highScore);
        }
    }

    /** Hides the apple after a while, then respawns it elsewhere, which starts the cycle again. */
    private void startAppleCycle() {
        timers.stop(appleTimer);
        appleTimer = timers.once(ITEM_VISIBLE_MS, () -> {
            state.setFoodVisible(false);
            timers.once(ITEM_RESPAWN_DELAY_MS, () -> {
                state.spawnFood();
                state.setFoodVisible(true);
            });
        });
        state.setFoodVisible(true);
        foodShownAt = System.currentTimeMillis();
    }

    /** Places a bomb, hides it after a while, then starts over somewhere else. */
    private void startBombCycle() {
        timers.once(ITEM_VISIBLE_MS, () -> {
            state.setBombVisible(false);
            timers.once(ITEM_RESPAWN_DELAY_MS, this::startBombCycle);
        });
        state.spawnBomb();
        state.setBombVisible(true);
    }

    /** Turns the 2x off after {@code delayMs}, replacing any earlier countdown. */
    private void startMultiplierTimer(int delayMs) {
        timers.stop(multiplierTimer);
        multiplierTimer = timers.once(delayMs, () -> {
            state.deactivateMultiplier();
            listener.onUpdate();
        });
    }

    private final class StateEvents implements GameState.Events {
        @Override
        public void appleEaten() {
            sounds.playEat();
        }

        @Override
        public void multiplierActivated() {
            startMultiplierTimer((int) GameState.MULTIPLIER_DURATION_MS);
            sounds.playMultiplier();
        }

        @Override
        public void foodSpawned() {
            if (state.getLevel().hasVanishingApple()) {
                startAppleCycle();
            }
        }

        @Override
        public void aiKilled() {
            timers.stop(aiTimer);
        }
    }
}
