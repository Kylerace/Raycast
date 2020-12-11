/**  
 *  Basement Simulator 2020
 * 
 *  December 10th, 2020
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Basement-Simulator-2020 along with documentation.
 */

package bs2020;

public class Ray {
    private double x, y; //the true x & y coordinates of the player divided by Main.cellsize

    //these are all of the vector x&y components that Ray uses, with sideDistX & Y being the most important
    private double distanceRatioX, distanceRatioY, //the inverses of rayX and rayY, gets added to sideDistX & Y when they are incremented
    dirX, dirY, //the x and y components of the vector representing where the player is pointing on the 2d plane
    rayX, rayY, //dirX & Y but adjusted for which pixel column of the screen this ray represents
    sideDistX, sideDistY, //gets incremented each tick to represent the total distance the ray has traveled in their respective directions
    perpendicularVectX, perpendicularVectY; //the x & y components of the vector perpendicular to the dirX & dirY vector (rotated 90 degrees clockwise)
    

    private int nextStepX, nextStepY, currentTurfXIndex, playerTurfYIndex;
    //nextStepX and nextStepY are set up once, if the ray goes to the east on the 2d plane nextStepX is 1 and -1 otherwise
    //similar to nextStepY, which is +1 if the ray is going south on the 2d plane and -1 otherwise

    private boolean hit = false;
    private boolean isYSideOfWall;//this is true if the ray is moving up or down in this moment, and false otherwise
    private double adjustedWallDist;
    private double collisionCoord;//this is the coordinate of the collision on the axis parallel to the side of the wall that was hit
    private int turfType;
    //so many vars reeeeeeeee
    int squaresToCheck = 10000;///this is how many map squares each ray will go through until they give up (if they dont hit anything)

    /**
     * constructs the ray with the given parameters and sets up necessary vectors for findCollision() to work
     * @param x the true x coordinate of the player divided by Main.cellsize
     * @param y the true y coordinate of the player divided by Main.cellsize
     * @param angle the RADIAN measure of the player's angle
     * @param xColumn which pixel column of the screen this ray represents
     */
    public Ray (double x, double y, double angle, double xColumn) {
        //GIVE THIS RADIANS AND NOT DEGREES
        //x and y are the coor
        this.x = x;
        this.y = y;
        this.currentTurfXIndex = (int)x;//which map square we're in, X AND Y HAVE TO BE DIVIDED BY CELLSIZE WHEN YOU CALL THIS, DO NOT ROUND
        this.playerTurfYIndex = (int)y;

        this.dirX = Math.cos(angle);//dir has a length of 1, so dirX is just the x value of a point on the unit circle
        this.dirY = Math.sin(angle);//dirY is the y value of a point on the unit circle

        this.perpendicularVectX = dirY;//to make a vector perpendicular clockwise to another, switch the x and y components of the first and multiply the new y component by -1
        this.perpendicularVectY = -1 * dirX;

        this.rayX = dirX + perpendicularVectX * xColumn;//
        this.rayY = dirY + perpendicularVectY * xColumn;//this represents the distance in the y direction on the unit circle
        
        this.distanceRatioX = Math.abs(1/rayX);
        this.distanceRatioY = Math.abs(1/rayY);
        
    }
    
    /**
     * move along a single turf at a time and check if its a wall. if it is then return the adjusted distance. 
     * @return the adjusted wall distance from the screen column this ray is from to the wall this ray hits
     */
    public double findCollision() {
        int iteration = 0;
        if (rayX < 0) {
            nextStepX = -1;
            sideDistX = (x - currentTurfXIndex) * distanceRatioX;
        } else {
            nextStepX = 1;
            sideDistX = (currentTurfXIndex + 1.0 - x) * distanceRatioX;
        }
        if (rayY < 0) {
            nextStepY = -1;
            sideDistY = (y - playerTurfYIndex) * distanceRatioY;
        } else {
            nextStepY = 1;
            sideDistY = (playerTurfYIndex + 1.0 - y) * distanceRatioY;
        }
        while (!hit && iteration < squaresToCheck) {
            //if the ray is looking for a turf in the X direction
            if (sideDistX < sideDistY) {
                sideDistX += distanceRatioX;
                currentTurfXIndex += nextStepX;
                collisionCoord = sideDistY + y;
                isYSideOfWall = false;
            } else {//if the ray is looking for a turf in the Y direction
                sideDistY += distanceRatioY;
                collisionCoord = sideDistX + x;
                playerTurfYIndex += nextStepY;
                isYSideOfWall = true;
            }
            turfType = Scene.maze.findTurfByIndex(currentTurfXIndex, playerTurfYIndex).turfType;
            if (turfType > 0) {//if the turf that the ray is on is a wall, then stop the loop. 0's are floors, anything greater is a wall type
                hit = true;
                if (!Scene.maze.findTurfByIndex(currentTurfXIndex, playerTurfYIndex).hasBeenSeen) {
                    Scene.maze.findTurfByIndex(currentTurfXIndex, playerTurfYIndex).hasBeenSeen = true;
                    Scene.drawWall(currentTurfXIndex, playerTurfYIndex);
                }
            }
            iteration++;
        }
        //calculate the adjusted wall distance
        if (isYSideOfWall == false) {
            adjustedWallDist = (currentTurfXIndex - x + (1 - nextStepX) / 2) / rayX;
            collisionCoord = y + adjustedWallDist * rayY;
        } else {
            adjustedWallDist = (playerTurfYIndex - y + (1 - nextStepY) / 2) / rayY;
            collisionCoord = x + adjustedWallDist * rayX;
        }
        return adjustedWallDist;
    }

    /**
     * returns where on the wall the ray hit. used for texture rendering
     * @param textureSize the size variable of the texture of the wall that the ray hit
     * @return which column of the given texture that this ray hit
     */
    public int getWallX(int textureSize) {
        return (int)(collisionCoord % 1 * textureSize);
    }

    /**
     * @return the type of the wall that this ray hit, used for texture rendering
     */
    public int getTurfType() {
        return turfType;
    }
} 
