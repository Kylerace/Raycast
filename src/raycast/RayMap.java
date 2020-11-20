package raycast;

import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RayMap {
    //each turf has a map of objects that are either fully or partially inside them and their locations
    //objects tell RayMap their location every turn, RayMap figures out which turfs theyre a part of, then tells all of those turfs
    //each turf's contents list is consulted every time a ray intersects them
    public Turf[][] turfList;
    private int totalX;
    private int totalY;
    private int resolution;
    private int turfSize;//should be the CELL size, so that resolution/cellsize = number of maze tiles
    private int turfTilesOnASide;
    private int[] turfXCenters;
    private int[] turfYCenters;
    private Atom[] movables;
    private Map<Atom, Turf[]> atomLocations = new TreeMap<>();
    private int[][] Maze;
    public RayMap() {
        //tell the RayMap constructor how big the 2d screen is, and how big you want each turf
        this.Maze = Scene.getLevel();
        this.turfTilesOnASide = Main.mazeSize;
        this.resolution = Main.resolution;
        this.turfSize = Main.cellSize;
        createTurfList();
    }
    public void createTurfList() {
        for (int i = 0; i < turfTilesOnASide; i++) {
            for (int j = 0; j < turfTilesOnASide; j++) {
                System.out.println("i "+i+" j "+j+" turfSize "+turfSize+" Maze[i][j] "+Maze[i][j]);
                turfList[i][j] = new Turf(i * turfSize, j * turfSize, turfSize, Maze[i][j]);
                
            }
        }
    }
    public static void placeAtomAtCoords(int x, int y, Atom toBePlaced) {

    }
    public void atomMoveConsult(Atom hasMoved, Point newPoint) {

    }
    public void findTurfForPoint(Point placingPoint) {

    }
    public void changeTurf(Turf oldTurf, Turf newTurf) {

    }
}
