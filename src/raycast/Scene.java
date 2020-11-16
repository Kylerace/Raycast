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

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Scene extends JPanel {
    //Points were causing rounding issues, so I just made the coords 2 separate doubles.
    private double playerX;
    private double playerY;
    private int playerRotation = 0; //This is in degrees so that I can just use an int.
    private Maze maze = new Maze(Main.mazeSize, Main.mazeSize);
    private int[][] mazeWalls = maze.getMaze();

    public Scene(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    public void move(String direction) { //I use some simple trig here to change how the movement is done depending on rotation.
        if (direction.equals("left")) {
            playerX -= Math.cos(Math.toRadians(playerRotation));
            playerY -= Math.sin(Math.toRadians(playerRotation));
        }
        else if (direction.equals("right")) {
            playerX += Math.cos(Math.toRadians(playerRotation));
            playerY += Math.sin(Math.toRadians(playerRotation));
        }
        else if (direction.equals("forwards")) {
            playerX += Math.sin(Math.toRadians(playerRotation));
            playerY -= Math.cos(Math.toRadians(playerRotation));
        }
        else if (direction.equals("backwards")) {
            playerX -= Math.sin(Math.toRadians(playerRotation));
            playerY += Math.cos(Math.toRadians(playerRotation));
        }
        
    }

    public void rotate(int angle) {
        playerRotation += angle;
    }

    public void renderFrame() {
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); /* We can turn this on later if necessary */
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < Main.mazeSize; i++) { //This displays the maze graphically
            for (int j = 0; j < Main.mazeSize; j++) {
                if (mazeWalls[i][j] == 1) {
                    g2d.fillRect(j * Main.cellSize, i * Main.cellSize, Main.cellSize, Main.cellSize);
                }
            }
        }
        g2d.setColor(Color.RED);
        g2d.rotate(Math.toRadians(playerRotation), (int)playerX + Main.cellSize / 2, (int)playerY + Main.cellSize / 2);
        g2d.fillRect((int)playerX, (int)playerY, Main.cellSize, Main.cellSize);
        g2d.drawLine((int)playerX + Main.cellSize / 2, (int)playerY + Main.cellSize / 2, (int)playerX + Main.cellSize / 2, (int)playerY - Main.cellSize / 2);
    }

}
