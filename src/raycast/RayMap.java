package raycast;

import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RayMap {
    //each turf will have a map of objects that are either fully or partially inside them and their locations
    //objects tell RayMap their location every turn, RayMap figures out which turfs theyre a part of, then tells all of those turfs
    //RayMap is what tells rays what turf they are currently on, whether it is a wall (so to stop the ray) or a floor (which lets it continue)
    //each turf's contents list will be consulted every time a ray intersects them
    //there should only be one raymap ever
    //TODO: get rid of RayMap, and merge it with the Maze class.
    public static Turf[][] turfList;
    //this is the array that organizes every turf in the game (as of right now at least)

    private int turfSize;//should be the CELL size, so that resolution/this = number of maze tiles
    private int turfTilesOnASide;
    private int[][] initialLevel;
    private Turf nullSpace;//if an atom goes out of bounds, it goes here. this is so that rays dont go on for 10000 iterations each time
    public Turf[][] highLightedTurfs;
    public RayMap() {
        this.nullSpace = new Turf(-1000,-1000,10,1);
        this.initialLevel = Scene.maze.getMaze();
        this.turfTilesOnASide = Main.mazeSize;
        this.turfSize = Main.cellSize;
        turfList = new Turf[turfTilesOnASide][turfTilesOnASide];
        highLightedTurfs = new Turf[turfTilesOnASide][turfTilesOnASide]; 
        createTurfList();
    }
    //creates the array of turfs that represent the game
    public void createTurfList() {
        for (int i = 0; i < turfTilesOnASide; i++) {
            for (int j = 0; j < turfTilesOnASide; j++) {
                //System.out.println("i "+i+" j "+j+" turfSize "+turfSize+" Maze[i][j] "+initialLevel[i][j]);
                turfList[i][j] = new Turf(i * turfSize, j * turfSize, turfSize, initialLevel[i][j]);
            }
        }//*/
    }
    
    //the more important one, just accesses turfList and returns the turf at the specified location
    public Turf findTurfByIndex(int x, int y) {
        if (turfList.length > x && x >= 0 && turfList[0].length > y && y >= 0) {
            turfList[x][y].toggleSpecial();
            return turfList[x][y];
        } else {
            return nullSpace;
        }
    }

    //if you want to just give the actual coordinates
    public Turf findTurfForPosition(double x, double y) {
        try {
            turfList[(int)x/turfSize][(int)y/turfSize].toggleSpecial();
            return turfList[(int)x/turfSize][(int)y/turfSize];
        } catch (Exception E) {
            return nullSpace;
        }
    }
    
    public void findTurfForPoint(Point placingPoint) {

    }
    //change a turf into another turf type
    public void changeTurf(Turf oldTurf, Turf newTurf) {

    }
}
