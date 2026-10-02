package snake.ui;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

import snake.model.GameState;
import snake.model.Level;

/** The bar above the board during a game: level/time, player score, AI score, high score and 2x status. */
final class HudPanel extends JPanel {
    private final JLabel levelLabel = label(14);
    private final JLabel playerScoreLabel = label(16);
    private final JLabel aiScoreLabel = label(16);
    private final JLabel highScoreLabel = label(14);
    private final JLabel multiplierLabel = label(12);

    HudPanel() {
        super(new GridBagLayout());
        setOpaque(false);
        multiplierLabel.setForeground(new Color(0, 255, 127));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.gridy = 0;
        addLabel(levelLabel, gbc, 0, GridBagConstraints.WEST);
        addLabel(playerScoreLabel, gbc, 1, GridBagConstraints.CENTER);
        addLabel(aiScoreLabel, gbc, 2, GridBagConstraints.CENTER);
        addLabel(highScoreLabel, gbc, 3, GridBagConstraints.EAST);
        addLabel(multiplierLabel, gbc, 4, GridBagConstraints.EAST);
    }

    private static JLabel label(int fontSize) {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        return label;
    }

    private void addLabel(JLabel label, GridBagConstraints gbc, int column, int anchor) {
        gbc.gridx = column;
        gbc.anchor = anchor;
        add(label, gbc);
    }

    void update(GameState state, int highScore, long now) {
        Level level = state.getLevel();
        String levelText = "Level " + level.number();
        if (level.hasTimeLimit()) {
            long remaining = state.getRemainingTimeMs(now);
            levelText += "  •  Time " + String.format("%02d:%02d", remaining / 60000, (remaining % 60000) / 1000);
        } else {
            levelText += "  •  " + level.themeName();
        }
        levelLabel.setText(levelText);

        playerScoreLabel.setText("You: " + state.getScore() + " / " + Level.WIN_SCORE);

        if (level.hasAiSnake()) {
            aiScoreLabel.setText("AI: " + state.getAiScore());
            aiScoreLabel.setVisible(true);
        } else {
            aiScoreLabel.setText("");
            aiScoreLabel.setVisible(false);
        }

        highScoreLabel.setText("High: " + highScore);

        if (state.isMultiplierActive()) {
            multiplierLabel.setText("2x: " + state.getMultiplierSecondsLeft(now) + "s");
            multiplierLabel.setVisible(true);
        } else {
            multiplierLabel.setText("");
            multiplierLabel.setVisible(false);
        }
    }
}
