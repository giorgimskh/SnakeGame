package snake.ui.render;

import java.awt.*;

import snake.model.FoodType;

import static snake.model.Board.UNIT;

/** Paints the apples and the bomb, each inside its grid cell. */
public final class ItemPainter {
    private ItemPainter() {
    }

    public static void paintFood(Graphics g, Point apple, FoodType type) {
        if (apple == null) {
            return;
        }
        int bodyInset = Math.max(2, UNIT / 10);
        int bodySize = UNIT - bodyInset * 2;
        int bodyX = apple.x + bodyInset;
        int bodyY = apple.y + bodyInset;

        if (type == FoodType.MULTIPLIER) {
            // Green apple with sparkles
            g.setColor(new Color(0, 255, 0));
            g.fillOval(bodyX, bodyY, bodySize, bodySize);

            g.setColor(new Color(0, 200, 0));
            g.drawOval(bodyX, bodyY, bodySize, bodySize);

            g.setColor(Color.YELLOW);
            int sparkleSize = 3;
            g.fillOval(bodyX + bodySize / 4, bodyY + bodySize / 4, sparkleSize, sparkleSize);
            g.fillOval(bodyX + bodySize * 3 / 4, bodyY + bodySize / 4, sparkleSize, sparkleSize);
            g.fillOval(bodyX + bodySize / 4, bodyY + bodySize * 3 / 4, sparkleSize, sparkleSize);
            g.fillOval(bodyX + bodySize * 3 / 4, bodyY + bodySize * 3 / 4, sparkleSize, sparkleSize);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("2x", bodyX + bodySize / 3, bodyY + bodySize / 2 + 4);
        } else {
            // Red apple with stem, leaf and highlight
            g.setColor(new Color(220, 20, 60)); // Crimson
            g.fillOval(bodyX, bodyY, bodySize, bodySize);

            g.setColor(new Color(139, 0, 0));
            g.drawOval(bodyX, bodyY, bodySize, bodySize);

            g.setColor(new Color(139, 69, 19)); // Saddle brown
            int stemWidth = Math.max(2, UNIT / 8);
            int stemHeight = Math.max(3, UNIT / 6);
            int stemX = bodyX + bodySize / 2 - stemWidth / 2;
            int stemY = bodyY - stemHeight / 2;
            g.fillRect(stemX, stemY, stemWidth, stemHeight);

            g.setColor(new Color(34, 139, 34)); // Forest green
            int leafSize = Math.max(3, UNIT / 6);
            int leafX = stemX + stemWidth;
            int leafY = stemY - leafSize / 3;
            g.fillOval(leafX, leafY, leafSize, leafSize);

            g.setColor(new Color(0, 100, 0));
            g.drawOval(leafX, leafY, leafSize, leafSize);

            g.setColor(new Color(255, 255, 255, 100));
            int highlightSize = Math.max(3, UNIT / 4);
            int highlightX = bodyX + bodySize / 5;
            int highlightY = bodyY + bodySize / 5;
            g.fillOval(highlightX, highlightY, highlightSize / 3, highlightSize / 3);
        }
    }

    public static void paintBomb(Graphics g, Point bomb) {
        if (bomb == null) {
            return;
        }
        int bodyInset = Math.max(2, UNIT / 10);
        int bodySize = UNIT - bodyInset * 2;
        int bodyX = bomb.x + bodyInset;
        int bodyY = bomb.y + bodyInset;

        g.setColor(Color.BLACK);
        g.fillOval(bodyX, bodyY, bodySize, bodySize);

        g.setColor(Color.DARK_GRAY);
        g.drawOval(bodyX, bodyY, bodySize, bodySize);

        // Fuse
        g.setColor(Color.RED);
        int fuseWidth = Math.max(2, UNIT / 8);
        int fuseHeight = Math.max(8, UNIT / 3);
        int fuseX = bodyX + bodySize / 2 - fuseWidth / 2;
        int fuseY = bodyY - fuseHeight;
        g.fillRect(fuseX, fuseY, fuseWidth, fuseHeight);

        // Fuse tip
        g.setColor(Color.ORANGE);
        int tipSize = Math.max(3, UNIT / 6);
        int tipX = fuseX - tipSize / 2;
        int tipY = fuseY - tipSize / 2;
        g.fillOval(tipX, tipY, tipSize, tipSize);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("BOOM!", bodyX + bodySize / 4, bodyY + bodySize / 2 + 4);
    }
}
