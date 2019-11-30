package uk.ac.ed.inf.powergrab;


import com.mapbox.geojson.*;
import java.util.*;

public class Stateful extends Drone {
	private ArrayList<Feature> goals;
	private ArrayList<Feature> bad = new ArrayList<Feature>();
	
	private Stack<Direction> plan = new Stack<Direction>();
	private Feature target = null;
	
	
	public Stateful(Position loc, Map map) {		
		// Initialize starting state
		this.coins = 0;
		this.power = 250;
		
		this.location = loc;
		
		this.goals = new ArrayList<Feature>(map.features);
		this.map = map;
		
		// Remove all negative stations from goals
		for(Feature f:this.goals) {
			if(f.getProperty("coins").getAsDouble() < 0) {this.bad.add(f);}
		}
		for(Feature f:this.bad) {this.goals.remove(f);}
		
		this.target = Map.nearestFeature(this.location, this.goals);
	}
	
	
	public Direction makeMove() {
		Direction nextMove;
		if (this.target != null && this.target.getProperty("coins").getAsDouble() == 0 ) {
			// Clear plan
			this.plan = new Stack<Direction>();
			this.goals.remove(this.target);
			
			// If all goals reached make dumb move
			if (this.goals.isEmpty()) {
				this.target = null;
			}
			
			// Otherwise update target and create new plan
			else {
				this.target = Map.nearestFeature(this.location, this.goals);
				this.plan = findPath(this.location, (Point)this.target.geometry());
			}
		}
			
		// If plan exists make a move
		if (!this.plan.empty()) {
			nextMove = this.plan.pop();
		}
		
		// If there are unvisited goals left
		else if (this.goals.size() > 0) {			
			
			// Create and execute new plan
			this.plan = findPath(this.location, (Point)this.target.geometry());

			// If target is unreachable
			if (!this.plan.isEmpty()) {
				nextMove = this.plan.pop();
			}
			else {
				nextMove = dumbMove();
			}
		}
		
		else {
			nextMove = dumbMove();
		}
		
		// Update location and map values
		this.location = this.location.nextPosition(nextMove);
		
		double[] result = this.map.update(this.location, this.power, this.coins);

		// Update coins and power
		this.coins = result[0];
		this.power = result[1] - 1.25;
		
		return nextMove;
	}

	
	private Stack<Direction> findPath(Position a, Point b) {
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

            // Found goal or plan is too long
            if (Map.distance(current.pos, b) <= 0.00025 || current.length == 10) {
                return Node.getPath(current);
            }

            // For all adjacent nodes:
            List<Node> adjacentNodes = current.getChildren(this.map, this.power);
            double cost;
            for (int i = 0; i < adjacentNodes.size(); i++) {
            	Node adj = adjacentNodes.get(i);

	            cost = Map.distance(adj.pos, b)/0.0003 * 1.25; // Heuristic cost = min power needed to get to goal
	            adj.sethCost(cost); 
	            unexplored.add(adj);
            }
            
            // No path exists
            if (unexplored.isEmpty()) { 
                return new Stack<Direction>(); // Return empty list
            }
        }
        return null; // line unreachable
		
	}
	
	// Method to make a decision when no information is available. Similar to stateless.
	private Direction dumbMove() {
		Direction move = Direction.N;
		Position newPos;	
		for (Direction d : Direction.values()) {
			// Check to see if a move is within the play area
			newPos = location.nextPosition(d);
			if (!newPos.inPlayArea()) {
				continue;
			}
			
			Boolean badDirection = false;
			
			// Check if any bad stations are within reach
			for(Feature b : this.bad) {
				if (b.geometry() instanceof Point) {
					if (Map.distance(newPos, (Point)b.geometry()) < 0.00025) {
						badDirection = true;
						break;
					}
				}
			}
			
			// If so ignore this direction
			if (badDirection) {
				continue;
			}
			
			// Pick this direction as soon as a valid move is found
			move = d;
			break;
		}
		
		return move;
	}
}
