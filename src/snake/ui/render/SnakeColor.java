package snake.ui.render;

import java.awt.Color;

/** The colors the player can pick for their snake in Settings. Red is left out; that's the AI. */
public enum SnakeColor {
    //        name      outline   dark outline body      belly     pupil
    GREEN("Green",   0x2F6B3A, 0x173D1E, 0x5FB35A, 0x8FD47A, 0x173D1E),
    BLUE("Blue",     0x1F4E79, 0x0F2A44, 0x4A90D9, 0x8CC4F2, 0x0F2A44),
    ORANGE("Orange", 0x8A4A12, 0x4A2706, 0xF0973A, 0xF8C27E, 0x4A2706),
    PURPLE("Purple", 0x4B2F7A, 0x26184A, 0x8E6AD8, 0xBFA8F0, 0x26184A);

    private final String displayName;
    private final SnakePalette onLight;
    private final SnakePalette onDark;

    SnakeColor(String displayName, int outline, int darkOutline, int body, int belly, int pupil) {
        this.displayName = displayName;
        this.onLight = new SnakePalette(outline, body, belly, pupil, false);
        // A darker outline on dark boards so the tube keeps its edge
        this.onDark = new SnakePalette(darkOutline, body, belly, pupil, false);
    }

    public String displayName() {
        return displayName;
    }

    public SnakePalette palette(boolean darkBoard) {
        return darkBoard ? onDark : onLight;
    }

    public Color swatch() {
        return onLight.body;
    }

    /** The color named {@code name}, or green if the name is unknown. */
    public static SnakeColor fromName(String name) {
        for (SnakeColor color : values()) {
            if (color.name().equals(name)) {
                return color;
            }
        }
        return GREEN;
    }
}
