package snake.ui.render;

import java.awt.Color;

/** The colors a snake is drawn in, and whether it gets the rival's angry eyebrows. */
public final class SnakePalette {
    public static final SnakePalette PLAYER = new SnakePalette(0x2F6B3A, 0x5FB35A, 0x8FD47A, 0x173D1E, false);
    /** The player on a dark board, with a darker outline so the tube keeps its edge. */
    public static final SnakePalette PLAYER_ON_DARK = new SnakePalette(0x173D1E, 0x5FB35A, 0x8FD47A, 0x173D1E, false);
    public static final SnakePalette AI = new SnakePalette(0x6E1820, 0xD8434A, 0xF28A8A, 0x2B0A0E, true);

    final Color outline;
    final Color body;
    final Color belly;
    final Color pupil;
    final boolean eyebrows;

    private SnakePalette(int outline, int body, int belly, int pupil, boolean eyebrows) {
        this.outline = new Color(outline);
        this.body = new Color(body);
        this.belly = new Color(belly);
        this.pupil = new Color(pupil);
        this.eyebrows = eyebrows;
    }
}
