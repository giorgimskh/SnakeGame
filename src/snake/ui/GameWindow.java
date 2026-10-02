package snake.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import snake.audio.SoundManager;
import snake.game.GameController;
import snake.model.GameState;
import snake.model.Level;
import snake.model.TickResult;
import snake.persistence.HighScoreStore;
import snake.persistence.Settings;
import snake.persistence.SettingsStore;
import snake.ui.render.SnakeColor;

/**
 * The application window. The center switches between the menu, the settings screen and the
 * board, and the HUD sits above the board during a game. The window is re-packed on each switch,
 * so it fits the 600×720 menu and settings and the 600×600 board. The maximize button fills the
 * screen instead: the board scales up, and the menu and settings stay centered at their own size.
 */
public final class GameWindow extends JFrame implements GameController.Listener {
    private static final String NEXT_LEVEL = "Next Level";
    private static final String RETRY = "Retry";
    private static final String MENU = "Menu";

    private final GameController controller;
    private final SoundManager sounds;
    private final SettingsStore settingsStore;
    private final Settings settings;
    private final HudPanel hudPanel;
    private final MainMenuPanel menuPanel;
    private final SettingsPanel settingsPanel;
    private final GamePanel gamePanel;
    // The menu and settings, each centered in a parchment panel that fills a maximized window
    private final JPanel menuScreen;
    private final JPanel settingsScreen;

    public GameWindow() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Resizable, so the window has a maximize button
        setResizable(true);

        // The web page sets snake.dataDir to CheerpJ's persistent /files folder; otherwise the working directory
        String dataDir = System.getProperty("snake.dataDir");
        HighScoreStore highScores = new HighScoreStore(new File(dataDir, "highscore.txt"));
        sounds = new SoundManager();
        controller = new GameController(sounds, highScores, this);
        settingsStore = new SettingsStore(new File(dataDir, "settings.properties"));
        settings = settingsStore.load();

        hudPanel = new HudPanel();
        menuPanel = new MainMenuPanel(this::startLevel, this::showSettings);
        settingsPanel = new SettingsPanel(settings, this::settingsChanged, this::closeSettings);
        gamePanel = new GamePanel(controller);
        menuScreen = centered(menuPanel);
        settingsScreen = centered(settingsPanel);
        applySettings();

        setLayout(new BorderLayout());
        add(hudPanel, BorderLayout.NORTH);

        KeyboardInput.install(getRootPane(), controller, this::showMainMenu);

        // Leaving maximized: shrink back to fit whatever screen is showing now
        addWindowStateListener(e -> {
            if (isMaximized(e.getOldState()) && !isMaximized(e.getNewState())) {
                SwingUtilities.invokeLater(this::pack);
            }
        });

        showMainMenu();
        setLocationRelativeTo(null);
    }

    private static JPanel centered(JPanel panel) {
        JPanel screen = new JPanel(new GridBagLayout());
        screen.setBackground(Theme.PARCHMENT);
        screen.add(panel);
        return screen;
    }

    private static boolean isMaximized(int state) {
        return (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    /** Sizes the window to the current screen, unless it is maximized and should stay full size. */
    private void fit() {
        if (isMaximized(getExtendedState())) {
            validate();
        } else {
            pack();
        }
    }

    private void showMainMenu() {
        openMenu();
        menuPanel.focusFirstCard();
    }

    private void openMenu() {
        controller.stop();
        hudPanel.setVisible(false);
        menuPanel.setHighScore(controller.getHighScore());
        showCenter(menuScreen);
        fit();
    }

    private void showSettings() {
        showCenter(settingsScreen);
        fit();
        settingsPanel.focusFirst();
    }

    private void closeSettings() {
        openMenu();
        menuPanel.focusSettingsButton();
    }

    /** A setting was changed on the settings screen: use it now and remember it. */
    private void settingsChanged() {
        applySettings();
        settingsStore.save(settings);
    }

    private void applySettings() {
        sounds.setMusicEnabled(settings.isMusic());
        sounds.setEffectsEnabled(settings.isSoundEffects());
        gamePanel.setPlayerColor(SnakeColor.fromName(settings.getSnakeColor()));
    }

    private void startLevel(Level level) {
        hudPanel.setVisible(true);
        showCenter(gamePanel);
        controller.start(level);
        // Fit after start() has filled in the HUD, so its height is known
        fit();
    }

    private void showCenter(JPanel panel) {
        Component current = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (current != null && current != panel) {
            getContentPane().remove(current);
        }
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void onUpdate() {
        hudPanel.update(controller.getState(), controller.getHighScore(), System.currentTimeMillis());
        gamePanel.repaint();
    }

    @Override
    public void onGameEnded(TickResult result) {
        GameState state = controller.getState();
        Level level = state.getLevel();
        int score = state.getScore();
        int highScore = controller.getHighScore();

        String message;
        String title;
        switch (result) {
            case LEVEL_COMPLETE:
                title = "Level Complete!";
                message = "Level " + level.number() + " Complete!\nYour score: " + score
                        + "\nHigh Score: " + highScore;
                break;
            case LEVEL_FAILED:
                title = "Level Failed!";
                message = "Level " + level.number() + " Failed!\nTime's up! Your score: " + score
                        + "\nYou need " + Level.WIN_SCORE + " points to win!";
                break;
            default:
                title = "Game Over";
                message = "Game Over! Your score: " + score + "\nHigh Score: " + highScore;
                break;
        }

        Level next = result == TickResult.LEVEL_COMPLETE ? level.next() : null;
        String[] options = next != null
                ? new String[] {NEXT_LEVEL, RETRY, MENU}
                : new String[] {RETRY, MENU};
        int choice = JOptionPane.showOptionDialog(this, message, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        // Closing the dialog counts as Menu
        String picked = choice >= 0 ? options[choice] : MENU;

        if (picked.equals(NEXT_LEVEL)) {
            startLevel(next);
        } else if (picked.equals(RETRY)) {
            startLevel(level);
        } else {
            showMainMenu();
        }
    }
}
