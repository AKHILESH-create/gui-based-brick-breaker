import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;

    private int score = 0;
    private int highScore = 0;
    private boolean levelComplete = false;
    private boolean gameOver = false;
    private int level = 1;
    private int totalBricks;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballPosX = 120;
    private int ballPosY = 350;

    private int ballXDir = -1;
    private int ballYDir = -2;

    private MapGenerator map;

    //constructor

    public GamePanel() {

        startLevel();

        addKeyListener(this);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(delay,this);
        timer.start();
    }

    private void startLevel() {

        int rows = 2 + level;
        int cols = 5 + level;

        map = new MapGenerator(rows, cols);

        totalBricks = rows * cols;

        ballPosX = 120;
        ballPosY = 350;

        ballXDir = -2;
        ballYDir = -2;

        playerX = 310;
    }

    public void paint(Graphics g){

        // Main background
        g.setColor(Color.black);
        g.fillRect(1,1,692,592);

        // Pink top information bar
        g.setColor(new Color(255,220,230));
        g.fillRect(10,10,670,55);

        g.setColor(Color.gray);

        // Bottom line only
        g.fillRect(20,580,650,3);
        // Draw bricks
        map.draw((Graphics2D)g);

        // Top information text
        g.setColor(Color.black);
        g.setFont(new Font("Calibri", Font.BOLD,24));

        g.drawString("Level : " + level,80,45);

        g.drawString("Score : " + score,270,45);

        g.drawString("High Score : " + highScore,440,45);


        // Paddle
        g.setColor(Color.green);
        g.fillRoundRect(playerX,550,140,10,15,15);

        // Ball
        g.setColor(Color.red);
        g.fillOval(ballPosX,ballPosY,30,30);


        // WIN
        if(levelComplete){

            // Level Complete popup background
            g.setColor(new Color(220,255,220));
            g.fillRoundRect(140,220,420,170,50,50);

            // Border
            Graphics2D g2d = (Graphics2D) g;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.green);

            g2d.drawRoundRect(140,220,420,170,50,50);

            // Text
            g.setColor(new Color(0,150,0));
            g.setFont(new Font("Arial", Font.BOLD,38));

            g.drawString("LEVEL COMPLETE!",155,290);

            g.setColor(Color.black);
            g.setFont(new Font("Calibri", Font.BOLD,24));

            g.drawString("Press ENTER for Next Level",175,340);
        }

        // GAME OVER POPUP
        if(gameOver){

            g.setColor(new Color(255,220,220));
            g.fillRoundRect(140,220,420,170,50,50);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.red);

            g2d.drawRoundRect(140,220,420,170,50,50);

            g.setColor(new Color(180,0,0));
            g.setFont(new Font("Arial", Font.BOLD,40));

            g.drawString("GAME OVER",190,290);

            g.setColor(Color.black);
            g.setFont(new Font("Calibri", Font.BOLD,24));

            g.drawString("Press ENTER to Restart",185,340);
        }
        g.dispose();
    }

    public void actionPerformed(ActionEvent e){

        if(play){

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            // paddle collision
            if(new Rectangle(ballPosX,ballPosY,30,30)
                    .intersects(new Rectangle(playerX,550,140,10))){

                ballYDir = -ballYDir;
            }

            A:
            for(int i=0;i<map.map.length;i++){

                for(int j=0;j<map.map[0].length;j++){

                    if(map.map[i][j]>0){

                        int brickX=j*map.brickWidth+80;
                        int brickY=i*map.brickHeight+50;

                        Rectangle rect=
                                new Rectangle(
                                        brickX,
                                        brickY,
                                        map.brickWidth,
                                        map.brickHeight);

                        Rectangle ballRect=
                                new Rectangle(
                                        ballPosX,
                                        ballPosY,
                                        30,
                                        30);

                        if(ballRect.intersects(rect)){

                            map.setBrickValue(0,i,j);

                            totalBricks--;

                            score +=5;
                            if(score > highScore){
                                highScore = score;
                            }

                            ballYDir=-ballYDir;

                            break A;
                        }
                    }
                }
            }

            if(ballPosX<0)
                ballXDir=-ballXDir;

            if(ballPosY<85)
                ballYDir = -ballYDir;

            if(ballPosX>640)
                ballXDir=-ballXDir;

            // Game Over Logic
            if(ballPosY > 570){

                play = false;

                ballXDir = 0;
                ballYDir = 0;

                gameOver = true;
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e){

        if(e.getKeyCode()==KeyEvent.VK_RIGHT){

            if(playerX >=530)
                playerX = 530;
            else
                moveRight();
        }

        if(e.getKeyCode()==KeyEvent.VK_LEFT){

            if(playerX<10)
                playerX=10;
            else
                moveLeft();
        }

        if(e.getKeyCode()==KeyEvent.VK_ENTER){

            // Start game for first time
            if(!play && !gameOver && !levelComplete){

                play = true;

                ballPosX = 120;
                ballPosY = 350;

                ballXDir = -2;
                ballYDir = -2;

                playerX = 310;

                repaint();
            }

            // Restart after Game Over
            else if(gameOver){

                gameOver = false;

                score = 0;
                level = 1;

                play = true;

                ballPosX = 120;
                ballPosY = 350;

                ballXDir = -2;
                ballYDir = -2;

                playerX = 310;

                startLevel();

                repaint();
            }

            // Next level
            else if(levelComplete){

                levelComplete = false;

                level++;

                startLevel();

                play = true;

                repaint();
            }
        }
    }

    public void moveRight(){

        play = true;
        playerX +=35;
    }

    public void moveLeft(){

        play = true;
        playerX -=35;
    }

    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}