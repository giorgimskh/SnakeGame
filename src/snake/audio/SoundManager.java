package snake.audio;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Background music and sound effects. A clip that fails to load is logged and stays silent.
 * Only sounds/background.wav ships with the game.
 */
public final class SoundManager {
    private final Clip backgroundMusic;
    private final Clip eatingSound;
    private final Clip multiplierSound;

    public SoundManager() {
        backgroundMusic = loadBackgroundMusic();
        eatingSound = loadEffect("sounds/eat.wav", "Eating");
        multiplierSound = loadEffect("sounds/multiplier.wav", "Multiplier");
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

    private Clip loadEffect(String path, String name) {
        try {
            AudioInputStream audioIn = loadAudioFromResourcesOrFile(path);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            System.out.println(name + " sound loaded");
            return clip;
        } catch (Exception e) {
            System.out.println("Failed to load " + name.toLowerCase() + " sound: " + e.getMessage());
            return null;
        }
    }

    public void playEat() {
        play(eatingSound);
    }

    public void playMultiplier() {
        play(multiplierSound);
    }

    private static void play(Clip clip) {
        if (clip != null) {
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

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}
