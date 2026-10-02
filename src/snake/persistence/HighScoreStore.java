package snake.persistence;

import java.io.File;
import java.io.PrintWriter;

/** Stores the high score in a text file. Write errors are ignored. */
public final class HighScoreStore {
    private final File file;

    public HighScoreStore(File file) {
        this.file = file;
    }

    public void save(int highScore) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(highScore);
        } catch (Exception ignored) {
            // Keep the in-memory high score if the file can't be written
        }
    }

    /** The high score starts at 0 on every launch, on purpose. */
    public int resetOnStartup() {
        save(0);
        return 0;
    }
}
