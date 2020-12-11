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
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Scene extends JPanel {
    public static Maze maze;
    private static double[] playerCoords; // {x, y}
    private static double playerRotation = 135; //This is in degrees
    private static String texturePath = "assets" + File.separator + "textures" + File.separator;
    private static String itemPath = "assets" + File.separator + "items" + File.separator;
    private static Texture wallTexture = new Texture(texturePath + "RedCobblestoneWall.png", 1280);//Make sure your terminal is IN the project folder
    private static Texture startTexture = new Texture(texturePath + "RedCobblestoneDoor.png", 1280);
    private static Texture exitTexture = new Texture(texturePath + "RedCobblestoneExit.png", 1280);
    private static BufferedImage miniMap, player;
    private static Graphics miniMapGraphics;
    private static boolean newTile = true;
    private static BufferedImage wallRender = new BufferedImage(Main.windowX, Main.windowY, BufferedImage.TYPE_INT_ARGB); //This will be used to render the walls pixel by pixel
    private static Graphics2D s = wallRender.createGraphics();
    private static BufferedImage background, menuBackground; //This will grab the background asset to be used in rendering the floor and ceiling
    private static BufferedImage start, easy, normal, hard, extreme; //These are used for the buttons in the menu
    private static BufferedImage knifeInv, spoonInv; //These are used for the items
    //I found this solution after setting each pixel individually with wallRender.setRGB() was way too slow. It links the wallRenderPixel array directly to the wallRender's data, which is why it's faster
    private static int[] wallRenderPixels = ((DataBufferInt)wallRender.getRaster().getDataBuffer()).getData();
    private static double lightDropOff;

    private static int columns = 5;
    private static int rows = 3;
    private static int slotSize =  Main.windowX / (columns + rows);

    public static Color currentColor;
    public static Color color;

    public static int[][] inventory = {
        {1, 2, 0, 0, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0}
    };  

    public Scene() {
        wallRender.setAccelerationPriority(1f);
        //sets textures and resizes some them to fit the resolution
        //Loads the menu background
        menuBackground = resizedImage(texturePath + "MenuBackground.png", Main.windowX, Main.windowY);
        //Loads the background asset for the floor and ceiling
        background = resizedImage(texturePath + "Background.png", Main.windowX, Main.windowY);
        //Loads the texture used for the player icon on the minimap
        player = resizedImage(texturePath + "Player.png", Main.windowX / 64, Main.windowX / 64);
        //Loads the menu buttons
        start = resizedImage(texturePath + "Start.png", Main.buttonWidth, Main.buttonHeight);
        easy = resizedImage(texturePath + "Easy.png", Main.buttonWidth, Main.buttonHeight);
        normal = resizedImage(texturePath + "Normal.png", Main.buttonWidth, Main.buttonHeight);
        hard = resizedImage(texturePath + "Hard.png", Main.buttonWidth, Main.buttonHeight);
        extreme = resizedImage(texturePath + "Extreme.png", Main.buttonWidth, Main.buttonHeight);
        //Loads the inventory textures
        knifeInv = resizedImage(itemPath + "knife inv.png", slotSize, slotSize);
        spoonInv = resizedImage(itemPath + "spoon inv.png", slotSize, slotSize);
    }

    /**
     * Resizes an image given to it to the provided dimensions
     * @param input BufferedImage to be resized
     * @param width of desired image
     * @param height of desired image
     * @return A resized BufferedImage
     */
    public static BufferedImage resizedImage(String filePath, int width, int height) {
        BufferedImage original;
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        try {
            original = ImageIO.read(new File(filePath));
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(original, 0, 0, width, height, null);
            g2d.dispose();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("You do not have the proper asset files!");
            System.exit(1);
        }
        return resized;
    }

    /**
     * Sets up variables that aren't defined before the menu before the game actually starts
     */
    public void start() {
        playerCoords = new double[] {Main.cellSize * 1.5, Main.cellSize * 1.5};
        maze = new Maze(Main.mazeSize, Main.mazeSize);
        maze.findTurfByIndex(1, 1).turfType = 0;
        miniMap = maze.getMiniMap();
        miniMapGraphics = miniMap.getGraphics();
    }

    /**
     * Moves the player in a direction
     * @param direction The direction a player is moving
     */
    public void move(Main.Movement direction) { //I use some simple trig here to change how the movement is done depending on rotation.
        switch (direction) {
            /** player degrees of rotation reference
             * 
             *     -135  -90  -45  
             *    +-180   P    0
             *     +135  +90  +45
             */
        case L:
            Main.playerVector = rotateVector(180);
            break;
        case R:
            Main.playerVector = rotateVector(0);
            break;
        case F:
            Main.playerVector = rotateVector(-90);
            break;
        case B:
            Main.playerVector = rotateVector(90);
            break;
        case BL:
            Main.playerVector = rotateVector(135);
            break;
        case BR:
            Main.playerVector = rotateVector(45);
            break;
        case FL:
            Main.playerVector = rotateVector(-135);
            break;
        case FR:
            Main.playerVector = rotateVector(-45);
            break;
        }
        Main.playerVector = collisionChecked(playerCoords, Main.playerVector);
        playerCoords[0] += Main.playerVector[0];
        playerCoords[1] += Main.playerVector[1];
        if (newTile) {
            drawPlayerPath();
            newTile = false;
        }
    }

    /**
     * Handles the movement with the rotation of a player
     * @param rotation The current rotation of the player
     * @return Adjusted point that the player would move to in one frame
     */
    private static double[] rotateVector(double rotation) {
        double[] rotatedVector = {0, 0}; // {x, y}
        rotatedVector[0] = Main.moveSpeed * Math.cos(Math.toRadians(playerRotation + rotation));
        rotatedVector[1] = Main.moveSpeed * Math.sin(Math.toRadians(playerRotation + rotation));
        return rotatedVector;
    }

    /**
     * Handles player collision
     * @param coords of entity to check for collision
     * @param vector The entity's current movement vector 
     * @return new vector corrected for collision
     */
    public double[] collisionChecked(double[] coords, double[] vector) {

        int currentXCell = (int) coords[0] / Main.cellSize;
        int currentYCell = (int) coords[1] / Main.cellSize;
        int futureXCell;
        int futureYCell;
        boolean collision = false;
        //Used to determine if the player has visited a new tile, for use with drawPlayerPath()
        boolean newXVisited = true;
        boolean newYVisited = true;
        //Adds a small border around walls so that the player can't get quite as close
        if (vector[0] > 0) {
            futureXCell = (int) (coords[0] + vector[0] + 3) / Main.cellSize;
        }
        else {
            futureXCell = (int) (coords[0] + vector[0] - 3) / Main.cellSize;
        }
        if (vector[1] > 0) {
            futureYCell = (int) (coords[1] + vector[1] + 3) / Main.cellSize;
        }
        else {
            futureYCell = (int) (coords[1] + vector[1] - 3) / Main.cellSize;
        }

        if(maze.findTurfByIndex(currentYCell, futureXCell).getType() >= 1) { //Player hit a wall in the x direction
            vector[0] = 0;
            newXVisited = false;
            collision = true;
            if (maze.findTurfByIndex(currentYCell, futureXCell).getType() == 3) {
                Main.gameOver();
            }
        }
        if(maze.findTurfByIndex(futureYCell, currentXCell).getType() >= 1) { //Player hit a wall in the y direction
            vector[1] = 0;
            newYVisited = false;
            collision = true;
            if (maze.findTurfByIndex(futureYCell, currentXCell).getType() == 3) {
                Main.gameOver();
            }
        }
        if(maze.findTurfByIndex(futureYCell, futureXCell).getType() >= 1 && !collision) { //Player hit a corner
            vector[0] = 0;
            vector[1] = 0;
            newYVisited = false;
            if (maze.findTurfByIndex(futureYCell, currentXCell).getType() == 3) {
                Main.gameOver();
            }
        }
        //Used to determine if the player has visited a new tile, for use with drawPlayerPath()
        if (((currentXCell != futureXCell && newXVisited) || (currentYCell != futureYCell && newYVisited))) {
            newTile = true;
        }
        return vector;
    }

    /**
     * Rotates the player
     * @param angle The angle for the player to rotate
     */
    public void rotate(double angle) {
        playerRotation += angle;
    }

    /**
     * Renders a new frame
     */
    public void renderFrame() {
        repaint();
    }

    /**
     * Draws that path that the player has taken thus far on the minimap
     */
    public void drawPlayerPath() {
        miniMapGraphics.setColor(Color.BLUE);
        miniMapGraphics.fillRect(   (int)Math.floor(playerCoords[0] / Main.cellSize) * Main.cellSize + Main.cellSize * 4,
                                    (int)Math.floor(playerCoords[1] / Main.cellSize) * Main.cellSize + Main.cellSize * 4,
                                    Main.cellSize, Main.cellSize);
    }

    /**
     * Draws a wall on the minimap as soon as the player sees it
     * @param x coordinate (in cellSize units) of wall to be drawn
     * @param y coordinate (in cellSize units) of wall to be drawn
     */
    public static void drawWall(int x, int y) {
        miniMapGraphics.setColor(Color.WHITE);
        miniMapGraphics.fillRect(y * Main.cellSize + Main.cellSize * 4, x * Main.cellSize + Main.cellSize * 4, Main.cellSize, Main.cellSize);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        this.setBackground(Color.black);
        Graphics2D g2d = (Graphics2D) graphics;
        //Renders the difficulty setting screen
        if (Main.difficultySet) {
            g2d.drawImage(menuBackground, null, 0, 0);
            g2d.drawImage(easy,     null, Main.windowX / 4, Main.windowY / 2);
            g2d.drawImage(normal,   null, Main.windowX / 4, Main.windowY / 2 + (int)(Main.buttonHeight * 1.5));
            g2d.drawImage(hard,     null, Main.windowX / 4, Main.windowY / 2 + (int)(Main.buttonHeight * 3));
            g2d.drawImage(extreme,  null, Main.windowX / 4, Main.windowY / 2 + (int)(Main.buttonHeight * 4.5));
        }
        //Renders the start menu screen
        else if (Main.startMenu) {
            g2d.drawImage(menuBackground, null, 0, 0);
            g2d.drawImage(start, null, Main.windowX / 4, Main.windowY * 2 / 3);
        }
        //Renders the inventory screen
        else if (Main.inventory) {
            int sidePadding = (Main.windowX - columns * slotSize) / (columns + 1);
            int bottomPadding = (Main.windowY - rows * slotSize) / (rows + 1);
            color = g2d.getColor();
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    int originX = (column * (slotSize + sidePadding)) + sidePadding;
                    int originY = (row * (slotSize + bottomPadding)) + bottomPadding;
                    RoundRectangle2D currentSlot = new RoundRectangle2D.Double(originX, originY, slotSize, slotSize, slotSize / 20, slotSize / 20);

                    if (inventory[row][column] == 1) currentColor = Color.blue;
                    if (inventory[row][column] == 2) currentColor = Color.red;
                    if (Main.mousePos != null) {
                        if (currentSlot.contains(Main.mousePos)) currentColor = currentColor.darker();
                    }
                    g2d.setColor(currentColor);
                    g2d.fill(currentSlot);

                    if (column == columns - 1) currentColor = Color.yellow;
                    else currentColor = Color.white;
                    g2d.setColor(currentColor);
                    g2d.draw(currentSlot);
                    currentColor = color;
                    
                    if (inventory[row][column] == 1) g2d.drawImage(spoonInv, null, originX, originY);
                    if (inventory[row][column] == 2) g2d.drawImage(knifeInv, null, originX, originY);
                }
            }
        }
        //Renders the gameplay
        else {
            //Used for timing the length it takes to render a frame
            // double start = System.nanoTime();
            g2d.setColor(Color.WHITE);
            Ray pixel;
            double collision;
            int columnHeight;
            int textureX;
            int textureY;
            Texture currentTexture;
            int currentPixel;
            int r,g,b;
            s.setColor(new Color(0, 0, 0, 0));
            s.setComposite(AlphaComposite.Src); //This resets the wallRender image to a new, completely transparent image
            s.fillRect(0, 0, Main.windowX, Main.windowY);
            //This does the collision calculations and renders the scene in 3D
            for (int x = 0; x < Main.windowX; x++) {
                double cameraX = 2 * x / (double)Main.windowX - 1;
                pixel = new Ray(playerCoords[1] / (double)Main.cellSize, playerCoords[0] / (double)Main.cellSize, Math.toRadians(180-playerRotation), cameraX);
                collision = pixel.findCollision();
                if (pixel.getTurfType() == 1) {
                    currentTexture = wallTexture;
                }
                else if (pixel.getTurfType() == 2) {
                    currentTexture = startTexture;
                }
                else {
                    currentTexture = exitTexture;
                }
                lightDropOff = collision * 20 + (Math.pow(collision,2)/5); //how much the brightness drops off as a unit of distance
                //How tall the column of pixels will be at x. We use the inverse of the collision distance because as the distance increases,
                //the height of the column should decrease. This is then multiplied by the window height and scaled by 40
                columnHeight = (int)(1 / collision / 40 * Main.windowY * 30 * ((double)Main.windowX / 1280));
                textureX = pixel.getWallX(currentTexture.size);
                //This handles texture mapping by scaling the image down to the appropriate size for each pixel
                int startY = (columnHeight > Main.windowY) ? (columnHeight - Main.windowY) / 2 : 0;
                int endY = (columnHeight > Main.windowY) ? Main.windowY + (columnHeight - Main.windowY) / 2 : columnHeight;
                //The statement between the ? and the : is assigned if columnHeight > windowY, the statement to the right of the : is assigned if it isnt
                float darkenDropOff = (float)(Math.max(0,160-(lightDropOff)) / 150);
                
                if (collision > 0.8) {//At collision distance of 0.7 you can kinda just barely see the difference between lighting on and off
                    for(int y = startY; y < endY; y++) {
                        textureY = y * currentTexture.size / columnHeight;
                        currentPixel = currentTexture.pixels[Math.max(textureY + textureX * currentTexture.size,0)];
                        r = (int) (((currentPixel >> 16) & 0xFF) * darkenDropOff);
                        g = (int) (((currentPixel >> 8) & 0xFF) * darkenDropOff);
                        b = (int) ((currentPixel & 0xFF) * darkenDropOff);
                        //Bit operations are evil, hexadecimal can be evil, therefore this is somewhere between evil and evil^2.
                        //Translates the integer inside of the current texture pixel into its component a,r,b,g values so we can darken them with distance.
                        //They must be translated back to work
                        currentPixel = (255 << 24) | (r << 16) | (g << 8) | b;//*/
                        wallRenderPixels[x + (y + (Main.windowY - columnHeight) /2 ) * Main.windowX] = currentPixel;
                        
                    }
                } else {
                    for(int y = startY; y < endY; y++) { 
                        textureY = y * currentTexture.size / columnHeight;
                        wallRenderPixels[x + (y + (Main.windowY - columnHeight) /2 ) * Main.windowX] = currentTexture.pixels[Math.max(textureY + textureX * currentTexture.size,0)];
                    }
                }
                
            }
            g2d.drawImage(background, null, 0, 0);
            g2d.drawImage(wallRender, null, 0, 0);
            g2d.setColor(Color.ORANGE);
            
            /*  THIS STUFF LOOKS LIKE A MESS. In reality, it's a bunch of graphical stuff, so there are a lot of numbers that help determine the scale
            of each GUI element. You don't need to understand exactly how the coordinates are determined, because it has to do with what looked good
            on the screen, but it should be pretty easy to understand what each line does. Basically, just don't worry about the parameters so long
            as you understand what each line does; I think you'd probably go crazy trying to figure everything out. */
            
            //Draws the circle that makes the border of the minimap
            g2d.fillOval(Main.windowX / 64 - Main.windowX / 256, Main.windowX / 64 - Main.windowX / 256, Main.windowX / 5 + Main.windowX / 128, Main.windowX / 5 + Main.windowX / 128);
            //This is how I'm able to rotate and move the minimap inside a bounded circle. It allows you to map a BufferedImage to a rectangle, and then paint a shape with the part of the image that would be there.
            TexturePaint miniMapPaint = new TexturePaint(miniMap, new Rectangle((int)(playerCoords[0] - Main.windowX - Main.cellSize * 4) / 2 + Main.windowX / 5 / 2 + Main.windowX / 64, (int)(playerCoords[1] - Main.windowX - Main.cellSize * 4) / 2 + Main.windowX / 5 / 2 + Main.windowX / 64, -(Main.windowX + Main.cellSize * 8) / 2, -(Main.windowX + Main.cellSize * 8) / 2));
            //Sets the paint used by g2d to the TexturePaint of the miniMap image
            g2d.setPaint(miniMapPaint);
            //This controls the rotation of the miniMap image, and it is centered on the center point of the circle that bounds it
            g2d.rotate(Math.toRadians(180 - playerRotation), Main.windowX / 5 / 2 + Main.windowX / 64, Main.windowX / 5 / 2 + Main.windowX / 64);
            //This is what actually draws the miniMap image in a circle
            g2d.fillOval(Main.windowX / 64, Main.windowX / 64, Main.windowX / 5, Main.windowX / 5);
            g2d.setColor(Color.RED);
            //This reverses the rotation needed for the miniMap so that a static player icon can be drawn at the center of the miniMap
            g2d.rotate(-Math.toRadians(180 - playerRotation), Main.windowX / 5 / 2 + Main.windowX / 64, Main.windowX / 5 / 2 + Main.windowX / 64);
            g2d.drawImage(player, null, Main.windowX / 5 / 2 + Main.windowX / 64 - Main.cellSize / 8, Main.windowX / 5 / 2 + Main.windowX / 64 - Main.cellSize / 8);
            g2d.dispose();
            //Used for timing the length it takes to render a frame
            // double end = System.nanoTime();
            // System.out.println((double)(end - start)/1000000); //Uncomment this and the System.nanoTime() lines to see how long it takes to render a frame in milliseconds
        }
    }

}
