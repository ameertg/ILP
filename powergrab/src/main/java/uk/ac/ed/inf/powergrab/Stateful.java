package uk.ac.ed.inf.powergrab;


import com.mapbox.geojson.*;
import java.util.*;

public class Stateful {
	public Position start;
	public Position location;
	
	private ArrayList<Feature> goals;
	private ArrayList<Feature> bad = new ArrayList<Feature>();
	private Map map;
	
	private Stack<Direction> plan = new Stack<Direction>();
	private Feature target = null;
	
	public double power = 250;
	public double coins = 0;
	
	public Stateful(Position loc, Map map) {
		this.start = loc;
		this.location = start;
		this.goals = new ArrayList(map.features);
		this.map = map;
		
		// Remove all negative stations from goals
		for(Feature f:this.goals) {
			if(f.getProperty("coins").getAsDouble() < 0) {
				this.bad.add(f);
			}
		}
		for(Feature f:this.bad) {
			this.goals.remove(f);
		}
	}
	
	
	public void makeMove() {
		Direction nextMove = Direction.N;
		double[] result;
		
		// If there are unvisited goals left
		if (this.goals.size() > 0) {
			
			// Get a target if none exists
			while (this.plan.empty() && this.goals.size() > 2) {
				
				this.target = Map.nearestFeature(this.location, this.goals);
				Point targetCoords = ((Point)(target.geometry()));
				if(Map.distance(this.location, targetCoords) < 0.00025) {
					this.goals.remove(this.target);
					continue;
				}
				this.plan = findPath(this.location, (Point)this.target.geometry());
				System.out.println(this.goals.size());
			}
			
			if (!this.plan.empty()) {
				nextMove = this.plan.pop();
			}
			
		}
		
		// Otherwise move randomly while avoiding bad nodes
		else {
			Position newPos;	
			for (Direction d : Direction.values()) {
				// Check to see if a move is within the play area
				newPos = location.nextPosition(d);
				if (!newPos.inPlayArea()) {
					continue;
				}
				
				Boolean badDirection = false;
				
				for(Feature b : this.bad) {
					if (b.geometry() instanceof Point) {
						if (Map.distance(newPos, (Point)b.geometry()) < 0.00025) {
							badDirection = true;
							break;
						}
					}
				}
				
				if (badDirection) {
					continue;
				}
				nextMove = d;
				break;
			}
		}
			
		// Update location and map values
		this.location = this.location.nextPosition(nextMove);
		result = map.update(this);
		// Update coins and power
		this.coins = result[0];
		this.power = result[1] - 1.25;
	}

	
	public Stack<Direction> findPath(Position a, Point b) {
		PriorityQueue<Node> unexplored; //Priority Queue ordered on F-costs for each node
		LinkedList<Node> explored;	
		unexplored = new PriorityQueue<Node>();
        explored = new LinkedList<Node>();
        // Add starting point to unexplored
        unexplored.add(new Node(a, 0)); 

        Boolean done = false;
        Node current;
        while (!done) {
        	// Get node with lowest cost from unexplored
            current = unexplored.poll(); 
            // Add current node to explored list
            explored.add(current); 

            // Found goal
            if (Map.distance(current.pos, b) < 0.00025) {
                return Node.getPath(current);
            }

            // For all adjacent nodes:
            List<Node> adjacentNodes = current.getChildren(map, this.power);
            double cost;
            for (int i = 0; i < adjacentNodes.size(); i++) {
            	Node adj = adjacentNodes.get(i);
            	Boolean ignore = false;
            	ArrayList<Node> prune = new ArrayList<Node>();
            	
            	// Prune nodes at the same location but with greater cost
            	for(Node n: explored) {
            		if(n.pos == adj.pos) {
            			if(n.gcost < adj.gcost) {
            				ignore = true;
            			}
            			else {
            				prune.add(n);
            			}
            		}
            	}
            	
            	for(Node n: prune) {
            		explored.remove(n);
            	}
            	
            	if(!ignore) {
            		cost = Map.distance(adj.pos, b)/0.0003 * 1.25; // Heuristic cost = min power needed to get to goal
            		adj.sethCost(cost); 
            		unexplored.add(adj);
            	}
            }
            
            // No path exists
            if (unexplored.isEmpty()) { 
                return new Stack<Direction>(); // Return empty list
            }
        }
        return null; // line unreachable
		
	}
}