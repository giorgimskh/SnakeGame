package snake.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/** Stores {@link Settings} in a properties file. A missing or unreadable file gives the defaults. */
public final class SettingsStore {
    private static final String MUSIC = "music";
    private static final String SOUND_EFFECTS = "soundEffects";
    private static final String SNAKE_COLOR = "snakeColor";

    private final File file;

    public SettingsStore(File file) {
        this.file = file;
    }

    public Settings load() {
        Settings settings = new Settings();
        if (!file.exists()) {
            return settings;
        }
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (Exception e) {
            return settings;
        }
        settings.setMusic(Boolean.parseBoolean(props.getProperty(MUSIC, "true")));
        settings.setSoundEffects(Boolean.parseBoolean(props.getProperty(SOUND_EFFECTS, "true")));
        settings.setSnakeColor(props.getProperty(SNAKE_COLOR, Settings.DEFAULT_SNAKE_COLOR));
        return settings;
    }

    public void save(Settings settings) {
        Properties props = new Properties();
        props.setProperty(MUSIC, String.valueOf(settings.isMusic()));
        props.setProperty(SOUND_EFFECTS, String.valueOf(settings.isSoundEffects()));
        props.setProperty(SNAKE_COLOR, settings.getSnakeColor());
        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Snake Game settings");
        } catch (Exception ignored) {
            // Keep the in-memory settings if the file can't be written
        }
    }
}
