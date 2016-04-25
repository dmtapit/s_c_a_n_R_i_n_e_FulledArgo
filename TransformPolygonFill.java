/***************************************************************
* file: TransformPolygonFill.java
* author: D. Tapit
* class: CS 445 – Computer Graphics
*
* assignment: Program #2
* date last modified: 4/23/2016
*
* purpose: This program draws a window of 640x480 in the center of the screen.
* It reads in a file title coordinates.txt and draws the corresponding filled
* polygon in this window using the scanline polygon fill algorithm.  Each of the
* polygons are filled with color specified by the text file, and then each of
* them undergo the transformations specified by the text file before being
* drawn on the screen.
* You can quit the application by pressing the escape key (uses the
* input.Keyboard class).
* 
****************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

/**
 * The following class reads in a "coordinates.txt" file and draws the
 * polygon, fills the color, and transforms each according to the input
 * text file.  The file should be formatted as thus (as an example):
 * P = Polygon	T = Transformations (of P, Polygon)
 * 
 * P 0.5 0.6 0.3		// R, G, B colors
 * 30 300						// Vertices of the polygon to join
 * 80 150
 * 160 400
 * 200 150
 * 250 300
 * T								// Transformations
 * r 45 0 0					// r = rotation (rotation angle, pivot point)
 * s 0.5 1.5 0 0		// s = scaling (x , y (scaling factors) , pivot point)
 * t 200 -150				// t = translation (x-coordinate , y-coordinate)
 * r -90 0 0
 * P 0.2 0.4 0.7
 * ...
 * T
 * ...
 * @author DTapit
 */
public class TransformPolygonFill {
	
	/** Scanner scanInput to read the input file.
	 * Using a private Scanner will allow each method in this class to use
	 * this one Scanner as it reads through the "coordinates.txt" file.
	 */
	private static Scanner scanInput;
	
	// Putting the inputFile on a wider scope to be used within methods.
	private File inputFile;
	
	// Keeps a count of what line the scanInput Scanner is on.
	// AFAIK, a scanner cannot read backwards, so I create a temporary Scanner
	// in the transformations() method, and continuing reading lines until just
	// before the current scanInput line would be, which should be on the line
	// that reads a Polygon's value ("P").  Due to the nature of how I am reading
	// the file in through a while loop in the render() method, setting the 
	// scanInput to the position the temporary Scanner would be would be akin
	// to bringing it back one. (Java has no actual pointers so...this is a
	// roundabout way to do it, but it works...)
	private int scanInputLineCount;

	// For debugging purposes
	private int polygonCount;
	
	// Stack size can be changed here to carry more transformations
	private final int STACK_SIZE = 64;
	
	// INFINITE variable, to be used with slope in scanline polygon fill algo.
	private final float INFINITE = -999999;
	
	// Parity values, to be used in the scanline polygon fill algorithm
	private final int EVEN = 0;
	private final int ODD = 1;
	
	/**
	 * method: start
	 * purpose: Runs the methods needed to draw with OpenGl.
	 * If any exceptions occur, the stack will be printed out.
	 */
	public void start() {
		try{
			createWindow();
			initGL();
			render();
		}catch(Exception e){
			e.printStackTrace();
		}		
	} // end start method
		
	/**
	 * method: createWindow
	 * purpose: Creates the window that will display the objects that will be
	 * drawn with OpenGl.
	 * @throws Exception //LWJGL Exception
	 */
	private void createWindow() throws Exception {
		Display.setFullscreen(false);
		Display.setDisplayMode(new DisplayMode(640, 480));
		Display.setTitle("CS445 - PROGRAM 2");
		Display.create();
	} // end createWindow method
	
