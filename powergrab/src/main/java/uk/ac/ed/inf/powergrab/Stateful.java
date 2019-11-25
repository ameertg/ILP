package uk.ac.ed.inf.powergrab;


import com.mapbox.geojson.*;
import java.util.*;

public class Stateful {
	public Position start;
	public Position location;
	
	private List<Feature> stations;
	private Map map;
	
	private Stack plan;
	private Point target;
	
	public double power;
	public double coins;
	
	public Stateful(Position loc, Map map) {
		this.start = loc;
		this.location = start;
		this.stations = map.features;
		this.map = map;
	}
	

	
	public Stack<Direction> findPath(Position a, Point b) {
		PriorityQueue<Node> unexplored;
		LinkedList<Node> explored;
				
		unexplored = new PriorityQueue<Node>();
        explored = new LinkedList<Node>();
        unexplored.add(new Node(a, 0)); // add starting node to open list

        Boolean done = false;
        Node current;
        while (!done) {
            current = unexplored.poll(); // get node with lowest fCosts from unexplored
            explored.add(current); // add current node to closed list

            if (Map.distance(current.pos, b) < 0.0025) { // found goal
                return Node.getPath(current);
            }

            // for all adjacent nodes:
            List<Node> adjacentNodes = current.getChildren(map);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                unexplored.add(adjacentNodes.get(i));
            }

            if (unexplored.isEmpty()) { // no path exists
                return new Stack<Direction>(); // return empty list
            }
        }
        return null; // unreachable
		
	}
}
