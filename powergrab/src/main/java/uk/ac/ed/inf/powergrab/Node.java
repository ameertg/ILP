package uk.ac.ed.inf.powergrab;
import java.util.*;
import com.mapbox.geojson.*;

public class Node implements Comparable<Node>{
	Position pos;
	Node parent = null;
	Direction d = null;
	double gcost;
	double hcost;
	
	public Node(Position e, double g) {
		this.pos = e;
		this.gcost = g;
	}
	
	public Node(Position e, double g, Node parent, Direction d) {
		this.pos = e;
		this.gcost = g;
		this.parent = parent;
		this.d = d;
	}
	
	
	public List<Node> getChildren(Map map){
		ArrayList<Node> children = new ArrayList<Node>();
		for (Direction d: Direction.values()) {
			Position next = this.pos.nextPosition(d);
			if (next.inPlayArea()) {
				List<Feature> nearby = map.nearbyFeatures(next, 0.0025);
				double cost = this.gcost + 1.25;
				for(Feature f: nearby) {
					cost = cost - f.getProperty("power").getAsDouble();
				}
				children.add(new Node(next, cost, this, d));
			}
		}
		
		return children;
	}
	
	
	public static Stack<Direction> getPath(Node n) {
		Node current = n;
		Stack<Direction> path = new Stack<Direction>();
		
		while (current.parent != null) {
			path.push(current.d);
			current = current.parent;
		}
		return path;
	}
	
	public void setParent(Node p) {
		this.parent = p;
	}
	
	public void sethCost(double h) {
		this.hcost = h;
	}
	
	public void setgCost(double g) {
		this.gcost = g;
	}
	
	@Override
	public int compareTo(Node other) {
		return -Double.compare(this.gcost + this.gcost, other.hcost + other.hcost);
	}
}
