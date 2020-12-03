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
import java.lang.Object;
import java.awt.Robot;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {
    public static int mazeSize = 32;
    public static int windowY = 720; //Keep this at a standard round 16:9 resolution (144p, 360p, 450p, 720p, 1080p, etc.) but make sure it is smaller than your monitor resolution. (480p does not work because the width is actually fractional and just rounded up in real life)
    public static int windowX = windowY * 16 / 9; //Sets the X of the window based on a 16:9 aspect ratio
    public static int cellSize = windowX / mazeSize;
    private static boolean left, right, backwards, forwards, turnLeft, turnRight, render, mouseTurned; //These will be used for the movement, and render will be used to determine whether or not a freame needs to be rendered
    private static Scene scene = new Scene(cellSize * 1.5, cellSize * 1.5); //Calls to the graphics function to draw the scene
    static Timer keyTimer = new Timer(10, new Main()); //This is the clock of the game. It runs a tick every 10ms
    private static double baseSpeed = (double)cellSize / 35;
    public static double moveSpeed = baseSpeed;
    public static double crouchSpeed = baseSpeed / 1.5;
    public static double runSpeed = baseSpeed * 1.5;
    public static int rotateSpeed = 2;
    public static int mouseX = -100000;
    public static int mouseTurnRate = 0;
    public static Robot bobTheRobot;//be nice to bob, for he is lord of cursor position
    public static JFrame f;
    public static boolean bobInControl = false;//if bob is currently moving the mouse position
    enum Movement {
        FL, F, FR,
        L,      R,
        BL, B, BR
    }
    Movement currentMove;
    public static double[] playerVector = {0, 0}; // {x, y}
    public static void main(String[] args) {
        //Pretty standard graphics setup
        f = new JFrame();
        
        //This might be irrelevant now, btw. Say something in the group chat about it when you test it and find where you need to have the right border end to see the whole scene
        /* For whatever reason the same settings dont work for all of us, so each of us will get their own setSize bar and they comment it out 
        for everyone else, when you merge a pr dont worry about it, just set it to what works for you and dont touch the commented out ones.
        The 36 is for the window bar at the top */
        // f.setSize(windowX + 16, windowY + 36); //what works for KYLER
        f.setSize(windowX, windowY + 36); // what works for NATHAN
        // f.setSize(windowX + 16, windowY + 36); // what works for MATT
        // f.setSize(windowX + 16, windowY + 36); // what works for DYLAN
        //DO NOT EDIT SOMEONE ELSE'S BAR
        //f.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)  { exitGame();}//this can later be a pausegame function
                    
                if      (e.getKeyCode() == KeyEvent.VK_SHIFT)   { moveSpeed = runSpeed; }
                else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { moveSpeed = crouchSpeed; }
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

                if      (e.getKeyCode() == KeyEvent.VK_SHIFT)   { moveSpeed = baseSpeed; }
                else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { moveSpeed = baseSpeed; }
            }
        });
        f.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent m) {
                int newMouseX = m.getX() + f.getLocation().x;
                if (mouseX < -10000) {
                    mouseX = newMouseX;
                } else if ((mouseX < newMouseX + 0 || mouseX > newMouseX - 0) && !bobInControl) {
                    if (mouseX == f.getLocation().x + windowX/2) {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    }
                    mouseTurned = true;
                    mouseTurnRate = newMouseX - mouseX;
                    mouseX = newMouseX;
                }
                if (bobInControl) {
                    mouseX = newMouseX;
                    mouseTurnRate = 0;
                    mouseTurned = false;
                }
            }
        });
        
        f.add(scene);
        try {
            bobTheRobot = new Robot();
        } catch (Exception e) {

        }
        
        f.setResizable(false);
        f.setVisible(true);
        keyTimer.start();
        //Mouse.grab(true);

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
        if (mouseTurned) {
            System.out.println(mouseTurnRate/2);
            if (mouseTurnRate/2 > 0 || mouseTurnRate/2 < -10) {
                System.out.print("AHHHHHHHH");
            }  
            scene.rotate((int)(mouseTurnRate/2));
            render = true;
            mouseTurned = false;
            bobTheRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            //bobTheRobot.mouseWheel(10);
            bobInControl = true;
            mouseX = f.getLocation().x + windowX/2;
            mouseTurnRate = 0;
            bobTheRobot.mouseMove(f.getLocation().x + windowX/2,f.getLocation().y + windowY/2);
            bobInControl = false;
            mouseX = f.getLocation().x + windowX/2;
            bobTheRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            mouseTurned = false;
        }
        //This makes sure that a frame is only rendered if the player has moved. It's just to reduce CPU load.
        //Since there is nothing moving other than the player, it makes no sense to render the same screen repeatedly.
        if (render) {
        scene.renderFrame();
        }
        render = false;
    }


    public static void exitGame() {
        System.out.println("Goodbye!");
        keyTimer.stop();
        System.exit(0);
    }

    public static void gameOver() {
        System.out.println("You've completed the maze!");
        keyTimer.stop();
        System.exit(0); //This is temporary, we just don't have a win screen or menu yet
    }
}