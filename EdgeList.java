/***************************************************************
* file: EdgeList.java
* author: D. Tapit
* class: CS 445 – Computer Graphics
*
* assignment: part of Program #2
* date last modified: 4/23/2016
*
* purpose: EdgeList class that holds an ArrayList of Edges.
* NOTE: In the end, this was not necessary, though kept for history's sake.
****************************************************************/
import java.util.ArrayList;
/**
 * @author DTapit
 */
public class EdgeList {
	private ArrayList<Edge> edgeList;
	public EdgeList() { edgeList = new ArrayList<Edge>(); }
	public void addEdge(Edge e) { edgeList.add(e); }
	public Edge getEdge(int index) { return edgeList.get(index); }
	public int edgeListSize() { return edgeList.size(); }		
}