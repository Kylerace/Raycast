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
    private Set<Atom> contents;
    public int turfType;
    //each turf has a map of objects that are either fully or partially inside them and their locations
    //objects tell RayMap their location every turn, RayMap figures out which turfs theyre a part of, then tells all of those turfs
    //each turf's contents list is consulted every time a ray intersects them
    public Turf(int x, int y, int sideLength, int type) {
        this.turfXCoord = x;
        this.turfYCoord = y;
        this.sideLength = sideLength;
        this.turfXFar = x + sideLength;
        this.turfYFar = y + sideLength;
        this.contents = new HashSet<>();
        this.turfType = type;
        isSpecial = false;
    }
    public boolean toggleSpecial() {
        boolean oldIsSpecial = isSpecial;
        isSpecial = !isSpecial;
        return oldIsSpecial;
    }
    public void addContents(Atom keyObject) {
        if (turfType != 0) {
            return;//i forgot how to throw custom errors
        }
        contents.add(keyObject);
    }
    public void removeContents(Atom keyObject) {
        contents.remove(keyObject);
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