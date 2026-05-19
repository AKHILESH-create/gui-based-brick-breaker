import java.awt.*;

public class MapGenerator {

    public int map[][];
    public int brickWidth;
    public int brickHeight;

    // Constructor
    public MapGenerator(int row, int col) {

        map = new int[row][col];

        // Create bricks
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {

                map[i][j] = 1;
            }
        }

        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    // Draw bricks
    public void draw(Graphics2D g) {

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {

                if (map[i][j] > 0) {

                    // Brick color
                    // Different colors for rows

                    if(i == 0)
                        g.setColor(Color.orange);

                    else if(i == 1)
                        g.setColor(Color.yellow);

                    else if(i == 2)
                        g.setColor(Color.orange);

                    else if(i == 3)
                        g.setColor(Color.green);

                    else if(i == 4)
                        g.setColor(Color.red);

                    else
                        g.setColor(Color.magenta);


                    g.fillRoundRect(
                            j * brickWidth + 80,
                            i * brickHeight + 120,
                            brickWidth,
                            brickHeight,
                            15,
                            15
                    );

                    // Brick border
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.white);

                    g.drawRoundRect(
                            j * brickWidth + 80,
                            i * brickHeight + 120,
                            brickWidth,
                            brickHeight,
                            15,
                            15
                    );
                }
            }
        }
    }

    // Remove brick after collision
    public void setBrickValue(int value, int row, int col) {

        map[row][col] = value;
    }
}
