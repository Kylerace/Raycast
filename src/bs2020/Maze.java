/**  
 *  Basement Simulator 2020
 * 
 *  December 10th, 2020
 * 
 *  This program was created under the collaboration of Nathan Grimsey, Eric Lumpkin, Dylan Gibbons-Churchward, and Matthew McGuinn
 *  for Martin Hock's CS143 class in the Fall quarter of 2020.
 * 
 *  This code may be found at https://github.com/CS143-Raycasting-Project/Raycast along with documentation.
 */

package bs2020;

import java.awt.*;
import java.awt.image.*;
import java.awt.Point;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

public class Maze {
	private int[][] board; // memory for the maze. 1 = wall, 0 = ground
	private int[][] baseBoard; // sets the board up with a nice boarder. might be an easier way to do this
	private TreeSet<String> moveTracker = new TreeSet<>(); // keeps track of the moves at the recursion moves it's way through the board. when this is empty the aren't any more possible moves
	private Point startPoint;
	private Point endPoint;
	private Turf[][] turfMap;
	private int turfSize;// should be the CELL size, so that resolution/this = number of maze tiles
	private Turf nullSpace;// if an atom goes out of bounds, it goes here. this is so that rays dont go on
	// for 10000 iterations each time

	/**
	 * Creates a random maze with input dimensions(assumes shape is square for now)
	 * 
	 * @param x - x dimension of the maze
	 * @param y - y dimension of the maze
	 */
	public Maze(int x, int y) {
		board = new int[x - 2][y - 2];
		baseBoard = new int[x][y];
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				board[i][j] = 1;
			}
		}
		board[1][1] = 0;
		moveTracker.add(intToString(1,1));
		solve(board, moveTracker, intToString(0, 0));  
		for(int i = 0; i < baseBoard.length; i++) {
			for(int j = 0; j < baseBoard.length; j ++) {
				if(i == 0 || i == baseBoard.length - 1 || j == 0 || j == baseBoard.length - 1) {
					baseBoard[i][j] = 1;
				}
			}
		}
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++) {
				baseBoard[i+1][j+1] = board[i][j];
			}
		}
		
		baseBoard[0][1] = 2; // sets the starting point to 1, 1 (upper left corner) which is where the maze grows from initially
		this.startPoint = new Point(1, 0);
		int endPoint = 0;
		for(int i = 0; i < baseBoard.length; i++) { // finds the cell from the right wall that's closest to the bottom right corner
			if (baseBoard[baseBoard.length - 2][i] == 0) {
				endPoint = i;
			}
		}
		baseBoard[endPoint][baseBoard.length - 1] = 3;
		this.endPoint = new Point(baseBoard.length - 1, endPoint);
		
		
		
		this.turfMap = new Turf[x][y];
		this.turfSize = Main.cellSize;
		createTurfMap(x, y);
	}

	private void solve(int[][] board, TreeSet<String> moveTracker, String currentMove) {
		if(moveTracker.isEmpty()) { // base case. there are no more coordinates with viable moves
			return;
		}
		
		int[] xY = stringToInt(currentMove); 
		int x = xY[0];
		int y = xY[1];
				
		Map<String, Point> validMoveCollector = new TreeMap<>(); // collects valid moves from current position, to later choose from randomly
		// possible left move
		if(x-1 >= 0 && checkIfValid(board, x-1, y)) { // check if this spot adjacent to current x,y is valid
			Point a = new Point(x-1, y);
			validMoveCollector.put("a", a);
		} 
		// possible bottom move
		if(y+1 < board.length && checkIfValid(board, x, y+1)) { // check if this spot adjacent to current x,y is valid
			Point b = new Point(x, y+1);
			validMoveCollector.put("b", b);

		} 
		// possible right move
		if(x+1 < board.length && checkIfValid(board, x+1, y)) { // check if this spot adjacent to current x,y is valid
			Point c = new Point(x+1, y);
			validMoveCollector.put("c", c);
		} 
		// possible upper move
		if(y-1 >= 0 && checkIfValid(board, x, y-1)) { // check if this spot adjacent to current x,y is valid
			Point d = new Point(x, y-1);
			validMoveCollector.put("d", d);
		} 
		if(validMoveCollector.size() == 0) {
			moveTracker.remove(currentMove);
			if(moveTracker.isEmpty()) {
				return;
			} else {
				solve(board, moveTracker, moveTracker.last());
				return;
			}
		}
		
		// This will iterate through the list of possible moves and choose one at random to move forward with
		Random rand = new Random();
		int theChosenOne = rand.nextInt(validMoveCollector.size());
		String theChosenKey = "";
		int loopCount = 0;
		for(String key : validMoveCollector.keySet()) {
			if (loopCount == theChosenOne) {
				theChosenKey = key;
			}
			loopCount++;
		}
		
		int newX = validMoveCollector.get(theChosenKey).x;
		int newY = validMoveCollector.get(theChosenKey).y;

		board[newX][newY] = 0; // sets the chosen coordinates at zero
		moveTracker.add(intToString(newX, newY)); // adds this to the list with viable moves
		solve(board, moveTracker, intToString(newX, newY)); // calls the method from the new chosen spot
	}
	
	private int[] stringToInt(String s) {
		int[] xAndY = new int[2];
		String[] split = s.split(",");
		xAndY[0] = Integer.parseInt(split[0]);
		xAndY[1] = Integer.parseInt(split[1]);
		return xAndY;
	}
	
	private String intToString(int x, int y) {
		String xAndY = "";
		xAndY += x;
		xAndY += ",";
		xAndY += y;
		return xAndY;
	}
	
	private boolean checkIfValid(int[][] board, int x, int y) {
		if(board[x][y] == 0) { // returns false as this spot is already alive
			return false;
		}
		Map<Integer, Integer> xTracker = new TreeMap<>(); // keeps track of x neighbors in a line
		Map<Integer, Integer> yTracker = new TreeMap<>(); // keeps track of y neighbors in a line
		
		int neighborCount = 0;
		// up and to the left
		if(x-1 >= 0 && y-1 >= 0 && board[x-1][y-1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x-1);
			yTracker.put(neighborCount, y-1);
		}
		// to the left
		if(x-1 >= 0 && board[x-1][y] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x-1);
			yTracker.put(neighborCount, y);
		}
		// down and to the left
		if(x-1 >=0 && y+1 < board.length && board[x-1][y+1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x-1);
			yTracker.put(neighborCount, y+1);
		}
		// down
		if(y+1 < board.length && board[x][y+1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x);
			yTracker.put(neighborCount, y+1);
		}
		// down and to the right
		if(x+1 < board.length && y+1 < board.length && board[x+1][y+1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x+1);
			yTracker.put(neighborCount, y+1);
		}
		// right
		if(x+1 < board.length && board[x+1][y] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x+1);
			yTracker.put(neighborCount, y);
		}
		// up and to the right
		if(x+1 < board.length && y-1 >= 0 && board[x+1][y-1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x+1);
			yTracker.put(neighborCount, y-1);
		}
		// up
		if(y-1 >= 0 && board[x][y-1] == 0) {
			neighborCount++;
			xTracker.put(neighborCount, x);
			yTracker.put(neighborCount, y-1);
		}
		if(neighborCount == 3 && xTracker.get(1) == xTracker.get(2) && xTracker.get(2) == xTracker.get(3)) { // if there are exactly 3 neighbors in a line (x-direction), this allows the maze to make a turn
			return true;
		}
		if(neighborCount == 3 && yTracker.get(1) == yTracker.get(2) && yTracker.get(2) == yTracker.get(3)) { // if there are exactly 3 neighbors in a line (y-direction) , this allows the maze to make a turn
			return true;
		}
		if(neighborCount > 2) {
			return false;
		} else {
			return true;
		}
		
	}
	
	
	public int[][] getMaze() {
		return baseBoard;
	}
	
	/**
	 * creates the live map of turfs from the generated int arrays
	 * 
	 * @param x
	 * @param y
	 */
	private void createTurfMap(int x, int y) {
		this.nullSpace = new Turf(-1000, -1000, 10, 1);
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				turfMap[i][j] = new Turf(i * turfSize, j * turfSize, turfSize, baseBoard[i][j]);
			}
		}
	}
	
	/**
	 * searches through TurfMap for the given turf. x and y cannot be the real
	 * graphical coordinates, divide them by cellSize
	 */
	public Turf findTurfByIndex(int x, int y) {
		if (turfMap.length > x && x >= 0 && turfMap[0].length > y && y >= 0) {
			return turfMap[x][y];
		} else {
			return nullSpace;
        }
    }

	/**
	 * This will take the array generated for the maze and convert it into a BufferedImage that can be used to
	 * render the minimap in the Scene class. The sides are padded with 8 cells so you just see walls on the
	 * minimap when you get to the edge (otherwise the TextureMap would loop through and you'd see the other
	 * side of the map)
	 * @return a BufferedImage of the map for use in rendering a minimap
	*/
	public BufferedImage getMiniMap() {
		BufferedImage miniMap = new BufferedImage(Main.windowX + Main.cellSize * 8, Main.windowX + Main.cellSize * 8, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = miniMap.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, miniMap.getWidth(), miniMap.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.fillRect(Main.cellSize * 4, Main.cellSize * 4, Main.windowX, Main.windowX);
		g2d.setColor(Color.RED); //Denotes the start of the map
		g2d.fillRect((int)(Main.cellSize * 4 + Main.cellSize * startPoint.getX()), (int)(Main.cellSize * 4 + Main.cellSize * startPoint.getY()), Main.cellSize, Main.cellSize);
		findTurfByIndex((int)startPoint.getY(), (int)startPoint.getX()).hasBeenSeen = true;
		g2d.setColor(Color.GREEN); //Denotes the end of the map
		g2d.fillRect((int)(Main.cellSize * 4 + Main.cellSize * endPoint.getX()), (int)(Main.cellSize * 4 + Main.cellSize * endPoint.getY()), Main.cellSize, Main.cellSize);
		findTurfByIndex((int)endPoint.getY(), (int)endPoint.getX()).hasBeenSeen = true;
		return miniMap;
	}
}
