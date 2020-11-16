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
    private double x1, y1, x2, y2;
    private double deltaDistX, deltaDistY, sideDistX, sideDistY, rayDirX, rayDirY;
    private int stepX, stepY, mapX, mapY;
    private boolean hit = false;
    private int side;
    private double perpWallDist;
    private RayMap worldMap;
    //so many vars reeeeeeeee
    int squaresToCheck = 10000;//this is how many map squares each ray will go through until they give up (if they dont hit anything)
    public Ray (double x, double y, double angle, double pAngle, RayMap inputMap) {
        this.x1 = x;
        this.y1 = y;
        this.mapX = (int)x;
        this.mapY = (int)y;
        //this.x2 = x1 + Math.cos(angle) * Math.sqrt(Math.pow(Main.windowX, 2) + Math.pow(Main.windowY, 2)) + 1;//the end of the line segment that the ray will check
        //this.y2 = y1 + Math.sin(angle) * Math.sqrt(Math.pow(Main.windowX, 2) + Math.pow(Main.windowY, 2)) + 1;//ill probably not use it like this
        this.rayDirX = Math.cos(angle);//this represents the distance in the x direction on the unit circle, if this doesnt work add x1?
        this.rayDirY = Math.sin(angle);//this represents the distance in the y direction on the unit circle, if this doesnt work add y1?
        this.deltaDistX = Math.sqrt((1.0 + Math.pow(rayDirY, 2) / Math.pow(rayDirX, 2)));
        this.deltaDistY = Math.sqrt((1.0 + Math.pow(rayDirX, 2) / Math.pow(rayDirY, 2)));
        this.worldMap = inputMap;
    }
    public double findCollision() {
        int iteration = 0;
        double distance = 0;
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (x1 - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - x1) * deltaDistX;
        }
        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (y1 - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - y1) * deltaDistY;
        }
        while (!hit && iteration < squaresToCheck) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }
            //if ()
            iteration++;
        }
        return distance;
    }
}
