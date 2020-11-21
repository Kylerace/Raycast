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
    private double x, y, x2, y2;
    private double deltaDistX, deltaDistY, sideDistX, sideDistY, rayDirX, rayDirY, planeX, planeY, dirX, dirY;
    private int stepX, stepY, mapX, mapY;
    private boolean hit = false;
    private int side;
    private double adjustedWallDist;
    private double rayAngle, playerAngle;
    //so many vars reeeeeeeee
    int squaresToCheck = 10000;//this is how many map squares each ray will go through until they give up (if they dont hit anything)
    public Ray (double x, double y, double angle, double pAngle, double cameraX) {
        //GIVE THIS RADIANS AND NOT DEGREES
        this.rayAngle = angle;
        this.playerAngle = pAngle;
        this.x = x;
        this.y = y;
        this.mapX = (int)x;//which map square we're in, X AND Y HAVE TO BE DIVIDED BY CELLSIZE WHEN YOU CALL THIS, DO NOT ROUND
        this.mapY = (int)y;
        this.dirX = Math.cos(angle);
        this.dirY = Math.sin(angle);

        this.planeX = dirY;//to make a vector perpendicular clockwise to another, switch the x and y components of the first and multiply the new y component by -1
        this.planeY = -1 * dirX;

        this.rayDirX = dirX + planeX * cameraX;//this represents the distance in the x direction on the unit circle
        this.rayDirY = dirY + planeY * cameraX;//this represents the distance in the y direction on the unit circle
        //this.deltaDistX = Math.sqrt((1.0 + Math.pow(rayDirY, 2) / Math.pow(rayDirX, 2)));
        //this.deltaDistY = Math.sqrt((1.0 + Math.pow(rayDirX, 2) / Math.pow(rayDirY, 2)));
        this.deltaDistX = Math.abs(1/rayDirX);
        this.deltaDistY = Math.abs(1/rayDirY);
        findCollision();
    }
    public double findCollision() {
        int iteration = 0;
        double distance = 0;
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (x - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - x) * deltaDistX;
        }
        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (y - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - y) * deltaDistY;
        }
        while (!hit && iteration < squaresToCheck) {
            //if the ray is looking for a turf in the X direction
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {//if the ray is looking for a turf in the Y direction
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }
            //System.out.println("turf "+Main.raymap.findTurfForPosition(mapX, mapY).turfType+" mapX "+mapX+" mapY "+mapY);
            if (Main.raymap.findTurfByIndex(mapX, mapY).turfType > 0) {
                //System.out.println("x "+mapX+" y "+mapY+" type "+Main.raymap.findTurfForPosition(mapX, mapY).turfType);
                hit = true;
            }
            iteration++;
        }
        if (side == 0) {
            adjustedWallDist = (mapX - x + (1 - stepX) / 2) / rayDirX;
        } else {
            adjustedWallDist = (mapY - y + (1 - stepY) / 2) / rayDirY;
        }
        System.out.println("x "+mapX+" y "+mapY+" type "+Main.raymap.findTurfForPosition(mapX, mapY).turfType+" distance "+adjustedWallDist);
        //System.out.println("dist "+adjustedWallDist+" mapX "+mapX+" mapY "+mapY+" x "+x+" y "+y);
        return adjustedWallDist;
    }
}
