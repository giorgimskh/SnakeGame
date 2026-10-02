import javax.swing.SwingUtilities;

import snake.ui.GameWindow;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}
