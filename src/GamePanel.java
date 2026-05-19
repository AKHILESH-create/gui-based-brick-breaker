import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;

    private int score = 0;
    private int highScore = 0;
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
        if(totalBricks <= 0){

            level++;
            startLevel();
        }

        // GAME OVER
        if(ballPosY > 570){

            play = false;
            ballXDir = 0;
            ballYDir = 0;

            g.setColor(Color.red);
            g.setFont(new Font("Calibri",Font.BOLD,30));

            g.drawString("Game Over",240,300);

            g.setFont(new Font("Calibri",Font.BOLD,20));
            g.drawString("Press Enter to Restart",220,350);
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

        // Restart
        if(e.getKeyCode()==KeyEvent.VK_ENTER){

            if(!play){

                play=true;

                ballPosX=120;
                ballPosY=350;

                ballXDir=-2;
                ballYDir=-2;

                playerX=310;

                score = 0;
                level = 1;

                startLevel();

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