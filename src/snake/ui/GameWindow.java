package snake.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import snake.audio.SoundManager;
import snake.game.GameController;
import snake.model.GameState;
import snake.model.Level;
import snake.model.TickResult;
import snake.persistence.HighScoreStore;

/**
 * The application window. The center switches between the menu and the board, and the HUD sits
 * above the board during a game. The window is re-packed on each switch, so it fits the 600×720
 * menu and the 600×600 board.
 */
public final class GameWindow extends JFrame implements GameController.Listener {
    private static final String NEXT_LEVEL = "Next Level";
    private static final String RETRY = "Retry";
    private static final String MENU = "Menu";

    private final GameController controller;
    private final HudPanel hudPanel;
    private final MainMenuPanel menuPanel;
    private final GamePanel gamePanel;

    public GameWindow() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        HighScoreStore highScores = new HighScoreStore(new File("highscore.txt"));
        controller = new GameController(new SoundManager(), highScores, this);

        hudPanel = new HudPanel();
        menuPanel = new MainMenuPanel(this::startLevel);
        gamePanel = new GamePanel(controller);

        setLayout(new BorderLayout());
        add(hudPanel, BorderLayout.NORTH);

        KeyboardInput.install(getRootPane(), controller, this::showMainMenu);

        showMainMenu();
        setLocationRelativeTo(null);
    }

    private void showMainMenu() {
        controller.stop();
        hudPanel.setVisible(false);
        menuPanel.setHighScore(controller.getHighScore());
        showCenter(menuPanel);
        pack();
        menuPanel.focusFirstCard();
    }

    private void startLevel(Level level) {
        hudPanel.setVisible(true);
        showCenter(gamePanel);
        controller.start(level);
        // Pack after start() has filled in the HUD, so its height is known
        pack();
    }

    private void showCenter(JPanel panel) {
        getContentPane().remove(panel == menuPanel ? gamePanel : menuPanel);
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
