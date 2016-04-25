/***************************************************************
* file: Point.java
* author: D. Tapit
* class: CS 445 – Computer Graphics
*
* assignment: part of Program #2
* date last modified: 4/16/2016
*
* purpose: Creates the Point class that will hold the 'x' and 'y'
* values of a point. To be used in conjunction with an ArrayList.
****************************************************************/

public class Point {
	private float x;
	private float y;
	// CONSTRUCTORS
	public Point () { x = 0; y = 0;}
	public Point(float initX, float initY) {
		x = initX;
		y = initY;
	}
	// GETTERS
	public float getX() { return x; }
	public float getY() { return y; }
	// SETTERS
	public void setX (float newX) { x = newX; }
	public void setY (float newY) { y = newY; }	
}
