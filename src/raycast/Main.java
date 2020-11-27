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
import java.util.Arrays;

import javax.swing.*;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
    public static int mazeSize = 32;
    public static int windowY = 720; //Keep this at a standard round 16:9 resolution (144p, 360p, 450p, 720p, 1080p, etc.) but make sure it is smaller than your monitor resolution. (480p does not work because the width is actually fractional and just rounded up in real life)
    public static int windowX = windowY * 16 / 9; //Sets the X of the window based on a 16:9 aspect ratio
    public static int cellSize = windowX / mazeSize;
    private static boolean left, right, backwards, forwards, turnLeft, turnRight, render = false; //These will be used for the movement, and render will be used to determine whether or not a freame needs to be rendered
    private static Scene scene = new Scene(windowX / 2, (windowY) / 2); //Calls to the graphics function to draw the scene
    static Timer keyTimer = new Timer(10, new Main()); //This is the clock of the game. It runs a tick every 10ms
    public static int moveSpeed = cellSize / 40;
    public static int rotateSpeed = 2;
    enum Movement {
        FL, F, FR,
        L,      R,
        BL, B, BR
    }
    Movement currentMove;
    public static double[] playerVector = {0, 0}; // {x, y}
    public static void main(String[] args) {
        //Pretty standard graphics setup
        JFrame f = new JFrame();

        //This might be irrelevant now, btw. Say something in the group chat about it when you test it and find where you need to have the right border end to see the whole scene
        /* For whatever reason the same settings dont work for all of us, so each of us will get their own setSize bar and they comment it out 
        for everyone else, when you merge a pr dont worry about it, just set it to what works for you and dont touch the commented out ones.
        The 36 is for the window bar at the top */
        // f.setSize(windowX + 16, windowY + 36); //what works for KYLER
        f.setSize(windowX, windowY + 36); // what works for NATHAN
        // f.setSize(windowX + 16, windowY + 36); // what works for MATT
        // f.setSize(windowX + 16, windowY + 36); // what works for DYLAN
        //DO NOT EDIT SOMEONE ELSE'S BAR

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
                currentMove = Movement.L;
            }
            else if (right) {
                currentMove = Movement.R;
            }
        }
        if (!(forwards && backwards)) {
            if (forwards) {
                if (currentMove == Movement.L) currentMove = Movement.FL;
                else if (currentMove == Movement.R) currentMove = Movement.FR;
                else currentMove = Movement.F;
            }
            else if (backwards) {
                if (currentMove == Movement.L) currentMove = Movement.BL;
                else if (currentMove == Movement.R) currentMove = Movement.BR;
                else currentMove = Movement.B;
            }
        }
        if (currentMove != null) {
            // System.out.println(Arrays.toString(playerVector) + "\t" + Scene.playerRotation);
            scene.move(currentMove);
            currentMove = null;
            render = true;
        }
        if (!(turnLeft && turnRight)) {
            if (turnLeft) {
                scene.rotate(-rotateSpeed);
                render = true;
            }
            else if (turnRight) {
                scene.rotate(rotateSpeed);
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