import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static BufferedImage image;
    private static Graphics2D graph;
    private static Random random = new Random();

    private static int randomSgn() {
        return random.nextInt(2) * 2 - 1;
    }

    private static void drawRandomKochCurve(Point2D startPoint, Point2D endPoint, int n,
                                            Color inColor, Color outColor) {
        int rnd = randomSgn();
        Point2D point1 = new Point2D.Double(
                (2 * startPoint.getX() + endPoint.getX()) / 3,
                (2 * startPoint.getY() + endPoint.getY()) / 3);
        Point2D point2 = new Point2D.Double(
                (startPoint.getX() + endPoint.getX()) / 2 -
                        rnd * (startPoint.getY() - endPoint.getY()) * Math.sqrt(3) / 6,
                ((startPoint.getY() + endPoint.getY()) / 2 +
                        rnd * (startPoint.getX() - endPoint.getX()) * Math.sqrt(3) / 6));
        Point2D point3 = new Point2D.Double(
                (startPoint.getX() + 2 * endPoint.getX()) / 3,
                (startPoint.getY() + 2 * endPoint.getY()) / 3);
        Path2D path = new Path2D.Double();
        path.moveTo(point1.getX(), point1.getY());
        path.lineTo(point2.getX(), point2.getY());
        path.lineTo(point3.getX(), point3.getY());
        path.lineTo(point1.getX(), point1.getY());
        path.closePath();
        if (rnd == 1) {
            graph.setColor(inColor);
        }
        else {
            graph.setColor(outColor);
        }
        graph.fill(path);
        if (n == 0) {
            return;
        }
        drawRandomKochCurve(startPoint, point1, n - 1, inColor, outColor);
        drawRandomKochCurve(point1, point2, n - 1, inColor, outColor);
        drawRandomKochCurve(point2, point3, n - 1, inColor, outColor);
        drawRandomKochCurve(point3, endPoint, n - 1, inColor, outColor);

    }

    private static void drawRandomKochSnowflake(Point2D startPoint, double diameter,
                                                int amountOfSnowflakes, int n,
                                                Color inColor, Color outColor) {
        Point2D[] points = new Point2D[amountOfSnowflakes];
        for (int i = 0; i < amountOfSnowflakes; ++i) {
            points[i] = new Point2D.Double(
                    startPoint.getX() + diameter * Math.cos(2 * Math.PI / amountOfSnowflakes * i),
                    startPoint.getY() - diameter * Math.sin(2 * Math.PI / amountOfSnowflakes * i));
        }
        Path2D path = new Path2D.Double();
        path.moveTo(points[0].getX(), points[0].getY());
        for (int i = 0; i < amountOfSnowflakes; ++i) {
            path.lineTo(points[(i + 1) % amountOfSnowflakes].getX(), points[(i + 1) % amountOfSnowflakes].getY());
        }
        path.closePath();
        graph.setColor(inColor);
        graph.fill(path);
        for (int i = 0; i < amountOfSnowflakes; ++i) {
            drawRandomKochCurve(points[(i + 1) % amountOfSnowflakes], points[i], n, inColor, outColor);
        }
    }

    private static Color getBetweenColor(
            Color startColor, Color endColor, double p) {
        return new Color(
                (int)(startColor.getRed() +
                        (endColor.getRed() - startColor.getRed()) * p),
                (int) (startColor.getGreen() +
                        (endColor.getGreen() - startColor.getGreen()) * p),
                (int) (startColor.getBlue() +
                        (endColor.getBlue() - startColor.getBlue()) * p));
    }

    private static void drawKochMegaSnowflake(Point2D startPoint, double diameter,
                                              int amountOfEdges, int amountOfSnowflakes, int n,
                                              Color inColor, Color endColor, Color outColor) {
        drawRandomKochSnowflake(startPoint, diameter, amountOfEdges, n, endColor, outColor);
        for (int i = 1; i < amountOfSnowflakes; ++i) {
            drawRandomKochSnowflake(startPoint, diameter * (amountOfSnowflakes - i) / amountOfSnowflakes, amountOfEdges, n,
                    getBetweenColor(endColor, inColor, (double) i / amountOfSnowflakes),
                    getBetweenColor(endColor, inColor, (double) (i - 1) / amountOfSnowflakes));
        }

    }

    public static void main(String[] args) {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        graph = image.createGraphics();
        Color color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
        graph.setColor(color);
        graph.fill(new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));

        drawKochMegaSnowflake(new Point2D.Double(WIDTH / 2, HEIGHT / 2),
                WIDTH / 3, 7, 10, 6, Color.RED, Color.WHITE, color);
        JFrame frame = new JFrame();
        frame.addNotify();
        frame.setResizable(false);
        frame.setSize(frame.getInsets().left +
                        frame.getInsets().right + WIDTH,
                frame.getInsets().top +
                        frame.getInsets().bottom + HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                if (image != null) {
                    G.drawImage(image, 0, 0, null);
                }
            }
        });
        frame.setVisible(true);
    }
}