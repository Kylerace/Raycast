package raycast;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Atom {
    //this is atom, what i think (so far at least) should be the superclass of every movable object in the game
    //this is so that RayMap can easily iterate through a list of known atoms to go through behavior, and so that turfs can only contain atoms
    private Point center;
    private double size;
    //TODO: make the player an atom, right now its not needed since nothing else exists
    public Atom(Point center, double size) {
        this.center = center;
        this.size = size;
        RayMap.placeAtomAtCoords(center.x, center.y, this);
    }
    public Atom(int x, int y, double size) {
        this.center = center;
        this.size = size;
        raymap.placeAtomAtCoords(center.x, center.y, this);
    }
    public Atom(double x, double y, double size) {
        int convX = (int)x;
        int convY = (int)y;
        this.center = new Point(convX,convY);
        this.size = size;
        raymap.placeAtomAtCoords(center.x, center.y, this);
    }
    public void initializeAtom() {

    }
}
