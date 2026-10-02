package snake.game;

import java.util.Random;

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
        running = false;
        timers.stopAll();
        sounds.stopMusic();
    }

    public void turn(Direction direction) {
        state.getPlayer().turn(direction);
    }

    public boolean isRunning() {
        return running;
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

        // A failed timed level does not count towards the high score
        if (result != TickResult.LEVEL_FAILED && state.getScore() > highScore) {
            highScore = state.getScore();
            highScores.save(highScore);
        }
        listener.onGameEnded(result);
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

    private final class StateEvents implements GameState.Events {
        @Override
        public void appleEaten() {
            sounds.playEat();
        }

        @Override
        public void multiplierActivated() {
            timers.stop(multiplierTimer);
            multiplierTimer = timers.once((int) GameState.MULTIPLIER_DURATION_MS, () -> {
                state.deactivateMultiplier();
                listener.onUpdate();
            });
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
