package snake.persistence;

/** The player's settings. Starts with the defaults; {@link SettingsStore} loads and saves them. */
public final class Settings {
    public static final String DEFAULT_SNAKE_COLOR = "GREEN";

    private boolean music = true;
    private boolean soundEffects = true;
    private String snakeColor = DEFAULT_SNAKE_COLOR;

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isSoundEffects() {
        return soundEffects;
    }

    public void setSoundEffects(boolean soundEffects) {
        this.soundEffects = soundEffects;
    }

    /** The name of the player's snake color, e.g. "GREEN". The UI maps it to colors. */
    public String getSnakeColor() {
        return snakeColor;
    }

    public void setSnakeColor(String snakeColor) {
        this.snakeColor = snakeColor;
    }
}