	/**
	 * method: initGl
	 * purpose: Initializes the objects to be drawn.
	 * glClearColor - background color, RGB values and Alpha from left to right
	 * glMatrixMode(GL_PROJECTION) - Loading camera using projection to view the
	 *	scene
	 * glLoadIdentity - loading the Identity matrix
	 * glOrtho(0,640,0,480,1,-1) - Setting up orthographic matrix;
	 *	size of 640x480 with a clipping distance between 1 and -1
	 * glMatrixMode - scene view
	 * glHint - rendering hints
	 */
	private void initGL() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// CENTERS THE ORIGIN TO THE CENTER OF THE WINDOW SCREEN!!!
		// Dividing the left, right, bottom, top, zooms into screen.
		// Multiplying will zoom out away from screen.
		// glOrtho(left, right, bottom, top, nearZ, farZ);
		glOrtho(-640/2, 640/2, -480/2, 480/2, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		//glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		//glDisable(GL_DEPTH_TEST);
	} // end initGl method
	
	/**
	 * method: render
	 * purpose: Creates the objects through a loop reading from a .txt file.
	 * @throws LWJGLException 
	 */
	private void render() throws LWJGLException {
		while(!Display.isCloseRequested()){
			try{
				glClear(GL_COLOR_BUFFER_BIT);
				glLoadIdentity();

				//////////////////SCAN FILE////////////////////////////////
				inputFile = new File("coordinates.txt");
				if (!(inputFile.exists())) {
					System.err.println("coordinates.txt file is missing!");
					System.exit(0);
				} scanInput = new Scanner(inputFile); // Start the scan of the inputFile
				
				String readLine; // Holds the value that the Scanner scanInput reads in
				scanInputLineCount = 0;
				polygonCount = 0;
				
				while (scanInput.hasNextLine()) {			
					// Placed within the while loop to continuously read the file
					readLine = scanInput.nextLine(); // Read line in the file
						scanInputLineCount++;
						System.out.println("scanInputLineCount: " + scanInputLineCount);
					System.out.println("RENDER: " + readLine);
					// tokens split at this delimiter " " (whitespaces)
					String[] tokens = readLine.split(" ");

					switch (tokens[0]) {
						case "P": // Polygon
							polygonCount++;
							glLoadIdentity(); // Resets drawing perspective for next polygon
							System.out.println("Polygon #: " + polygonCount);
							float red = Float.parseFloat(tokens[1]);
							float green = Float.parseFloat(tokens[2]);
							float blue = Float.parseFloat(tokens[3]);
							glColor3f(red,green,blue); // R, G, B
							polygon();
							break;
					/*
					 * I realized that after creating the polygon, the transformations
					 * would (should) come right after, thus only one case is needed....
						case "T": // Transformations
							transformations();
							break;
					*/
					} // end switch statement block
				} // end while loop that scans each line of inputFile
				Display.update();
				Display.sync(60);				
			}catch(Exception e) {
			} // end try-catch statement block
			System.out.println("No more Polygons to draw.");
			System.out.println("-EOF- restarting to beginning of file\n\n");
			Keyboard.create(); //throws LWJGLException
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				System.exit(0);
			}
		} // end while loop
		Keyboard.destroy();
		Display.destroy();
	} // end render method
	
	/**
	 * method: polygon
	 * purpose: Draws the polygon using the "Scanline Polygon Fill" algorithm
	 * by reading each line in the tokenized String array.  Each line of the 
	 * .txt file will contain a point to plot.  It should stop plotting points
	 * when the line reads a letter, like "T" or "P".  This method should be
	 * invoked when the "P" letter is read; the scanner is then passed through
	 * to this method to read in the coordinate points, checking before moving
	 * to the next line if it is a coordinate point (double).
	 *	Each point is stored in an ArrayList called orderedVertices, which will be
	 * placed in an all_edges table, used in the scanline polygon fill algorithm.
	 */
	private void polygon() throws FileNotFoundException {
		ArrayList<Point> orderedVertices = new ArrayList<Point>();
		
		String readLine;
		while(scanInput.hasNextDouble()) { //hasNextInt() ??
			readLine = scanInput.nextLine();
				scanInputLineCount++;
				System.out.println("rend-scanInputLineCount: " + scanInputLineCount);
			System.out.println("POLYGON POINT: " + readLine);
			String[] tokens = readLine.split(" ");
			float x = Float.parseFloat(tokens[0]);
			float y = Float.parseFloat(tokens[1]);
			Point p = new Point(x, y);
			orderedVertices.add(p);
			//glVertex2f(x,y);
		}
		// Need to apply transformations before plotting polygon and filling it up
		transformations(orderedVertices);
	/*	//FLAG CHECK to see each polygons' points
			for(int i = 0; i < orderedVertices.size(); i++)
			{	int count = i+1;
				System.out.println("Point " + count);
				System.out.println("X: " + orderedVertices.get(i).getX());
				System.out.println("Y: " + orderedVertices.get(i).getY());
			} System.out.println();	
	*/
		// Transformation applied, now plot the vertices of the polygon
		glBegin(GL_LINE_LOOP); //GL_POLYGON; automatic scanline polygon fill algo.
			for (Point orderedVertex : orderedVertices) {
				glVertex2f(orderedVertex.getX(), orderedVertex.getY());
			}
		glEnd();
		
		// Now that points are plotted and transformed, fill the polygon up
		// SCANLINE FILL ALGORITHM
		scanLinePolygonFillAlgorithm(orderedVertices);
	}
	
	/**
	 * method: transformations
	 * purpose: Applies the transformations to a polygon after the vertices
	 * of each polygon has been read from the file and stored into an ArrayList.
	 * @param orderedVertices 
	 */
	private void transformations(ArrayList<Point> orderedVertices) 
					throws FileNotFoundException {
		String readLine;
		// Assumes that, after the polygon method finishes running through all the
		// lines that contain vertices, that the next line will be "T", transform.
		readLine = scanInput.nextLine();
			scanInputLineCount++;
			System.out.println("trans-scanInputLineCount: " + scanInputLineCount);
		String[] tokens = readLine.split(" ");
		System.out.println("TRANSFORMATIONS: " + tokens[0]);
		if (!(tokens[0].equals("T"))) { 
			System.err.println("Expected a 'T' for transform after 'P' for polygon."
							+ "\nThe 'coordinates.txt' file is in improper syntax! \n"
							+ "An example file using Proper Syntax:\n"
							+ "P 1.0 0.0 0.0 \n80 10\n80 30\n230 37\n280 30\n280 10"
							+ "\nT\nt 100 -75\nr 30 0 0\ns 0.5 1.5 0 0\n.....\n"
							+ "Can follow up with more 'P' then 'T' lines as above...");
			System.exit(1);
		}
		
		Scanner justBeforeP = new Scanner(inputFile);
		String checksP; // Used as a FLAG CHECK for printing
		boolean transforming = true;
		Stack transStackSymbol = new Stack(STACK_SIZE);
		Stack transStackInfo = new Stack(STACK_SIZE*3);
		while(scanInput.hasNextLine() && transforming) {			
			readLine = scanInput.nextLine();
				scanInputLineCount++;
				System.out.println("trans2-scanInputLineCount: " + scanInputLineCount);
			String[] transTokens = readLine.split(" ");
				
			if (transTokens[0].equals("P")) {
				System.out.println("---");
				// Need to bring the scanInput Line back
				// Setting up a temporary Scanner to be set in the line previous
				for (int i = 0; i < scanInputLineCount-1; i++) {
					checksP = justBeforeP.nextLine(); //DON'T COMMENT OUT!!!!
					//System.out.println("ChecksP: " + checksP);
				}
				System.out.println("Moving scanner back one line for next polygon.");
				scanInput = justBeforeP; // Effectively sets the scanInput back one
				scanInputLineCount--;
				transforming = false;
			}
			else {			
				// Use stack operations to store transformations and their related info
				System.out.println("(Symbol) Pushing: " + transTokens[0]);
				transStackSymbol.push(transTokens[0]);
				for (int i = 1 ; i < transTokens.length ; i++) {
					System.out.println("(Info) Pushing: " + transTokens[i]);
					transStackInfo.push(transTokens[i]);
				}
			}			
		} // end while loop for PUSHING
		 
		Point pivot = new Point();
		while(!(transStackSymbol.empty())) {
			String popped = transStackSymbol.pop();
			System.out.print("Popping transformation symbol: " + popped + ": ");
			float angle, x, y;
				switch(popped) {
					case "t": System.out.println("TRANSLATE");
					System.out.println("Popping: " + transStackInfo.peek());
					y = Float.parseFloat(transStackInfo.pop());
					System.out.println("Popping: " + transStackInfo.peek());
					x = Float.parseFloat(transStackInfo.pop());
					glTranslatef(x, y, 0); 
						break;
					case "r": System.out.println("ROTATE");
					System.out.println("Popping: " + transStackInfo.peek());
					y = Float.parseFloat(transStackInfo.pop());
					System.out.println("Popping: " + transStackInfo.peek());
					x = Float.parseFloat(transStackInfo.pop());
					System.out.println("Popping: " + transStackInfo.peek());
					angle = Float.parseFloat(transStackInfo.pop());
					glRotatef(angle, x, y, 1f); 
						break;
					case "s": System.out.println("SCALE");
					System.out.println("Popping: " + transStackInfo.peek());
						pivot.setY(Float.parseFloat(transStackInfo.pop()));
					System.out.println("Popping: " + transStackInfo.peek());
						pivot.setX(Float.parseFloat(transStackInfo.pop()));
					System.out.println("Popping: " + transStackInfo.peek());
					y = Float.parseFloat(transStackInfo.pop());
					System.out.println("Popping: " + transStackInfo.peek());
					x = Float.parseFloat(transStackInfo.pop());								
					glScalef(x, y, 0); 
						break;
				}
		} // end while loop for POPPING
	}
	
	/**
	 * method: scanLinePolygonFillAlgorithm
	 * purpose: Colors the line looped polygons and fills them up with color.
	 * First, this method grabs all the stored vertices in orderedVertices read by
	 * the coordinate.txt file and calculates the Edges between them to be placed
	 * into an arraylist of Edges.  Using the Comparable defined in the Edge 
	 * class, Collections.sort is called on the ArrayList of Edges, and the Edges
	 * will then be sorted in Ascending order by y_Min, y_Max, x_Val, and slope,
	 * respectively, thus creating a "Global Edge Table".
	 * @param orderedVertices 
	 */
	private void scanLinePolygonFillAlgorithm(ArrayList<Point> orderedVertices) {
		System.out.println("Inside scanline polygon fill algorithm");
		// PRINT CHECKER for orderedVertices
		int j = 0;
		for(Point p : orderedVertices) {
			System.out.print("Point " + j + " X: " + p.getX());
			System.out.println(" Y: " + p.getY());
			j++;
		}
		// Grabbing the vertices, calculate their edges, place in allEdgesArrayList
		// then ordering them, creating a "Global Edge Table".
    ArrayList<Edge> allEdgesArrayList = new ArrayList<Edge>();
		Edge edgeToAdd;
		float y_Min;
		float y_Max;
		float x_Val;
		float slope; // (x2 - x1) / (y2 - y1)  ////  1/m
		
		// Comparing edges of each vertex in the polygon (in ArrayList of Points)
		for(int i = 0; i < orderedVertices.size(); i++) {
			// Comparing last edge with the first edge
			if (i == (orderedVertices.size()-1)) {
				if (orderedVertices.get(i).getY() < orderedVertices.get(0).getY()) {
					y_Min = orderedVertices.get(i).getY();
					y_Max = orderedVertices.get(0).getY();
					x_Val = orderedVertices.get(i).getX(); // x_Val of y_Min's vertex
				} 
				else {
					y_Min = orderedVertices.get(0).getY();
					y_Max = orderedVertices.get(i).getY();
					x_Val = orderedVertices.get(0).getX();
				}
				//SLOPE Calculation; if a '0' slope (1/0) then set to an "inf" value...
				if ((orderedVertices.get(0).getY() - orderedVertices.get(i).getY()) == 0) {
					slope = INFINITE; //Considered to be a 0 slope, or 1/0 (vertical)
				} else {
					slope = ( (orderedVertices.get(0).getX() - orderedVertices.get(i).getX())
										/ (orderedVertices.get(0).getY() - orderedVertices.get(i).getY())
					);
				}
			}			
			else { // All other edges compared to other adjacent edge
				if(orderedVertices.get(i).getY() < orderedVertices.get(i+1).getY()) {
					y_Min = orderedVertices.get(i).getY();
					y_Max = orderedVertices.get(i+1).getY();
					x_Val = orderedVertices.get(i).getX(); // x_Val of y_Min's vertex
				} 
				else {
					y_Min = orderedVertices.get(i+1).getY();
					y_Max = orderedVertices.get(i).getY();
					x_Val = orderedVertices.get(i+1).getX();
				}
				//SLOPE Calculation; make sure to compare the correct vertices
				if ((orderedVertices.get(i+1).getY() - orderedVertices.get(i).getY()) == 0) {
					slope = INFINITE; //Considered to be a 0 slope, or 1/0 (vertical)
				} else {
					slope = ( (orderedVertices.get(i+1).getX() - orderedVertices.get(i).getX())
										/ (orderedVertices.get(i+1).getY() - orderedVertices.get(i).getY())
					);
				}
			}
			edgeToAdd = new Edge(y_Min, y_Max, x_Val, slope);
			allEdgesArrayList.add(edgeToAdd);
		} // end for loop

		System.out.println("Printing all edges array list...");
		for (int i = 0 ; i < allEdgesArrayList.size(); i++) {
			System.out.print("Edge "+i+": " + allEdgesArrayList.get(i).getY_Min());
			System.out.print(" " + allEdgesArrayList.get(i).getY_Max());
			System.out.print(" " + allEdgesArrayList.get(i).getX_Val());
			System.out.println(" " + allEdgesArrayList.get(i).getSlope());
		}
		
		///////////////////////////////////////////////////////////////////////////
		// Creating the Global Edge Table
		///////////////////////////////////////////////////////////////////////////
		
		// Edges are placed in order of increasing y_Min, then y_Max, then x_Val
		Collections.sort(allEdgesArrayList); // See Comparable in Edge class...
		
		System.out.println("Printing SORTED edges array list...");
		for (int i = 0 ; i < allEdgesArrayList.size(); i++) {
			System.out.print("Edge "+i+": " + allEdgesArrayList.get(i).getY_Min());
			System.out.print(" " + allEdgesArrayList.get(i).getY_Max());
			System.out.print(" " + allEdgesArrayList.get(i).getX_Val());
			System.out.println(" " + allEdgesArrayList.get(i).getSlope());
		}
		// REMOVES THE INFINITE SLOPES
		for (int i = 0 ; i < allEdgesArrayList.size(); i++) {
			if (allEdgesArrayList.get(i).getSlope() == INFINITE) {
				allEdgesArrayList.remove(i);
			}
		}
		System.out.println("Removed Infinite slopes: Printing global array list...");
		for (int i = 0 ; i < allEdgesArrayList.size(); i++) {
			System.out.print("Global Edge "+i+": " + allEdgesArrayList.get(i).getY_Min());
			System.out.print(" " + allEdgesArrayList.get(i).getY_Max());
			System.out.print(" " + allEdgesArrayList.get(i).getX_Val());
			System.out.println(" " + allEdgesArrayList.get(i).getSlope());
		}
		
		// FILLING THE POLYGON
		System.out.println("Filling the polygon up.");
		fill(allEdgesArrayList);
	}
	
	private void fill(ArrayList<Edge> globalEdgeTable) {
		ArrayList<Edge> activeEdgeArrayList = new ArrayList<Edge>();
		
		// get Lowest y value to be the initial scan line (float to int...lossy?)
		int scanLine = (int) globalEdgeTable.get(0).getY_Min();
		// Assumes to be sorted properly by Y-min, Y-Max, X-Val, and then slope
		int y_MaxLineOfY_Min = (int) globalEdgeTable.get(0).getY_Max();
		boolean afterInitialY_Max = false; // Messy patchwork code; don't bother reading!
						
		// Add all edges with the same value as the initial scan line.
		for (int i = 0 ; i < globalEdgeTable.size();) {
			if (globalEdgeTable.get(i).getY_Min() == scanLine) {
				activeEdgeArrayList.add(globalEdgeTable.get(i));
		// Removing all the edges from the global table that were added to active
				globalEdgeTable.remove(i); // Since ArrayList, indices are shifted left
			} else i++; // If nothing removed increment index
		}
		for (int i = 0 ; i < globalEdgeTable.size(); i++) {
			System.out.print("Remaining Global Edge "+i+": " + globalEdgeTable.get(i).getY_Min());
			System.out.print(" " + globalEdgeTable.get(i).getY_Max());
			System.out.print(" " + globalEdgeTable.get(i).getX_Val());
			System.out.println(" " + globalEdgeTable.get(i).getSlope());
		}
		
		// Sort by X-Values only, changing the mode in the comparator
		for(int i = 0; i < activeEdgeArrayList.size(); i++) {
			activeEdgeArrayList.get(i).setSortMode(1);
		} 
		Collections.sort(activeEdgeArrayList);
		
		int x_Last_Index = 0;
		System.out.println("Printing the X_val-sorted Active Edge Table (Array List)...");
		for (int i = 0 ; i < activeEdgeArrayList.size(); i++) {
			System.out.print("Active Edge "+i+": " + activeEdgeArrayList.get(i).getY_Min());
			System.out.print(" " + activeEdgeArrayList.get(i).getY_Max());
			System.out.print(" " + activeEdgeArrayList.get(i).getX_Val());
			System.out.println(" " + activeEdgeArrayList.get(i).getSlope());
			if (i == activeEdgeArrayList.size()-1) {
				x_Last_Index = (int) activeEdgeArrayList.get(i).getX_Val();
			}
		}
		if (x_Last_Index == 0) { 
			System.err.println("Improper Last Index; should not be = '0'");
		}
		
////////////////////////////////////////////////////////////////////////////////	
// Begin the filling.... TODO
		System.out.println("Dotting the points...");
		glPointSize(2);
				
		int parity = EVEN; //0
		boolean globalEdgeTableEmpty = false;
		boolean lastLoop = false;
		
		glBegin(GL_POINTS);
			while (!lastLoop ) {
				// Assures that this will be the last loop once global edge table is empty
				if (globalEdgeTableEmpty) { lastLoop = true; }
				// Sets a new y_Max to fill up to
				if (afterInitialY_Max) {
					y_MaxLineOfY_Min = (int) activeEdgeArrayList.get(0).getY_Max();
				}				
				
				System.out.println("Scanline at y = " + scanLine);
				// Assumes that since the active edge array list is sorted in ascending
				// order, that this will hold the max x_val of the edges.
				int maxEdgeIndex = activeEdgeArrayList.size()-1;
				System.out.println("Last index of arraylist: " + maxEdgeIndex);
				int maxEdgeXVal = (int) activeEdgeArrayList.get(maxEdgeIndex).getX_Val();
				// SCANLINE STARTS AT THE Y_MIN, checks all x_vals on said line...
				System.out.println("Max edge X value: " + maxEdgeXVal);
				System.out.println("Min edge X value: " + y_MaxLineOfY_Min);
				
			while (scanLine < y_MaxLineOfY_Min){
					int edgeN = 0;
					for(int x = 0; x <= maxEdgeXVal && edgeN != activeEdgeArrayList.size(); x++) {
						//System.out.println("X is at: " + x);
						// Important to grab the (int) value so as to have a comparable
						// value to integer 'x'. The float value of X_Val that actual
						// gets retrieved should still keep its float value.
						if (x == (int)activeEdgeArrayList.get(edgeN).getX_Val()) {
							System.out.println("Current Edge: " + edgeN);
							System.out.println("Edge at x = " + x + " encountered.");
							if (parity == EVEN) {
								System.out.println("Parity changed to ODD.");
								parity = ODD;
							}
							else if (parity == ODD) {
								System.out.println("Parity changed to EVEN.");
								parity = EVEN;
							}
							//SPECIAL PARITY CASE
							if (x == maxEdgeXVal && edgeN != activeEdgeArrayList.size()-1) {
								System.out.println("SPECIAL PARITY CASE");
								edgeN++;
								System.out.println("Current Edge: " + edgeN);
								System.out.println("Edge at x = " + x + " encountered.");
								glVertex2f(x, scanLine);
								System.out.println("Parity changed to EVEN.");
								parity = EVEN;
							}
							else edgeN++;
						} // end if statement
						if (parity == ODD) glVertex2f(x, scanLine);
					} // end for loop
					// Reset for the next scan line
					scanLine++;
					System.out.println("Parity reset to EVEN...");
				  parity = EVEN;
					
					// NEED TO UPDATE X-VALUES by adding SLOPE
					// THEN SORT BY X_VAL AGAIN
					
					// Stores the float x_val from adding slope to X_val..
					float newX_ValFloat;
					// To prevent lossyness, we store the float value into the slope,
					// but when comparing the int x value to this slope, we need to 
					// convert the comparision to int (int); does not change the slope
					// value, as it should not (Stays as a float value)					
					for (int i = 0; i < activeEdgeArrayList.size(); i++) {
						newX_ValFloat = activeEdgeArrayList.get(i).getX_Val() 
										+ activeEdgeArrayList.get(i).getSlope();
						activeEdgeArrayList.get(i).setX_Val(newX_ValFloat);
					}
					
					// Sort by X-Values only, changing the mode in the comparator
					for(int i = 0; i < activeEdgeArrayList.size(); i++) {
						activeEdgeArrayList.get(i).setSortMode(1);
					}
					//System.out.println("Current sort mode: " + activeEdgeArrayList.get(0).getSortMode());
					Collections.sort(activeEdgeArrayList);
				} // end while loop
				
			// removing all edges that are equal to (Y-MAX) of the current scan line
			// add any global edges equal to current y (y_min)
			for (int i = 0 ; i < globalEdgeTable.size(); i++) {
				System.out.print("Remaining Global Edge "+i+": " + globalEdgeTable.get(i).getY_Min());
				System.out.print(" " + globalEdgeTable.get(i).getY_Max());
				System.out.print(" " + globalEdgeTable.get(i).getX_Val());
				System.out.println(" " + globalEdgeTable.get(i).getSlope());
			}
		
			System.out.println("\nCurrent scanline: " + scanLine);

			x_Last_Index = 0;
			System.out.println("Printing current Active Edge Table (Array List)...");
			for (int i = 0 ; i < activeEdgeArrayList.size(); i++) {
				System.out.print("Active Edge "+i+": " + activeEdgeArrayList.get(i).getY_Min());
				System.out.print(" " + activeEdgeArrayList.get(i).getY_Max());
				System.out.print(" " + activeEdgeArrayList.get(i).getX_Val());
				System.out.println(" " + activeEdgeArrayList.get(i).getSlope());
				if (i == activeEdgeArrayList.size()-1) {
					x_Last_Index = (int) activeEdgeArrayList.get(i).getX_Val();
				}
			}
			if (x_Last_Index == 0) { 
				System.err.println("Improper Last Index; should not be = '0'");
			}
			
			//////////////////////////////////////////////////////////////////////////
			//REMOVING the unneeded edges (where Y-Max equals the scanline)
			//////////////////////////////////////////////////////////////////////////
			for (int i = 0; i < activeEdgeArrayList.size();) {
				if (activeEdgeArrayList.get(i).getY_Max() == scanLine) {
					activeEdgeArrayList.remove(i);
				} else { i++; }
			}
			System.out.println("Active Edge Table...removed edges with Y_Max of: " + scanLine);
			for (int i = 0 ; i < activeEdgeArrayList.size(); i++) {
				System.out.print("Active Edge "+i+": " + activeEdgeArrayList.get(i).getY_Min());
				System.out.print(" " + activeEdgeArrayList.get(i).getY_Max());
				System.out.print(" " + activeEdgeArrayList.get(i).getX_Val());
				System.out.println(" " + activeEdgeArrayList.get(i).getSlope());
				if (i == activeEdgeArrayList.size()-1) {
					x_Last_Index = (int) activeEdgeArrayList.get(i).getX_Val();
				}
			}
			if (x_Last_Index == 0) { 
				System.err.println("Improper Last Index; should not be = '0'");
			}
			if (activeEdgeArrayList.isEmpty()) {
				System.out.println("Active Edge Table is empty!");
			}
			
			//////////////////////////////////////////////////////////////////////////
			//CHECKING GLOBAL EDGE TABLE TO ADD ANY EDGES equal to scanline
			//////////////////////////////////////////////////////////////////////////
			for (int i = 0 ; i < globalEdgeTable.size(); ) {
				if (globalEdgeTable.get(i).getY_Min() == scanLine) {
					System.out.println("Adding an edge from global edge table to active edge...");
					activeEdgeArrayList.add(globalEdgeTable.get(i));
					// ArrayList automatically shifts objects to left when removing
					globalEdgeTable.remove(i);
				} else i++; // If nothing removed, go to the next index
			}
			// Prints out the new Active Edge Table
			for (int i = 0 ; i < activeEdgeArrayList.size(); i++) {
				System.out.print("Active Edge "+i+": " + activeEdgeArrayList.get(i).getY_Min());
				System.out.print(" " + activeEdgeArrayList.get(i).getY_Max());
				System.out.print(" " + activeEdgeArrayList.get(i).getX_Val());
				System.out.println(" " + activeEdgeArrayList.get(i).getSlope());
				if (i == activeEdgeArrayList.size()-1) {
					x_Last_Index = (int) activeEdgeArrayList.get(i).getX_Val();
				}
			}
			if (x_Last_Index == 0) { 
				System.err.println("Improper Last Index; should not be = '0'");
			}
			for (int i = 0 ; i < globalEdgeTable.size(); i++) {
				System.out.print("Remaining Global Edge "+i+": " + globalEdgeTable.get(i).getY_Min());
				System.out.print(" " + globalEdgeTable.get(i).getY_Max());
				System.out.print(" " + globalEdgeTable.get(i).getX_Val());
				System.out.println(" " + globalEdgeTable.get(i).getSlope());
			}
			if (globalEdgeTable.isEmpty()) {
				System.out.println("Global Edge Table is empty!");
				globalEdgeTableEmpty = true;
			}
			
			// NOTE: Need to set the sort Mode to "1" after each time before calling
			// the Collections.sort, since edges are initialized to sort mode = '0',
			// which, according to the Edge class, would mean the sorting would be
			// applied to Y-min, Y-max, X-val, and then slope.
			// We only want to sort by X-Val though, while the filling algorithm
			// takes place
			
			// Sort by X-Values only, changing the mode in the comparator
			for(int i = 0; i < activeEdgeArrayList.size(); i++) {
				activeEdgeArrayList.get(i).setSortMode(1);
			}
			//System.out.println("Current sort mode: " + activeEdgeArrayList.get(0).getSortMode());
			Collections.sort(activeEdgeArrayList);
			
			// Variable became necessary after buggy use of looping.
			// such as poor way of initializing values; thus, "flag state"-like
			// codes were needed...
			afterInitialY_Max = true;
		} // end glBegin while loop
		glEnd();

		if (activeEdgeArrayList.isEmpty()) {
			System.out.println("No more edges left therefore...");
		}
		else System.out.println("Checking file for next Polygon...\n");
	} // end fill method (companion to scanLinePolygonFillAlgorithm
	
	/**
	 * method: main
	 * purpose: Creates an instance of the TransformPolygonFill class, so we
	 * do not have to create any of the other methods as static references.
	 * @param args 
	 */
	public static void main(String[] args) throws LWJGLException {
		TransformPolygonFill tpf = new TransformPolygonFill();
		tpf.start();
	}
}