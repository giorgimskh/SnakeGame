package snake.audio;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;

/**
 * Background music and sound effects, loaded from the classpath ({@code /sounds/...}), which is
 * how the jar, CheerpJ, IntelliJ and run.sh all provide them. Only background.wav ships with the
 * game. An effect without a .wav file uses a short generated tone instead. If there is no audio
 * output, that is logged once and the game stays silent.
 */
public final class SoundManager {
    private static final float TONE_SAMPLE_RATE = 22050f;

    // Written by the music loader thread as well as the EDT
    private volatile boolean audioAvailable = true; // false once no audio output could be opened
    private volatile boolean musicEnabled = true;
    private volatile boolean musicWanted; // a game wants the music playing, even if it hasn't loaded yet
    private boolean effectsEnabled = true;

    private volatile Clip backgroundMusic; // null until the loader thread has opened it
    private final Clip eatingSound;
    private final Clip multiplierSound;
    private final Clip gameOverSound;
    private final Clip levelCompleteSound;

    public SoundManager() {
        eatingSound = loadEffect("sounds/eat.wav", 880, 70);
        multiplierSound = loadEffect("sounds/multiplier.wav", 1320, 160);
        gameOverSound = loadEffect("sounds/gameover.wav", 196, 450);
        levelCompleteSound = loadEffect("sounds/levelcomplete.wav", 1047, 350);
        // The music is the one large file and decoding it is slow (especially in CheerpJ), so it loads
        // in the background and the window doesn't wait for it
        Thread loader = new Thread(this::loadMusicInBackground, "music-loader");
        loader.setDaemon(true);
        loader.start();
    }

    private void loadMusicInBackground() {
        Clip clip = loadMusic("sounds/background.wav");
        SwingUtilities.invokeLater(() -> {
            backgroundMusic = clip;
            if (musicWanted) {
                startMusic(); // A game started before the music was ready
            }
        });
    }

    private Clip loadMusic(String path) {
        AudioInputStream stream = openResource(path);
        if (stream == null) {
            System.out.println("Background music not found on the classpath: /" + path);
            return null;
        }
        return openClip(stream);
    }

    /** Loads {@code path}, or falls back to a tone of {@code frequencyHz} lasting {@code durationMs}. */
    private Clip loadEffect(String path, int frequencyHz, int durationMs) {
        AudioInputStream stream = openResource(path);
        return openClip(stream != null ? stream : tone(frequencyHz, durationMs));
    }

    /** The classpath resource {@code path} as 16-bit PCM, or null if it is missing or unreadable. */
    private static AudioInputStream openResource(String path) {
        URL url = SoundManager.class.getResource("/" + path);
        if (url == null) {
            return null;
        }
        try {
            return toPcm16(AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream())));
        } catch (UnsupportedAudioFileException | IOException | IllegalArgumentException e) {
            System.out.println("Can't read /" + path + ": " + e.getMessage());
            return null;
        }
    }

    /** Converts {@code in} to signed 16-bit little-endian PCM, the format audio lines support most widely. */
    private static AudioInputStream toPcm16(AudioInputStream in) {
        AudioFormat source = in.getFormat();
        if (source.getEncoding() == AudioFormat.Encoding.PCM_SIGNED
                && source.getSampleSizeInBits() == 16 && !source.isBigEndian()) {
            return in;
        }
        AudioFormat target = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, source.getSampleRate(), 16,
                source.getChannels(), source.getChannels() * 2, source.getSampleRate(), false);
        return AudioSystem.getAudioInputStream(target, in);
    }

    /** Opens a clip for exactly {@code stream}'s format, or returns null if there's no audio output. */
    private Clip openClip(AudioInputStream stream) {
        try (AudioInputStream in = stream) {
            if (!audioAvailable) {
                return null;
            }
            DataLine.Info info = new DataLine.Info(Clip.class, in.getFormat());
            if (!AudioSystem.isLineSupported(info)) {
                audioAvailable = false;
                System.out.println("Sound disabled: no audio output device supports " + in.getFormat());
                return null;
            }
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(in);
            return clip;
        } catch (LineUnavailableException | IOException | IllegalArgumentException e) {
            audioAvailable = false;
            System.out.println("Sound disabled: can't open the audio output (" + e.getMessage() + ")");
            return null;
        }
    }

    /** A sine tone that fades out, so it ends without a click. 16-bit mono PCM. */
    private static AudioInputStream tone(int frequencyHz, int durationMs) {
        int samples = (int) (TONE_SAMPLE_RATE * durationMs / 1000);
        byte[] data = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double fade = 1.0 - (double) i / samples;
            short value = (short) (Math.sin(2 * Math.PI * frequencyHz * i / TONE_SAMPLE_RATE) * 6000 * fade);
            data[i * 2] = (byte) value;
            data[i * 2 + 1] = (byte) (value >> 8);
        }
        AudioFormat format = new AudioFormat(TONE_SAMPLE_RATE, 16, 1, true, false);
        return new AudioInputStream(new ByteArrayInputStream(data), format, samples);
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

    private void play(Clip clip) {
        if (clip != null && effectsEnabled) {
            clip.stop(); // So a sound that is still playing starts over
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /** Turns the background music on or off. Turning it off stops it; it starts with the next game. */
    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) {
            stopMusic();
        }
    }

    /** Turns the sound effects on or off. */
    public void setEffectsEnabled(boolean enabled) {
        effectsEnabled = enabled;
    }

    public void startMusic() {
        musicWanted = true;
        if (backgroundMusic != null && musicEnabled) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Continues the music from where {@link #stopMusic()} left it. */
    public void resumeMusic() {
        musicWanted = true;
        if (backgroundMusic != null && musicEnabled) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        musicWanted = false;
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}
