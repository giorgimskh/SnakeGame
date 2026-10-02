package snake.ui.render;

import java.awt.*;

import snake.model.Level;

/** Paints each level's themed background and the vignette over it. */
public final class ThemePainter {
    private ThemePainter() {
    }

    public static void paintBackground(Graphics g, Level level, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        switch (level) {
            case LEVEL_1:
                drawDesertTheme(g2d, width, height);
                break;
            case LEVEL_2:
                drawGrassTheme(g2d, width, height);
                break;
            case LEVEL_3:
                drawOceanTheme(g2d, width, height);
                break;
            case LEVEL_4:
                drawForestTheme(g2d, width, height);
                break;
            case LEVEL_5:
                drawSpaceTheme(g2d, width, height);
                break;
        }

        g2d.dispose();
    }

    /** Darkens the edges of the board slightly. */
    public static void paintVignette(Graphics2D g2d, int width, int height) {
        int radius = Math.max(width, height);
        float[] dist = {0.6f, 1.0f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)};
        RadialGradientPaint paint = new RadialGradientPaint(new Point(width / 2, height / 2), radius, dist, colors);
        Paint old = g2d.getPaint();
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, width, height);
        g2d.setPaint(old);
    }

    private static void drawDesertTheme(Graphics2D g2d, int width, int height) {
        GradientPaint sandGradient = new GradientPaint(
            0, 0, new Color(238, 203, 173), // Light sand
            0, height, new Color(194, 178, 128) // Darker sand
        );
        g2d.setPaint(sandGradient);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(34, 139, 34, 100));
        for (int i = 0; i < 8; i++) {
            int x = (i * 75) % width;
            int y = (i * 60 + 30) % height;
            drawCactus(g2d, x, y);
        }
    }

    private static void drawGrassTheme(Graphics2D g2d, int width, int height) {
        GradientPaint grassGradient = new GradientPaint(
            0, 0, new Color(34, 139, 34), // Forest green
            0, height, new Color(0, 100, 0) // Dark green
        );
        g2d.setPaint(grassGradient);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(50, 205, 50, 120));
        for (int i = 0; i < 12; i++) {
            int x = (i * 50) % width;
            int y = (i * 40 + 20) % height;
            drawGrassBlades(g2d, x, y);
        }
    }

    private static void drawOceanTheme(Graphics2D g2d, int width, int height) {
        GradientPaint oceanGradient = new GradientPaint(
            0, 0, new Color(135, 206, 235), // Sky blue
            0, height, new Color(0, 105, 148) // Deep blue
        );
        g2d.setPaint(oceanGradient);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(255, 255, 255, 80));
        for (int i = 0; i < 10; i++) {
            int x = (i * 60) % width;
            int y = (i * 50 + 25) % height;
            drawWave(g2d, x, y);
        }
    }

    private static void drawForestTheme(Graphics2D g2d, int width, int height) {
        GradientPaint forestGradient = new GradientPaint(
            0, 0, new Color(0, 100, 0), // Dark green
            0, height, new Color(25, 25, 112) // Dark blue
        );
        g2d.setPaint(forestGradient);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(139, 69, 19, 100));
        for (int i = 0; i < 6; i++) {
            int x = (i * 100) % width;
            int y = (i * 80 + 40) % height;
            drawTree(g2d, x, y);
        }
    }

    private static void drawSpaceTheme(Graphics2D g2d, int width, int height) {
        GradientPaint spaceGradient = new GradientPaint(
            0, 0, new Color(25, 25, 112), // Dark blue
            0, height, new Color(0, 0, 0) // Black
        );
        g2d.setPaint(spaceGradient);
        g2d.fillRect(0, 0, width, height);

        // Stars
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (i * 37) % width;
            int y = (i * 73) % height;
            g2d.fillOval(x, y, 2, 2);
        }

        // Planets
        g2d.setColor(new Color(255, 165, 0, 150));
        g2d.fillOval(50, 100, 40, 40);
        g2d.setColor(new Color(128, 128, 128, 150));
        g2d.fillOval(500, 200, 30, 30);
    }

    private static void drawCactus(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(x, y, 8, 25);
        g2d.fillRect(x - 4, y + 8, 16, 8);
        g2d.fillRect(x - 2, y + 20, 12, 8);
    }

    private static void drawGrassBlades(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(50, 205, 50));
        for (int i = 0; i < 5; i++) {
            int bladeX = x + (i * 3);
            g2d.drawLine(bladeX, y + 15, bladeX - 2, y);
            g2d.drawLine(bladeX, y + 15, bladeX + 2, y);
        }
    }

    private static void drawWave(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(255, 255, 255, 80));
        for (int i = 0; i < 3; i++) {
            int waveX = x + (i * 20);
            g2d.drawArc(waveX, y, 20, 10, 0, 180);
        }
    }

    private static void drawTree(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x, y + 20, 12, 30); // Trunk
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillOval(x - 8, y, 28, 25); // Leaves
    }
}
