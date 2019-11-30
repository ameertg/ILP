package uk.ac.ed.inf.powergrab;
import java.util.*;
import com.mapbox.geojson.*;

public class Node implements Comparable<Node>{
	Position pos;
	int length; // Number of steps until current node
	Node parent = null;
	Direction d = null; // Direction moved from parent to current node
	double gcost; // Cost to node
	double hcost; // Cost to goal
	
	public Node(Position e, double g) {
		this.length = 0;
		this.pos = e;
		this.gcost = g;
	}
	
	// Constructor used only by getChildren
	private Node(Position e, double g, Node parent, Direction d) {
		this.pos = e;
		this.gcost = g;
		this.parent = parent;
		this.d = d;
		this.length = parent.length + 1;
	}
	
	
	// Returns all valid child nodes
	public List<Node> getChildren(Map map, double power){
		ArrayList<Node> children = new ArrayList<Node>();
		// Check each direction
		for (Direction d: Direction.values()) {
			// Update position
			Position next = this.pos.nextPosition(d);
			//Check if in play area else ignore this child
			if (next.inPlayArea()) {
				// Compute cost of node
				ArrayList<Feature> nearby = map.nearbyFeatures(next, 0.00025);
				double cost = this.gcost + 1.25; // gcost is given as power needed to get from root to node
				for(Feature f: nearby) {
					// Add cost of nearby stations to cost of node (negative sign since we want to penalize negative power)
					cost = cost - f.getProperty("power").getAsDouble();
				}
				
				// Check if child is reachable
				if (cost < power) {
					children.add(new Node(next, cost, this, d));
				}
			}
		}
		
		return children;
	}
	
	// Trace nodes back to root and add moves to stack
	public static Stack<Direction> getPath(Node n) {
		Node current = n;
		Stack<Direction> path = new Stack<Direction>();
		
		while (current.parent != null) {
			path.push(current.d);
			current = current.parent;
		}
		return path;
	}
	
	public void sethCost(double h) {
		this.hcost = h;
	}
	
	
	// Create ordering on f-cost
	@Override
	public int compareTo(Node other) {
		return Double.compare(this.gcost + this.hcost, other.gcost + other.hcost);
	}
}
