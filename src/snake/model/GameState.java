package snake.model;

import java.awt.Point;
import java.util.Random;

/**
 * State and rules of a single game, from level start until it ends. It does not use Swing:
 * the controller drives it from timers and reacts to its {@link Events}.
 */
public final class GameState {
    /** Things the controller reacts to, such as sounds and timers. */
    public interface Events {
        void appleEaten();

        void multiplierActivated();

        void foodSpawned();

        void aiKilled();
    }

    public static final long MULTIPLIER_DURATION_MS = 10_000;

    private static final int APPLE_POINTS = 10;
    private static final double MULTIPLIER_CHANCE = 0.2;
    private static final int AI_APPLE_POINTS = 10;
    private static final int AI_SELF_COLLISION_REWARD = 30;
    private static final int AI_BOMB_REWARD = 30;
    private static final int AI_COLLISION_REWARD = 50;

    private final Level level;
    private final Random random;
    private final Events events;
    private final long startTime;

    private final Snake player;
    private final Snake ai; // null unless the level has an AI snake
    private boolean aiAlive;

    private Point food;
    private FoodType foodType = FoodType.APPLE;
    private boolean foodVisible = true;

    private Point bomb;
    private boolean bombVisible;

    private int score;
    private int aiScore;
    private boolean multiplierActive;
    private long multiplierStartTime;

    public GameState(Level level, Random random, long startTime, Events events) {
        this.level = level;
        this.random = random;
        this.startTime = startTime;
        this.events = events;

        int u = Board.UNIT;
        int cx = Board.WIDTH / 2;
        int cy = Board.HEIGHT / 2;
        player = new Snake(Direction.RIGHT,
                new Point(cx, cy), new Point(cx - u, cy), new Point(cx - u * 2, cy), new Point(cx - u * 3, cy));

        if (level.hasAiSnake()) {
            int w = Board.WIDTH;
            int y = Board.HEIGHT - u * 4;
            ai = new Snake(Direction.LEFT,
                    new Point(w - u * 4, y), new Point(w - u * 3, y), new Point(w - u * 2, y), new Point(w - u, y));
            aiAlive = true;
        } else {
            ai = null;
        }
    }

    /** Moves the player one step and applies every rule. */
    public TickResult tick(long now) {
        TickResult levelResult = checkLevelConditions(now);
        if (levelResult != TickResult.CONTINUE) {
            return levelResult;
        }

        player.addHead(Board.stepWrapped(player.head(), player.direction()));
        if (!eatFood(now)) {
            player.removeTail();
        }
        if (player.hitsItself()) {
            return TickResult.PLAYER_DIED;
        }

        if (level.hasBomb() && bomb != null && bombVisible) {
            if (player.head().equals(bomb)) {
                return TickResult.PLAYER_DIED;
            }
            if (aiAlive && ai.head().equals(bomb)) {
                killAi(AI_BOMB_REWARD);
            }
        }

        return checkAiCollision();
    }

    private TickResult checkLevelConditions(long now) {
        if (level.hasTimeLimit()) {
            if (now - startTime >= level.timeLimitMs()) {
                return score >= Level.WIN_SCORE ? TickResult.LEVEL_COMPLETE : TickResult.LEVEL_FAILED;
            }
            return TickResult.CONTINUE;
        }
        return score >= Level.WIN_SCORE ? TickResult.LEVEL_COMPLETE : TickResult.CONTINUE;
    }

    private boolean eatFood(long now) {
        if (!player.head().equals(food) || !foodVisible) {
            return false;
        }
        if (foodType == FoodType.MULTIPLIER) {
            multiplierActive = true;
            multiplierStartTime = now;
            events.multiplierActivated();
        } else {
            score += multiplierActive ? APPLE_POINTS * 2 : APPLE_POINTS;
            events.appleEaten();
        }
        spawnFood();
        return true;
    }

    private TickResult checkAiCollision() {
        if (!aiAlive) {
            return TickResult.CONTINUE;
        }
        Point playerHead = player.head();
        Point aiHead = ai.head();

        // Head-on collision counts as an AI loss, for fairness
        if (playerHead.equals(aiHead)) {
            killAi(AI_COLLISION_REWARD);
            return TickResult.CONTINUE;
        }
        if (ai.contains(playerHead)) {
            return TickResult.PLAYER_DIED;
        }
        if (player.contains(aiHead)) {
            killAi(AI_COLLISION_REWARD);
        }
        return TickResult.CONTINUE;
    }

    /** Moves the AI snake one step in {@code direction}. */
    public void moveAi(Direction direction) {
        ai.setDirection(direction);
        Point head = Board.stepWrapped(ai.head(), direction);
        ai.addHead(head);

        if (ai.hitsItself()) {
            killAi(AI_SELF_COLLISION_REWARD);
            return;
        }

        if (head.equals(food) && foodVisible) {
            aiScore += AI_APPLE_POINTS;
            spawnFood();
        } else {
            ai.removeTail();
        }
    }

    private void killAi(int playerReward) {
        aiAlive = false;
        events.aiKilled();
        score += Math.max(0, playerReward);
    }

    /** Puts the food on a random free cell. 20% of the time it is a multiplier. */
    public void spawnFood() {
        Point p;
        do {
            p = Board.randomCell(random);
        } while (player.contains(p));

        food = p;
        foodType = random.nextDouble() < MULTIPLIER_CHANCE ? FoodType.MULTIPLIER : FoodType.APPLE;
        events.foodSpawned();
    }

    /** Puts the bomb on a random cell that is not under the player or the food. */
    public void spawnBomb() {
        Point p;
        do {
            p = Board.randomCell(random);
        } while (player.contains(p) || p.equals(food));

        bomb = p;
    }

    public void deactivateMultiplier() {
        multiplierActive = false;
    }

    public void setFoodVisible(boolean foodVisible) {
        this.foodVisible = foodVisible;
    }

    public void setBombVisible(boolean bombVisible) {
        this.bombVisible = bombVisible;
    }

    public Level getLevel() {
        return level;
    }

    public Snake getPlayer() {
        return player;
    }

    /** The AI snake, or null on levels without one. */
    public Snake getAi() {
        return ai;
    }

    public boolean isAiAlive() {
        return aiAlive;
    }

    public Point getFood() {
        return food;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public boolean isFoodVisible() {
        return foodVisible;
    }

    public Point getBomb() {
        return bomb;
    }

    public boolean isBombVisible() {
        return bombVisible;
    }

    public int getScore() {
        return score;
    }

    public int getAiScore() {
        return aiScore;
    }

    public boolean isMultiplierActive() {
        return multiplierActive;
    }

    public long getRemainingTimeMs(long now) {
        return Math.max(0, level.timeLimitMs() - (now - startTime));
    }

    public long getMultiplierSecondsLeft(long now) {
        return Math.max(0, MULTIPLIER_DURATION_MS / 1000 - (now - multiplierStartTime) / 1000);
    }
}
