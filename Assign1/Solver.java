package assg1;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


/** @description
 * Cell class is used to store individual cells within the grid
 * @variables
 * row - x value
 * col - y value
 * id - unique identifier
 * base - defined area cell
 */

class Cell { 
	int row;
	int col;
	int value;
	String id;
	Boolean isBase;
	
	public Cell(int _row, int _col, int _value, String _id, Boolean _isBase) {
		row = _row;
		col = _col;
		value = _value;
		id = _id;
		isBase = _isBase;
	}
	
	public Boolean isAvailable() {
		try {
			if (this.value == 0)
				return true;
			else
				return false;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public void replace(Cell c) {
		this.id = c.id;
		this.value = c.value;
	}
	
}

/** @description
 * Rect class represents a rectangular area on the grid
 * @variables
 * childNodes - DFS child nodes
 * valid - to mark as valid
 * width - width of rectangle
 * height - hiehgt of rectangle
 */

class Rect { 
	ArrayList<Rect> childNodes;
	Boolean valid;
	int width;
	int height;
	String id;
	Cell topLeft;
	
	public Rect(String _id, int _width, int _height, Cell _topLeft) {
		width = _width;
		height = _height;
		topLeft = _topLeft;
		valid = true;
		id = _id;
		childNodes = new ArrayList<Rect>();
	}
	
   	public void print(Grid grid) {
   		System.out.println("id " + id + " row " + this.topLeft.row + " col " + this.topLeft.col + " w " + this.width + " h " + this.height);
   	}
}

/** @description
 * Grid class 
 * @variables
 * cells - a 2d list of all cells within the grid
 * width - width of rectangle
 * height - hiehgt of rectangle
 * @functions
 * 
 */

class Grid {
	ArrayList<ArrayList<Cell>> cells;
	int width;
	int height;
	
	public Grid(int _width, int _height) {
		cells = new ArrayList<ArrayList<Cell>>();
		width = _width;
		height = _height;
	}
	
	public Boolean valid() {
		for (ArrayList<Cell> cellList : this.cells) {
			for (Cell c : cellList) {
				if (c.id.equals("0")) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void clear() {
		this.cells.clear();
		for (int row = 0; row < this.height; row++) {
			this.cells.add(new ArrayList<Cell>());
			for (int col = 0; col < this.width; col++) {
				this.cells.get(row).add(new Cell(row, col, 0,"", false));
			}
		}
	}
	
	public Cell get(int row, int col) {
		if (this.isInBounds(row, col)) {
			return cells.get(row).get(col);
		}
		else {
			System.out.println("Could not get cell: "+ row + ", " + col  + " Out of bounds");
			return null;
		}
	}
	
	public Boolean isInBounds(int row, int col) {
		if (row >= 0 && row < height && col >= 0 && col < width) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Boolean placeRect(Rect rect, Cell base) {
		// check cells are available
		ArrayList<Cell> cells = getRectCells(rect);
		int count = 0;
		
		for (Cell cell : cells) {
			if (cell.isAvailable()) { //count available cells
				count++;
			}
		}
		
		if (count == base.value-1) {
		
			// replace cells now that we know they are available
			for (int row = rect.topLeft.row; row < rect.height+rect.topLeft.row; row++) {
				for (int col = rect.topLeft.col; col < rect.width+rect.topLeft.col; col++) {
					if (this.isInBounds(row, col)) {
						this.get(row, col).id = base.id;
					}
					else {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int getCollisions(Rect rect, Cell base, ArrayList<Rect> allRects) {
		int collisions = 0;
		for (Rect idRect : allRects) {
			ArrayList<Cell> cells = getRectCells(idRect);
			for (Cell c : cells) {
				if (!c.id.equals(rect.id) || !c.id.equals("")) {
					collisions++;
				}
			}
		}
		return collisions;
	}
	
	public ArrayList<Cell> getRectCells(Rect rect) {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		
		for (int row = rect.topLeft.row; row < rect.height+rect.topLeft.row; row++) {
			for (int col = rect.topLeft.col; col < rect.width+rect.topLeft.col; col++) {
				if (this.isInBounds(row, col)) {
					cells.add(this.get(row, col));
				}
				else {
					return null;
				}
			}
		}
		return cells;
	}
	
	// Generate possible fitted rectangles using basic rectangles 
	public ArrayList<Rect> fitRects(Cell base, Rect rect) {
		ArrayList<Rect> rects = new ArrayList<Rect>();
		
		for (int row = 0; row < rect.height/2.0+1; row++) { // move rect
			for (int col = 0; col < rect.width/2.0+1; col++) {
				int r = base.row-row;
				int c = base.col-col;
				
				if (this.isInBounds(r, c)) { // check topleft is inbound
					Cell newTopLeft = this.get(r, c);
					Rect newRect = new Rect(base.id, rect.width, rect.height, newTopLeft);
					
					ArrayList<Cell> cells = this.getRectCells(newRect); // list of cells in new rect
					
					Boolean containsBase = false;
					if (cells != null) {
						
						int count = 0;
						for (Cell cell : cells) {
							if (cell.isAvailable()) { //count available cells
								count++;
							}
							if (cell.id == base.id) {
								containsBase = true;
							}
						}
						// only add newRect if area is available
						if (count == base.value-1 && containsBase) { // count not including base cell (value-1)
							
							// determine if rect is valid
							rects.add(newRect);
						}
					}
				}
					
			}
		}
		return rects;
	}
	
   	public void print() {
   		for(ArrayList<Cell> row : this.cells) {
   			for(Cell cell : row) {
				if (cell.value < 10) { // can be replaced with better code (test cases are <100 for each cell so this works for now)
					System.out.print(cell.id + "  ");
				}
				else {
					System.out.print(cell.id + " ");
				}
   			}
   		System.out.println();
   		}
   	}
}

	//iterate through every possible rectangle width and height
	//iterate through every possible rectangle position
    //if overlapping over previous rectangles in partially generated grid, mark invalid
    //if overlapping with other bases in the grid, mark invalid
    //otherwise, if valid, save rectangle to grid and recursively call the function on base+1

public class Solver {
	// Generate basic rectangles using factors of area
    static ArrayList<Rect> generateRects(Cell topLeft, String id, int n) { 
    	ArrayList<Rect> rects = new ArrayList<>();
    	
        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0)
            	rects.add(new Rect(id, i, n / i, topLeft)); // factor pairs of n
        }
        
        for (int i = 1; i * i <= n; i++) {
            if (n % i == 0)
            	if (i != n / i) // No point swapping the same value, this will create duplicate
            		rects.add(new Rect(id, n / i, i, topLeft)); // swapped values for all combinations
        }
        
        return rects;
    } 
    
    static Map<String, Integer> cellValues;
	
	public static void main(String[] args) {
		String infile = "codes/src/assg1/data/test_case_06.in";
		Solver s = new Solver();
		s.solve(infile);
	}
	
    //if base > totalBases return gridparts since it's the correct solution, otherwise:
	 
    //iterate through every possible rectangle width and height
        //iterate through every possible rectangle position
            //if overlapping over previous rectangles in partially generated grid, mark invalid
            //if overlapping with other bases in the grid, mark invalid
            //otherwise, if valid, save rectangle to grid and recursively call the function on base+1
	
  Grid findSolution(int base, ArrayList<Cell> bases, Grid grid) {
	  Cell b;
	  
	  if (base > bases.size()-1) {
		  return grid;
	  }
		  b = bases.get(base);
		  System.out.println(b.id);
	  
			ArrayList<Rect> rects = generateRects(b, b.id, b.value);
			ArrayList<Rect> fittedRects = new ArrayList<Rect>();
			for (Rect rect : rects) {
				// Generate possible fitted rectangles using basic rectangles
				fittedRects = grid.fitRects(b, rect);
				for (Rect fittedRect : fittedRects) {
					//fittedRect.print(grid);
					ArrayList<Cell> cells = grid.getRectCells(fittedRect); // list of cells in new rect
					
					Boolean containsBase = false;
					if (cells != null) {
						
						int count = 0;
						for (Cell cell : cells) {
							if (cell.isAvailable()) { //count available cells
								count++;
							}
							if (cell.id == b.id) {
								containsBase = true;
							}
						}
						// only add newRect if area is available
						if (count == b.value-1 && containsBase) { // count not including base cell (value-1)
							if (fittedRect.valid) {
								grid.placeRect(fittedRect, b);
								fittedRect.valid = true;
								findSolution(base+1, bases, grid);
							}
						}
						else {
							fittedRect.valid = false;
						}
					}
				}
			}
			
			System.out.println();
			grid.print();
			
			return findSolution(base+1, bases, grid);
  }
	

	/** The solve method accepts a String containing the 
	 * path to the input file of a space partition problem
	 * as described in the assignment specification and 
	 * returns a two-dimensional String array containing 
	 * the solution.
	 * 
	 * @param infile the input file containing the problem
	 * @return solution to the space partition problem
	 */
  
	public String[][] solve(String infile){
		/*
		 Here is a simple try-catch block for readData.
		 If you don't know what try/catch and exceptions are,
		 you don't have to worry about it for this unit, but
		 it would be good if you can learn a bit of it.
		*/
		
		// odd will always be along one row / column
		// even may occupy multiple rows / columns
		
		// use DFS branch of bruteforce trying different combinations
		// mark potential solution when it has been tried
		
		Grid grid = null;
		
		// read input and fill grid
		try {
			grid = readData(infile, grid);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
   		System.out.println();
   		
   		ArrayList<ArrayList<Rect>> allRects = new ArrayList<ArrayList<Rect>>();
		ArrayList<Rect> fittedRects = new ArrayList<Rect>();
   		
   		ArrayList<Cell> bases = new ArrayList<Cell>();
   		
   		for(ArrayList<Cell> row : grid.cells) {
   			for(Cell base : row) { 
   				if (base.isBase) {
   					bases.add(base);
   				}
   			}
   		}
   		
   		ArrayList<Cell> allBases = new ArrayList<Cell>();
		
   		for(ArrayList<Cell> row : grid.cells) {
   			for(Cell base : row) { 
   				if (base.isBase) {
   					allBases.add(base);
   					ArrayList<Rect> idRects = new ArrayList<Rect>();
   					// Generate basic rectangles using factors of area
   					ArrayList<Rect> rects = generateRects(base, base.id, base.value);
   					for (Rect rect : rects) {
   						// Generate possible fitted rectangles using basic rectangles
   						fittedRects = grid.fitRects(base, rect);
   						for (Rect fittedRect : fittedRects) {
   							idRects.add(fittedRect);
   							fittedRect.print(grid);
   						}
   					}
   					allRects.add(idRects);
   		   		   	System.out.println("next id list");
   				}
   			}
   		}
   		
   		System.out.println();
		
		// original
		grid.print();
		System.out.println();
		
		Random r = new Random();
		while(!grid.valid()) {
			try {
				grid = readData(infile, grid);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
			for (int baseId = 0; baseId < allRects.size(); baseId++) {
				int rectId = r.nextInt(allRects.get(baseId).size());
				//System.out.print(rectId + " ");
				grid.print();
				System.out.println();
				grid.placeRect(allRects.get(baseId).get(rectId), bases.get(baseId));
			}
			System.out.println("try again");
			System.out.println();
		}
		
		// solution
		System.out.println();
		
		String[][] strArr = new String[grid.height][grid.width];
		for (int x = 0; x < grid.height; x++) {
			for (int y = 0; y < grid.width; y++) {
				strArr[x][y] = (grid.get(x, y).id);
				System.out.print(grid.get(x, y).id + " ");
			}
			System.out.println();
		}
		
		return strArr;
	}
	
	
	/**
	 * The readData method accepts a String containing the 
	 * path to the input file containing the details of the
	 * problem (size of grid, number of sections, etc). 
	 * Please see the assignment specification for more information
	 * on the input format.
	 * 
	 * You should use this method to populate this class with 
	 * the information that you need to solve the problem.
	 * 
	 * I also recommend the use of Scanner class (I have written a
	 * little bit for you to start with), but you may choose to
	 * use something else. 
	 * 
	 * @param infile the input file containing the problem
	 * @throws Exception if file is not found or there is input reading error
	 */
   	private Grid readData(String infile, Grid grid) throws Exception {
   		Scanner in = new Scanner(new FileReader(infile));

   	 // _____                _   __          __   _    _      _   _ 
   	 //|  __ \              | |  \ \        / /  | |  | |    | \ | |
   	 //| |__) |___  __ _  __| |   \ \  /\  / /   | |__| |    |  \| |
   	 //|  _  // _ \/ _` |/ _` |    \ \/  \/ /    |  __  |    | . ` |
   	 //| | \ \  __/ (_| | (_| |     \  /\  /     | |  | |_   | |\  |
   	 //|_|  \_\___|\__,_|\__,_|      \/  \( )    |_|  |_( )  |_| \_|
   	 //                                   |/            |/          
   		
   		int width = in.nextInt();
   		int height = in.nextInt();
   		int sections = in.nextInt();
   		
   		grid = new Grid(width, height);
   		
   		//System.out.print(width + " " + height + " " + sections);
   		//System.out.println();
   		
   		in.nextLine();
   		
   		// map for id to cell count value
   		cellValues = new HashMap<String, Integer>();
   		
   	 // _____                _     _____     _ _    _     _                                    
   	 //|  __ \              | |   / ____|   | | |  (_)   | |    ___                            
   	 //| |__) |___  __ _  __| |  | |     ___| | |   _  __| |   ( _ )     _ __  _   _ _ __ ___  
   	 //|  _  // _ \/ _` |/ _` |  | |    / _ \ | |  | |/ _` |   / _ \/\  | '_ \| | | | '_ ` _ \ 
   	 //| | \ \  __/ (_| | (_| |  | |___|  __/ | |  | | (_| |  | (_>  <  | | | | |_| | | | | | |
   	 //|_|  \_\___|\__,_|\__,_|   \_____\___|_|_|  |_|\__,_|   \___/\/  |_| |_|\__,_|_| |_| |_|
   	 
   		for (int i = 0; i < sections; i++) {
   			String line = in.nextLine();
   			String[] parts = line.split("\\s+");
   			
   			String key = parts[0];
   			int cellNum = Integer.parseInt(parts[1]);
   			
   			cellValues.put(key, cellNum);
   			//System.out.println(key + " " + cellNum);
   		}
   		
   	 // _____                _     _____      _     _ 
   	 //|  __ \              | |   / ____|    (_)   | |
   	 //| |__) |___  __ _  __| |  | |  __ _ __ _  __| |
   	 //|  _  // _ \/ _` |/ _` |  | | |_ | '__| |/ _` |
   	 //| | \ \  __/ (_| | (_| |  | |__| | |  | | (_| |
   	 //|_|  \_\___|\__,_|\__,_|   \_____|_|  |_|\__,_|
   		
   		for (int row = 0; row < height; row++) {
   			String line = in.nextLine();
   			String[] cellsRow = line.split("\\s+"); // REGEX: split string with multiple spaces
   			
   			grid.cells.add(new ArrayList<Cell>()); // add new row
   			
   			for (int col = 0; col < width; col++) {
   				if (!cellsRow[col].isEmpty()) // check cell is not empty
   				{
   	   				if (cellsRow[col].equals("0")) {
   	   					grid.cells.get(row).add(new Cell(row,col, 0, cellsRow[col], false));
   	   				}
   	   				else {
   	   					int value = cellValues.get(cellsRow[col]);
   	   					grid.cells.get(row).add(new Cell(row,col, value, cellsRow[col], true));
   	   				}
   	   				//System.out.print(grid.cells.get(i).get(j).value + "  ");
   				}
   			}
   		}
   		
  		//grid.print();
   		
		return grid;
   	}
   	
}