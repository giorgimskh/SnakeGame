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
 * The application window. The HUD sits at the top, and the center switches between the menu
 * and the board.
 */
public final class GameWindow extends JFrame implements GameController.Listener {
    private final GameController controller;
    private final HudPanel hudPanel;
    private final MenuPanel menuPanel;
    private final GamePanel gamePanel;

    public GameWindow() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        HighScoreStore highScores = new HighScoreStore(new File("highscore.txt"));
        controller = new GameController(new SoundManager(), highScores, this);

        hudPanel = new HudPanel();
        menuPanel = new MenuPanel(this::startLevel);
        gamePanel = new GamePanel(controller);

        setLayout(new BorderLayout());
        add(hudPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);

        // Size the frame around the 600x600 center panel
        pack();
        setLocationRelativeTo(null);

        KeyboardInput.install(getRootPane(), controller, this::showMainMenu);

        showMainMenu();
    }

    private void showMainMenu() {
        controller.stop();
        showCenter(menuPanel);
        hudPanel.showMenu(controller.getHighScore());
    }

    private void startLevel(Level level) {
        showCenter(gamePanel);
        controller.start(level);
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
                        + "\nHigh Score: " + highScore + "\nWould you like to play again?";
                break;
            case LEVEL_FAILED:
                title = "Level Failed!";
                message = "Level " + level.number() + " Failed!\nTime's up! Your score: " + score
                        + "\nYou need " + Level.WIN_SCORE + " points to win!\nWould you like to try again?";
                break;
            default:
                title = "Game Over";
                message = "Game Over! Your score: " + score + "\nHigh Score: " + highScore
                        + "\nWould you like to play again?";
                break;
        }

        int choice = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startLevel(level);
        } else {
            showMainMenu();
        }
    }
}
