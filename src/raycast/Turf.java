package raycast;

import java.awt.Point;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Turf {
    private int turfXCoord;
    private int turfXAcross;
    private int turfXFar;

    private int turfYCoord;
    private int turfYAcross;
    private int turfYFar;
    
    private int sideLength;
    public boolean isSpecial;
    public boolean isEmpty = true;
    public int turfType;
    /*
    each turf has a map of objects that are either fully or partially inside them and their locations
    objects tell RayMap their location every turn, RayMap figures out which turfs theyre a part of, then tells all of those turfs
    each turf's contents list is consulted every time a ray intersects them
    currently turfs dont have contents (nor is there anything supposed to go into contents), i will consult the group about the Atom class
    that i am making, which is supposed to represent all non-turf objects in game (including mobs)
    as of right now, turfs are essentially an int representing their type that RayMap holds inside of an array for retrieval, this will change
    */
    public Turf(int x, int y, int sideLength, int type) {
        this.turfXCoord = x;
        this.turfYCoord = y;
        this.sideLength = sideLength;
        this.turfXFar = x + sideLength;
        this.turfYFar = y + sideLength;
        this.turfType = type;
        isSpecial = false;
    }
    public boolean toggleSpecial() {
        boolean oldIsSpecial = isSpecial;
        isSpecial = !isSpecial;
        return oldIsSpecial;
    }
    
    public int getType() {
        return turfType;
    }
    public int getSideLength() {
        return sideLength;
    }
    public Point getLocation() {
        return new Point(turfXCoord, turfYCoord);
    }
}