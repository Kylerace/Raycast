/**  
 *  Basement Simulator 2020
 * 
 *  December 10th, 2020
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Basement-Simulator-2020 along with documentation.
 */

package bs2020;

public class Turf {
    public boolean isEmpty = true;
    public int turfType;
    public boolean hasBeenSeen = false;
    
    //The turf class is responsible for storing the type of space a tile is (empty space, wall, start, end)
    //and whether it has been seen by the player yet.
    public Turf(int x, int y, int sideLength, int type) {
        this.turfType = type;
    }

    public int getType() {
        return turfType;
    }
}