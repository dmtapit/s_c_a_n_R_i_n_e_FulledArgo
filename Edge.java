/***************************************************************
* file: Edge.java
* author: D. Tapit
* class: CS 445 – Computer Graphics
*
* assignment: part of Program #2
* date last modified: 4/23/2016
*
* purpose: Edge class used for the Scanline Polygon Fill Algorithm
* Will hold values such as y-Min, y-Max, x-Val, and 1/m (slope)
* NOTE: Contains a comparable that will be used in conjunction
* with Collection.sort, which will order by y-Min, y-Max, x-Val,
* and then slope, respectively, default by ASCENDING order.
****************************************************************/
/**
 * @author DTapit
 */
public class Edge implements Comparable<Edge>{
	private int sortMode;
	private float y_Min;
	private float y_Max;
	private float x_Val;
	private float slope;
	public Edge() { y_Min = 0; y_Max = 0; x_Val = 0; slope = 0; }
	public Edge(float y_Min, float y_Max, float x_Val, float slope) {
		this.sortMode = 0;
		this.y_Min = y_Min;
		this.y_Max = y_Max;
		this.x_Val = x_Val;
		this.slope = slope;
	}
	public void setY_Min(float y_min) { y_Min = y_min; }
	public void setY_Max(float y_max) { y_Max = y_max; }
	public void setX_Val(float x_val) { x_Val = x_val; }
	public void setSlope(float s) { slope = s; }
	public float getY_Min() { return y_Min; }
	public float getY_Max() { return y_Max; }
	public float getX_Val() { return x_Val; }
	public float getSlope() { return slope; }
	
	public float getSortMode() { return sortMode; }
	public void setSortMode(int newMode) { sortMode = newMode; }
		
	/**
	 * method: compareTo
	 * purpose: Sorts Edges by y-Min, y-Max, x-Val, and then slope, respectively,
	 * default by ASCENDING order.  (When values are checked, if they happen to
	 * be equal to each other, it will compareTo the next value given...)
	 * @param that
	 * @return 
	 */
	public int compareTo(Edge that) {
		if (sortMode == 0) {
			Float fYMin = new Float(y_Min);
			Float thatFYMin = new Float(that.y_Min);
			if (fYMin.compareTo(thatFYMin) == 0) {
				Float fYMax = new Float(y_Max);
				Float thatFYMax = new Float(that.y_Max);
				if (fYMax.compareTo(thatFYMax) == 0) {
					Float fXVal = new Float(x_Val);
					Float thatXVal = new Float(that.x_Val);
					return fXVal.compareTo(thatXVal);
				}
				else return fYMax.compareTo(thatFYMax);
			}
			else return fYMin.compareTo(thatFYMin);
		}
		else {
			Float fXVal = new Float(x_Val);
			Float thatFXVal = new Float(that.x_Val);
			return fXVal.compareTo(thatFXVal);
		}
	}
}