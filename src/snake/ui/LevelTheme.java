package snake.ui;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import snake.model.Level;
import snake.ui.render.Decorations;

/**
 * Each level's look: its menu card, its board tiles and decorations, and whether the board is
 * dark (Space), which changes a few colors so things stay visible.
 */
final class LevelTheme {
    private static final Map<Level, LevelTheme> THEMES = new EnumMap<>(Level.class);

    static {
        THEMES.put(Level.LEVEL_1, new LevelTheme("Time attack, 3 minutes",
                0xF3C77A, 0xD9A24A, 0x4A2E05, 0x7A5212, BiomeScenes::desert,
                0xEFCF92, 0xEBC987, Decorations::desert, false));
        THEMES.put(Level.LEVEL_2, new LevelTheme("Faster pace, first to 300 wins",
                0xB6DD8A, 0x86B85A, 0x1F3D0E, 0x3D6421, BiomeScenes::grass,
                0x93CF63, 0x8AC85A, Decorations::grass, false));
        THEMES.put(Level.LEVEL_3, new LevelTheme("Speed and vanishing food",
                0x9FD3EE, 0x5EA9D4, 0x0C3350, 0x275A7C, BiomeScenes::ocean,
                0x8CCDEC, 0x84C6E6, Decorations::ocean, false));
        THEMES.put(Level.LEVEL_4, new LevelTheme("Speed, vanishing food, a bomb",
                0x8FC3A0, 0x4F8F63, 0x0F2E1A, 0x2C5A3A, BiomeScenes::forest,
                0x86BD8F, 0x7FB688, Decorations::forest, false));
        THEMES.put(Level.LEVEL_5, new LevelTheme("Race an AI snake, dodge the bomb",
                0x3A3566, 0x26224A, 0xF2EEFF, 0xC4BDF0, BiomeScenes::space,
                0x2E2A58, 0x2B2752, Decorations::space, true));
    }

    final String subtitle;
    final Color cardFill;
    final Color cardBorder;
    final Color cardTitle;
    final Color cardSubtitle;
    final BiomeCard.Scene cardScene;
    final Color tileA;
    final Color tileB;
    final Decorations.Decorator decorations;
    final boolean dark;

    private LevelTheme(String subtitle, int cardFill, int cardBorder, int cardTitle, int cardSubtitle,
                       BiomeCard.Scene cardScene, int tileA, int tileB, Decorations.Decorator decorations,
                       boolean dark) {
        this.subtitle = subtitle;
        this.cardFill = new Color(cardFill);
        this.cardBorder = new Color(cardBorder);
        this.cardTitle = new Color(cardTitle);
        this.cardSubtitle = new Color(cardSubtitle);
        this.cardScene = cardScene;
        this.tileA = new Color(tileA);
        this.tileB = new Color(tileB);
        this.decorations = decorations;
        this.dark = dark;
    }

    /** To add a level, add its theme above, its card scene in {@code BiomeScenes} and its decorations in {@code Decorations}. */
    static LevelTheme of(Level level) {
        return THEMES.get(level);
    }

    /** Fixed per level, so its decorations land in the same places every time. */
    static long decorationSeed(Level level) {
        return 1000L + level.number();
    }

    /** "1 · Desert" */
    static String title(Level level) {
        return level.number() + " · " + level.themeName();
    }
}
