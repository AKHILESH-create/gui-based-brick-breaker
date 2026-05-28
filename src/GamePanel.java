import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    private int paddleY;

    private boolean play = false;
    private boolean gameStarted = false;

    private int score = 0;
    private int highScore = 0;
    private boolean levelComplete = false;
    private boolean gameOver = false;

    private boolean paused = false;
    private int level = 1;
    private int totalBricks;
    private int effectX = -100;
    private int effectY = -100;
    private int effectTimer = 0;

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

        loadHighScore();

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

        ballXDir = -3;
        ballYDir = -3;

        playerX = 310;

        paddleY = screenHeight - 120;

        playerX = screenWidth/2 - 70;
        ballPosX = screenWidth/2;
        ballPosY = screenHeight - 250;
    }

    public void paint(Graphics g){

        // Main background
        g.setColor(Color.black);
        g.fillRect(0,0,screenWidth,screenHeight);

        // Pink top information bar
        g.setColor(new Color(255,220,230));
        g.fillRect(10,10,screenWidth-30,55);

        g.setColor(Color.gray);

        // Bottom line only
        g.fillRect(20,screenHeight-70,screenWidth-50,3);
        // Draw bricks
        map.draw((Graphics2D)g);

        // Top information text
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD,24));

        g.drawString("Level : " + level,80,45);

        g.drawString("Score : " + score,270,45);

        g.drawString("High Score : " + highScore,440,45);


        // Paddle
        g.setColor(Color.green);
        g.fillRoundRect(playerX,paddleY,220,10,15,15);

        // Ball
        g.setColor(Color.red);
        g.fillOval(ballPosX,ballPosY,30,30);

        // Brick break effect
        if(effectTimer > 0){

            g.setColor(Color.orange);

            g.fillOval(effectX-15,effectY,8,8);
            g.fillOval(effectX+15,effectY,8,8);

            g.setColor(Color.yellow);

            g.fillOval(effectX,effectY-15,8,8);
            g.fillOval(effectX,effectY+15,8,8);

            g.setColor(Color.white);

            g.fillOval(effectX,effectY,10,10);
        }


        // START SCREEN
        if(!gameStarted){

            Graphics2D g2d = (Graphics2D)g;

            // Full black background
            g.setColor(Color.black);
            g.fillRect(1,1,692,592);

            // Neon popup background
            g.setColor(new Color(255,255,230)); // Softer neon light yellow
            g.fillRoundRect(60,70,560,420,30,30);

            // Border
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(new Color(255,170,0)); // Orange border
            g2d.drawRoundRect(60,70,560,420,30,30);

            // Title
            g.setColor(new Color(220,110,0));
            g.setFont(new Font("Arial",Font.BOLD,46));

            g.drawString("BRICK BREAKER",120,130);

            // Underline
            g.setColor(new Color(255,200,100)); // Light orange
            g.fillRect(180,155,240,2);

            // Start text
            g.setColor(new Color(0,120,0));
            g.setFont(new Font("Arial",Font.BOLD,26));

            g.drawString("Press ENTER to Start",185,210);

            // Pause text
            g.setColor(new Color(180,120,0));

            g.drawString("Press P to Pause",205,260);

            // Exit text
            g.setColor(new Color(180,0,0));

            g.drawString("Press ESC to Exit",205,310);

            // Controls title
            g.setColor(new Color(0,100,150));
            g.setFont(new Font("Arial",Font.BOLD,24));

            g.drawString("MOVE PADDLE",210,370);

            // Arrow instructions
            g.setColor(Color.black);
            g.setFont(new Font("Arial",Font.BOLD,20));

            g.drawString("← Move Left",150,420);
            g.drawString("→ Move Right",350,420);

            return;
        }

        // PAUSE POPUP
        if(paused){

            g.setColor(new Color(220,220,255));
            g.fillRoundRect(140,220,420,170,50,50);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.pink);

            g2d.drawRoundRect(140,220,420,170,50,50);

            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD,36));

            g.drawString("GAME PAUSED",205,290);

            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD,24));

            g.drawString("Press R to Resume",215,340);
        }


        // WIN
        if(levelComplete){

            // Level Complete popup background
            g.setColor(new Color(220,255,220));
            g.fillRoundRect(110,220,480,170,50,50);

            // Border
            Graphics2D g2d = (Graphics2D) g;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.green);
            g2d.drawRoundRect(110,220,480,170,50,50);


            // Text
            g.setColor(new Color(0,150,0));
            g.setFont(new Font("Arial", Font.BOLD,38));

            g.drawString("LEVEL COMPLETE!",165,290);

            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD,24));

            g.drawString("Press ENTER for Next Level",185,340);
        }

        // GAME OVER POPUP
        if(gameOver){

            g.setColor(new Color(255,220,220));
            g.fillRoundRect(110,220,420,170,50,50);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setStroke(new BasicStroke(4));
            g2d.setColor(Color.red);

            g2d.drawRoundRect(110,220,420,170,50,50);

            g.setColor(new Color(180,0,0));
            g.setFont(new Font("Arial", Font.BOLD,40));

            g.drawString("GAME OVER",205,300);

            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD,24));

            g.drawString("Press ENTER to Restart",185,350);
        }
        g.dispose();
    }

    public void actionPerformed(ActionEvent e){

        if(play && !paused){

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            // paddle collision
            if(new Rectangle(ballPosX,ballPosY,30,30)
                    .intersects(new Rectangle(playerX,paddleY,220,10))){

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

                            // Store effect position
                            effectX = brickX + map.brickWidth/2;
                            effectY = brickY + map.brickHeight/2;
                            effectTimer = 12;

                            totalBricks--;

                            score +=5;

                            playSound("brick.wav");

                            if(score > highScore){

                                highScore = score;

                                saveHighScore();
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

            if(ballPosX > screenWidth-40)
                ballXDir=-ballXDir;

            // Game Over Logic
            if(ballPosY > screenHeight-80){

                play = false;

                ballXDir = 0;
                ballYDir = 0;

                gameOver = true;
                playSound("gameover.wav");
            }

            // Level Complete Logic
            if(totalBricks <= 0){

                play = false;

                ballXDir = 0;
                ballYDir = 0;

                levelComplete = true;
                playSound("levelcomplete.wav");
            }
        }

        if(effectTimer > 0){

            effectTimer--;
        }

        repaint();
    }

    public void keyPressed(KeyEvent e){

        if(e.getKeyCode()==KeyEvent.VK_RIGHT){

            if(playerX >= screenWidth - 260)
                playerX = screenWidth - 180;
            else
                moveRight();
        }

        if(e.getKeyCode()==KeyEvent.VK_LEFT){

            if(playerX<10)
                playerX=10;
            else
                moveLeft();
        }

        // Pause game using P
        if(e.getKeyCode()==KeyEvent.VK_P){

            if(play){
                paused = true;
            }
        }

        // Resume game using R
        if(e.getKeyCode()==KeyEvent.VK_R){

            if(paused){
                paused = false;
            }
        }

        // Exit game using ESC
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){

            System.exit(0);
        }

        if(e.getKeyCode()==KeyEvent.VK_ENTER){

            // First start screen
            if(!gameStarted){

                gameStarted = true;
                play = true;

                repaint();
                return;
            }

            // Start game normally
            if(!play && !gameOver && !levelComplete){

                play = true;

                ballPosX = 120;
                ballPosY = 350;

                ballXDir = -3;
                ballYDir = -3;

                playerX = 310;

                repaint();
            }

            // Restart after Game Over
            else if(gameOver){

                gameOver = false;

                score = 0;     // Reset current score only

                play = true;

                ballPosX = 120;
                ballPosY = 350;

                ballXDir = -3;
                ballYDir = -3;



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
        playerX += 80;
    }

    public void moveLeft(){

        play = true;
        playerX -= 80;
    }

    //sounds
    public void playSound(String soundName){

        try{

            AudioInputStream audioInput =
                    AudioSystem.getAudioInputStream(
                            getClass().getResource("/sounds/" + soundName)
                    );

            Clip clip = AudioSystem.getClip();

            clip.open(audioInput);

            clip.start();

        }
        catch(Exception ex){

            ex.printStackTrace();
        }
    }

    public void saveHighScore(){

        try{

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter("highscore.txt"));

            writer.write(String.valueOf(highScore));

            writer.close();
        }
        catch(Exception e){

            System.out.println(e);
        }
    }

    public void loadHighScore(){

        try{

            File file = new File("highscore.txt");

            if(file.exists()){

                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file));

                highScore =
                        Integer.parseInt(reader.readLine());

                reader.close();
            }
        }
        catch(Exception e){

            System.out.println(e);
        }
    }


    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}