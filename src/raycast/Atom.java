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
    private double angle;
    //TODO: make the player an atom, right now its not needed since nothing else exists
    public Atom(Point center, double size, double angle) {
        this.center = center;
        this.size = size;
        this.angle = angle;
        RayMap.placeAtomAtCoords(center.x, center.y, this);
    }
    public Atom(int x, int y, double size, double angle) {
        this.center = center;
        this.size = size;
        this.angle = angle;
        RayMap.placeAtomAtCoords(center.x, center.y, this);
    }
    public Atom(double x, double y, double size, double angle) {
        int convX = (int)x;
        int convY = (int)y;
        this.center = new Point(convX,convY);
        this.size = size;
        this.angle = angle;
        RayMap.placeAtomAtCoords(center.x, center.y, this);
    }
    public void initializeAtom() {

    }
    public class Mob extends Atom {
        int type;
        public Mob (int x, int y, int size, int type, double angle) {
            super(x,y,size, angle);
            this.type = type;
        }
        public class Player extends Mob {
            double angle;
            public Player (int x, int y, int size, double angle) {
                super(x,y,size,2, angle);
                this.angle = angle;
            }
        }
    }
}

