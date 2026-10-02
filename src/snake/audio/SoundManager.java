package snake.audio;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Background music and sound effects. Only sounds/background.wav ships with the game. An effect
 * without a .wav file uses a short generated tone instead, and a clip that can't be created at
 * all is logged and stays silent.
 */
public final class SoundManager {
    private static final float TONE_SAMPLE_RATE = 22050f;

    private final Clip backgroundMusic;
    private final Clip eatingSound;
    private final Clip multiplierSound;
    private final Clip gameOverSound;
    private final Clip levelCompleteSound;

    public SoundManager() {
        backgroundMusic = loadBackgroundMusic();
        eatingSound = loadEffect("sounds/eat.wav", "Eating", 880, 70);
        multiplierSound = loadEffect("sounds/multiplier.wav", "Multiplier", 1320, 160);
        gameOverSound = loadEffect("sounds/gameover.wav", "Game over", 196, 450);
        levelCompleteSound = loadEffect("sounds/levelcomplete.wav", "Level complete", 1047, 350);
    }

    /**
     * Loads from the classpath first, which works in the jar and CheerpJ. Falls back to
     * ./, src/ and bin/ for local runs.
     */
    private AudioInputStream loadAudioFromResourcesOrFile(String relativePath) throws Exception {
        try {
            URL url = getClass().getClassLoader().getResource(relativePath);
            if (url != null) {
                return AudioSystem.getAudioInputStream(url);
            }
        } catch (Exception ignored) {
        }
        File file = new File(relativePath);
        if (!file.exists()) {
            file = new File("src/" + relativePath);
            if (!file.exists()) {
                file = new File("bin/" + relativePath);
            }
        }
        return AudioSystem.getAudioInputStream(file);
    }

    private Clip loadBackgroundMusic() {
        try {
            AudioInputStream audioIn = loadAudioFromResourcesOrFile("sounds/background.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.setFramePosition(0);
            return clip;
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("I/O error: " + e.getMessage());
        }
        return null;
    }

    /** Loads {@code path}, or falls back to a tone of {@code frequencyHz} lasting {@code durationMs}. */
    private Clip loadEffect(String path, String name, int frequencyHz, int durationMs) {
        try {
            AudioInputStream audioIn = loadAudioFromResourcesOrFile(path);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            System.out.println(name + " sound loaded");
            return clip;
        } catch (Exception e) {
            return tone(name, frequencyHz, durationMs);
        }
    }

    /** A sine tone that fades out, so it ends without a click. 16-bit mono PCM. */
    private static Clip tone(String name, int frequencyHz, int durationMs) {
        int samples = (int) (TONE_SAMPLE_RATE * durationMs / 1000);
        byte[] data = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double fade = 1.0 - (double) i / samples;
            short value = (short) (Math.sin(2 * Math.PI * frequencyHz * i / TONE_SAMPLE_RATE) * 6000 * fade);
            data[i * 2] = (byte) value;
            data[i * 2 + 1] = (byte) (value >> 8);
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(new AudioFormat(TONE_SAMPLE_RATE, 16, 1, true, false), data, 0, data.length);
            return clip;
        } catch (Exception e) {
            System.out.println("Failed to create " + name.toLowerCase() + " sound: " + e.getMessage());
            return null;
        }
    }

    public void playEat() {
        play(eatingSound);
    }

    public void playMultiplier() {
        play(multiplierSound);
    }

    public void playGameOver() {
        play(gameOverSound);
    }

    public void playLevelComplete() {
        play(levelCompleteSound);
    }

    private static void play(Clip clip) {
        if (clip != null) {
            clip.stop(); // So a sound that is still playing starts over
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void startMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
            System.out.println("Background music started");
        } else {
            System.out.println("Background music Clip is null");
        }
    }

    /** Continues the music from where {@link #stopMusic()} left it. */
    public void resumeMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}
