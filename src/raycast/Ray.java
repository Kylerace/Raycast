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
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Ray {
    private double x, y;
    private double distanceRatioX, distanceRatioY, sideDistX, sideDistY, rayX, rayY, perpendicularVectX, perpendicularVectY, dirX, dirY;
    private int nextStepX, nextStepY, playerTurfXIndex, playerTurfYIndex;
    private boolean hit = false;
    private boolean isYSideOfWall;//this is true if the ray is moving up or down this tick, and false otherwise
    private double adjustedWallDist;
    //so many vars reeeeeeeee
    int squaresToCheck = 10000;//this is how many map squares each ray will go through until they give up (if they dont hit anything)
    public Ray (double x, double y, double angle, double xColumn) {
        //GIVE THIS RADIANS AND NOT DEGREES
        //x and y are the coor
        this.x = x;
        this.y = y;
        this.playerTurfXIndex = (int)x;//which map square we're in, X AND Y HAVE TO BE DIVIDED BY CELLSIZE WHEN YOU CALL THIS, DO NOT ROUND
        this.playerTurfYIndex = (int)y;

        this.dirX = Math.cos(angle);//dir has a length of 1, so dirX is just the x value of a point on the unit circle
        this.dirY = Math.sin(angle);//dirY is the y value of a point on the unit circle

        this.perpendicularVectX = dirY;//to make a vector perpendicular clockwise to another, switch the x and y components of the first and multiply the new y component by -1
        this.perpendicularVectY = -1 * dirX;

        this.rayX = dirX + perpendicularVectX * xColumn;//
        this.rayY = dirY + perpendicularVectY * xColumn;//this represents the distance in the y direction on the unit circle
        
        this.distanceRatioX = Math.abs(1/rayX);
        this.distanceRatioY = Math.abs(1/rayY);
        findCollision();
    }
    
    //move along a single turf at a time and check if its a wall. if it is then return the adjusted euclidian distance. you cant use
    //normal euclidian distance because it will cause the fisheye effect, so the distance returned by a ray is pretending that the ray is a straight 
    //line that is coming out of the vector that is perpendicular to dir (dir and perpendicularVect are vectors with x and y components,
    //so dirX and dirY are actually the two parts for the dir vector representing the players direction, perpendicularVect is a vector rotated 
    //90 degrees clockwise from dir)
    public double findCollision() {
        int iteration = 0;
        if (rayX < 0) {
            nextStepX = -1;
            sideDistX = (x - playerTurfXIndex) * distanceRatioX;
        } else {
            nextStepX = 1;
            sideDistX = (playerTurfXIndex + 1.0 - x) * distanceRatioX;
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
                playerTurfXIndex += nextStepX;
                isYSideOfWall = false;
            } else {//if the ray is looking for a turf in the Y direction
                sideDistY += distanceRatioY;
                playerTurfYIndex += nextStepY;
                isYSideOfWall = true;
            }
            if (Main.raymap.findTurfByIndex(playerTurfXIndex, playerTurfYIndex).turfType > 0) {//0's are floors, anything greater is a wall type
                //System.out.println("x "+playerTurfXIndex+" y "+playerTurfYIndex+" type "+Main.raymap.findTurfByIndex(playerTurfXIndex, playerTurfYIndex).turfType);
                hit = true;
            }
            iteration++;
        }
        if (isYSideOfWall == false) {
            adjustedWallDist = (playerTurfXIndex - x + (1 - nextStepX) / 2) / rayX;
        } else {
            adjustedWallDist = (playerTurfYIndex - y + (1 - nextStepY) / 2) / rayY;
        }
        return adjustedWallDist;
    }
}
