/** 
 *  Basement Simulator 2020
 * 
 *  December 10th, 2020
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Raycast along with documentation.
 */

package bs2020;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
    public static int mazeSize;
    //Keep this at a standard round 16:9 resolution (144p, 360p, 450p, 720p, 1080p, etc.) but make sure it is smaller than your monitor resolution. 
    //(480p does not work because the width is actually fractional and just rounded up in real life)
    public static int windowY = 720;
    public static int windowX = windowY * 16 / 9; //Sets the X of the window based on a 16:9 aspect ratio
    public static int cellSize;
    public static int buttonHeight = windowY / 20;
    public static int buttonWidth = windowX / 2;
    //These will be used for the movement, and render will be used to determine whether or not a freame needs to be rendered
    private static boolean left, right, backwards, forwards, turnLeft, turnRight, render;
    public static boolean startMenu = true;
    public static boolean inventory = false;
    public static boolean difficultySet = false;
    private static Scene scene = new Scene(); //Calls to the graphics function to draw the scene
    private static Timer keyTimer = new Timer(10, new Main()); //This is the clock of the game. It runs a tick every 10ms
    public static double moveSpeed;
    private static double baseSpeed;
    private static double rotateSpeed = 1.5;
    public static Point mousePos = new Point();

    public static JFrame f;

    enum Movement {
        FL, F, FR, //Front Left, Front, Front Right
        L,      R, //Left,              Right
        BL, B, BR  //Back Left,  Back,  Back Right
    }
    Movement currentMove;
    public static double[] playerVector = {0, 0}; // {x, y}
    public static void main(String[] args) {
        //Pretty standard graphics setup
        f = new JFrame();
        f.setSize(windowX, windowY + 36); //The +36 is for the window bar at the top
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(new KeyListener() { //This KeyListener is what allows movement inputs to be detected.
            //If a key is held down during the tick, then the corresponding movement boolean will be true.
            public void keyPressed(KeyEvent e) {
                if (!inventory) {
                    if      (e.getKeyCode() == KeyEvent.VK_LEFT)    { turnLeft  = true; }
                    else if (e.getKeyCode() == KeyEvent.VK_RIGHT)   { turnRight = true; }
                    else if (e.getKeyCode() == KeyEvent.VK_W)       { forwards  = true; }
                    else if (e.getKeyCode() == KeyEvent.VK_A)       { left      = true; }
                    else if (e.getKeyCode() == KeyEvent.VK_S)       { backwards = true; }
                    else if (e.getKeyCode() == KeyEvent.VK_D)       { right     = true; }
                    
                    if      (e.getKeyCode() == KeyEvent.VK_SHIFT)   { moveSpeed = baseSpeed * 1.5; }
                    else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { moveSpeed = baseSpeed / 1.5; }
                }
                if (e.getKeyCode() == KeyEvent.VK_I) { inventory ^= true; }
            }
            public void keyTyped(KeyEvent e) {

            }
            public void keyReleased(KeyEvent e) { //If a key is released during the tick, then the corresponding movement boolean will be false.
                if      (e.getKeyCode() == KeyEvent.VK_LEFT)     { turnLeft  = false; }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)    { turnRight = false; }
                else if (e.getKeyCode() == KeyEvent.VK_W)        { forwards  = false; }
                else if (e.getKeyCode() == KeyEvent.VK_A)        { left      = false; }
                else if (e.getKeyCode() == KeyEvent.VK_S)        { backwards = false; }
                else if (e.getKeyCode() == KeyEvent.VK_D)        { right     = false; }

                if      (e.getKeyCode() == KeyEvent.VK_SHIFT)   { moveSpeed = baseSpeed; }
                else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { moveSpeed = baseSpeed; }
            }
        });
        f.addMouseListener( new MouseListener() {
            public void mousePressed(MouseEvent e) {
                
            }
            public void mouseReleased(MouseEvent e) {
                
            }
            public void mouseEntered(MouseEvent e) {

            }
            public void mouseExited(MouseEvent e) {

            }
            public void mouseClicked(MouseEvent e) { //Checks if ther mouse has clicked a button on screen
                if (startMenu) {
                    if (e.getX() >= Main.windowX / 4 && e.getX() <= Main.windowX * 3 / 4) {    
                        if (difficultySet) {
                            if (e.getY() >= Main.windowY / 2 + 36 && e.getY() <= Main.windowY / 2 + buttonHeight + 36) { //Easy
                                mazeSize = 16;
                                startGame();
                            }
                            else if (e.getY() >= Main.windowY / 2 + buttonHeight * 1.5 + 36 && e.getY() <= Main.windowY / 2 + buttonHeight * 2.5 + 36) { //Normal
                                mazeSize = 20;
                                startGame();
                            }
                            else if (e.getY() >= Main.windowY / 2 + buttonHeight * 3 + 36 && e.getY() <= Main.windowY / 2 + buttonHeight * 4 + 36) { //Hard
                                mazeSize = 32;
                                startGame();
                            }
                            else if (e.getY() >= Main.windowY / 2 + buttonHeight * 4.5 + 36 && e.getY() <= Main.windowY / 2 + buttonHeight * 5.5 + 36) { //Extreme
                                mazeSize = 40;
                                startGame();
                            }
                        }
                        else if (startMenu && e.getY() >= Main.windowY * 2 / 3 + 36 && e.getY() <= Main.windowY * 2 / 3 + buttonHeight + 36) { //Start Button
                            difficultySet = true;
                            scene.renderFrame();
                        }
                    }
                }
            }
        });
        f.add(scene);
        f.setResizable(false); //Since things depend on the initial resolution, we restrict the window from being resized
        f.setVisible(true);        
    }
    
    /**
     * Sets the parameters necessary before the first frame of the game is rendered
     */
    public static void startGame() {
        cellSize = windowX / mazeSize;
        baseSpeed = (double)cellSize / 35;
        moveSpeed = baseSpeed;
        startMenu = false;
        difficultySet = false;
        render = true;
        scene.start();
        keyTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        //There are safeguards to prevent movement when opposing directions are being held, so that frames aren't unnecessarily rendered
        if (!(left && right) && !startMenu && !difficultySet) {
            if (left) {
                currentMove = Movement.L;
            }
            else if (right) {
                currentMove = Movement.R;
            }
        }
        if (!(forwards && backwards) && !startMenu && !difficultySet) {
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
            scene.move(currentMove);
            currentMove = null;
        }
        if (!(turnLeft && turnRight) && !startMenu && !difficultySet) {
            if (turnLeft) {
                scene.rotate(-rotateSpeed);
            }
            else if (turnRight) {
                scene.rotate(rotateSpeed);
            }
        }
        if (render) {
            scene.renderFrame();
        }
        mousePos = f.getMousePosition();
    }

    /**
     * Ends the game
     */
    public static void gameOver() {
        System.out.println("You've completed the maze!");
        keyTimer.stop();
        System.exit(0); //This is temporary, we just don't have a win screen yet
    }
}