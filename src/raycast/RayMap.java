package raycast;

import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RayMap {
    //each turf has a map of objects that are either fully or partially inside them and their locations
    //objects tell RayMap their location every turn, RayMap figures out which turfs theyre a part of, then tells all of those turfs
    //each turf's contents list is consulted every time a ray intersects them
    //there should only be one raymap ever
    public static Turf[][] turfList;
    private int totalX;
    private int totalY;
    private int resolution;
    private int turfSize;//should be the CELL size, so that resolution/this = number of maze tiles
    private int turfTilesOnASide;
    private int[] turfXCenters;
    private int[] turfYCenters;
    private Atom[] movables;
    private Map<Atom, Turf[]> atomLocations = new TreeMap<>();
    private int[][] initialLevel;
    private Turf nullSpace;//if an atom goes out of bounds, it goes here
    public Turf[][] highLightedTurfs;
    public RayMap() {
        this.nullSpace = new Turf(-1000,-1000,100,1);
        this.initialLevel = Scene.maze.getMaze();
        this.turfTilesOnASide = Main.mazeSize;
        this.resolution = Main.resolution;
        this.turfSize = Main.cellSize;
        turfList = new Turf[turfTilesOnASide][turfTilesOnASide];
        highLightedTurfs = new Turf[turfTilesOnASide][turfTilesOnASide]; 
        createTurfList();
    }
    public void createTurfList() {
        for (int i = 0; i < turfTilesOnASide; i++) {
            for (int j = 0; j < turfTilesOnASide; j++) {
                //System.out.println("i "+i+" j "+j+" turfSize "+turfSize+" Maze[i][j] "+initialLevel[i][j]);
                turfList[i][j] = new Turf(i * turfSize, j * turfSize, turfSize, initialLevel[i][j]);
            }
        }//*/
    }
    public static void placeAtomAtCoords(int x, int y, Atom toBePlaced) {

    }
    public void atomMoveConsult(Atom hasMoved, Point newPoint) {

    }
    public void findTurfForPoint(Point placingPoint) {

    }
    public Turf findTurfForPosition(double x, double y) {
        try {
            //turfList[(int)x/turfSize][(int)y/turfSize].toggleSpecial();
            return turfList[(int)x/turfSize][(int)y/turfSize];
        } catch (Exception E) {
            return nullSpace;
        }
    }
    public Turf findTurfByIndex(int x, int y) {
        try {
            turfList[x][y].toggleSpecial();
            return turfList[x][y];
        } catch (Exception E) {
            return nullSpace;
        }
    }
    public void putIntoSpecial(Turf ble) {

    }
    public void changeTurf(Turf oldTurf, Turf newTurf) {

    }
}
