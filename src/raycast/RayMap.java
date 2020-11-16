package raycast;

public class RayMap {
    private int[][] fullMap;
    private int[][][] typeList;
    /*
    immobile types (so far only walls and floors which are type 1 and 0 respectively) are mapped point by point onto fullMap.
    movable types are in a list and in order to check if a point collides with them you simply check if its distance to their center is less than 
    a certain value.
    */
    public RayMap (int width, int height) {
        fullMap = new int[width][height];
        typeList = new int[width][height][1000];//objects are organized by x,y, and type
    }
    public void add(int type, int x, int y) {
        switch(type) {
            case 0:
            return;
            case 1:
            addRectWall(x,y);
        }
    }
    private void addRectWall(int x, int y) {
        if (typeList[x][y][1] == 0) {//if the specified type doesnt exist in the record
            typeList[x][y][1] = 1;
            for (int i = x; i < 16; i++) {
                for (int j = y; j < 16; j++) {
                    fullMap[i][j] = 1;
                }
            }
        } else {
            return;
        }
    }

    public void remove(int type, int x, int y) {

    }

    public void move(int type, int x, int y) {

    }

    public boolean checkObject(int type, int x, int y) {
        return false;
    }
}
