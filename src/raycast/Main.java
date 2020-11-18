/** 
 *  Title of project
 * 
 *  Date of completion
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Raycast along with documentation.
 */

package raycast;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
    public static int mazeSize = 25;
    public static int resolution = 400;
    public static int cellSize = resolution / mazeSize;
    private static boolean left, right, backwards, forwards, turnLeft, turnRight, render = false; //These will be used for the movement, and render will be used to determine whether or not a freame needs to be rendered
    private static Scene scene = new Scene(resolution / 2, resolution / 2); //Calls to the graphics function to draw the scene
    static Timer keyTimer = new Timer(10, new Main()); //This is the clock of the game. It runs a tick every 10ms
    public static void main(String[] args) {
        //Pretty standard graphics setup
        JFrame f = new JFrame();

        f.setSize(resolution, resolution + 40); /* The 40 is for the window bar */
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(new KeyListener() { //This KeyListener is what allows movement inputs to be detected.
            //If a key is held down during the tick, then the corresponding movement boolean will be true.
            public void keyPressed(KeyEvent e) {
                if      (e.getKeyCode() == KeyEvent.VK_LEFT)    { turnLeft  = true; }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)   { turnRight = true; }
                else if (e.getKeyCode() == KeyEvent.VK_W)       { forwards  = true; }
                else if (e.getKeyCode() == KeyEvent.VK_A)       { left      = true; }
                else if (e.getKeyCode() == KeyEvent.VK_S)       { backwards = true; }
                else if (e.getKeyCode() == KeyEvent.VK_D)       { right     = true; }
            }
            public void keyTyped(KeyEvent e) { //Currently this isn't used, but this will be helpful for actions

            }
            public void keyReleased(KeyEvent e) { //If a key is released during the tick, then the corresponding movement boolean will be false.
                if      (e.getKeyCode() == KeyEvent.VK_LEFT)     { turnLeft  = false; }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)    { turnRight = false; }
                else if (e.getKeyCode() == KeyEvent.VK_W)        { forwards  = false; }
                else if (e.getKeyCode() == KeyEvent.VK_A)        { left      = false; }
                else if (e.getKeyCode() == KeyEvent.VK_S)        { backwards = false; }
                else if (e.getKeyCode() == KeyEvent.VK_D)        { right     = false; }
            }
        });
        f.add(scene);
        f.setResizable(false);
        f.setVisible(true);
        keyTimer.start();

    }
    @Override
    public void actionPerformed(ActionEvent arg0) {
        //There are safeguards to prevent movement when opposing directions are being held, so that frames aren't unnecessarily rendered
        if (!(left && right)) {
            if (left) {
                scene.move("left");
                render = true;
            }
            else if (right) {
                scene.move("right");
                render = true;
            }
        }
        if (!(forwards && backwards)) {
            if (forwards) {
                scene.move("forwards");
                render = true;
            }
            else if (backwards) {
                scene.move("backwards");
                render = true;
            }
        }
        if (!(turnLeft && turnRight)) {
            if (turnLeft) {
                scene.rotate(-1);
                render = true;
            }
            else if (turnRight) {
                scene.rotate(1);
                render = true;
            }
        }
        //This makes sure that a frame is only rendered if the player has moved. It's just to reduce CPU load.
        //Since there is nothing moving other than the player, it makes no sense to render the same screen repeatedly.
        if (render) {
            scene.renderFrame();
        }
        render = false;
    }
}