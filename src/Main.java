import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Main {

    public static void main(String[] args) {

        JFrame obj = new JFrame();

        GamePanel gamePlay = new GamePanel();

        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();

        obj.setSize(screenSize.width,screenSize.height);

        obj.setTitle("Brick Breaker Game");

        obj.setExtendedState(JFrame.MAXIMIZED_BOTH);

        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        obj.setResizable(false);

        obj.add(gamePlay);

        obj.setVisible(true);
    }
}