package snake.model;

/**
 * Every level and its rules. To add or change a level, edit this enum, then add its
 * background in {@code ThemePainter} and its menu button colors in {@code MenuPanel}.
 */
public enum Level {
    //        n  menu title                                     theme     delay  limit    vanish bomb   ai
    LEVEL_1(1, "LEVEL 1 - Desert Time Attack (3 min)",        "Desert", 100, 180_000, false, false, false),
    LEVEL_2(2, "LEVEL 2 - Grass Speed Challenge",             "Grass",   80,       0, false, false, false),
    LEVEL_3(3, "LEVEL 3 - Ocean Speed + Vanishing",           "Ocean",   70,       0, true,  false, false),
    LEVEL_4(4, "LEVEL 4 - Forest Speed + Vanishing + Bomb",   "Forest",  60,       0, true,  true,  false),
    LEVEL_5(5, "LEVEL 5 - Space AI Snake Battle + Bomb",      "Space",   70,       0, true,  true,  true);

    /** Points needed to win a level. On a timed level, this is checked when time runs out. */
    public static final int WIN_SCORE = 300;

    private final int number;
    private final String menuTitle;
    private final String themeName;
    private final int tickDelayMs;
    private final long timeLimitMs;
    private final boolean vanishingApple;
    private final boolean bomb;
    private final boolean aiSnake;

    Level(int number, String menuTitle, String themeName, int tickDelayMs, long timeLimitMs,
          boolean vanishingApple, boolean bomb, boolean aiSnake) {
        this.number = number;
        this.menuTitle = menuTitle;
        this.themeName = themeName;
        this.tickDelayMs = tickDelayMs;
        this.timeLimitMs = timeLimitMs;
        this.vanishingApple = vanishingApple;
        this.bomb = bomb;
        this.aiSnake = aiSnake;
    }

    public int number() {
        return number;
    }

    public String menuTitle() {
        return menuTitle;
    }

    public String themeName() {
        return themeName;
    }

    /** Delay between player moves; lower is faster. */
    public int tickDelayMs() {
        return tickDelayMs;
    }

    /** 0 means no time limit. */
    public long timeLimitMs() {
        return timeLimitMs;
    }

    public boolean hasTimeLimit() {
        return timeLimitMs > 0;
    }

    /** The apple disappears after a few seconds and reappears elsewhere. */
    public boolean hasVanishingApple() {
        return vanishingApple;
    }

    public boolean hasBomb() {
        return bomb;
    }

    public boolean hasAiSnake() {
        return aiSnake;
    }

    /** The level after this one, or null for the last level. */
    public Level next() {
        Level[] levels = values();
        return ordinal() + 1 < levels.length ? levels[ordinal() + 1] : null;
    }
}
