package uk.ac.ed.inf.powergrab;


import com.mapbox.geojson.*;
import java.util.*;

public class Stateful {
	public Position location;
	public double power;
	public double coins;
	public Map map;
	
	public ArrayList<Feature> unvisited;
	public ArrayList<Feature> visited;
	public Feature target;
	
	public Stateful(Position loc, Map map) {
		this.location = loc;
		this.power = 250;
		this.coins = 0;
		
		this.map = map;
		
		this.unvisited = new ArrayList<Feature>(map.features);
		this.visited = new ArrayList<Feature>();
		
		this.target = null; // Stores the nearest feature to avoid recalculating every time
		
		this.map.update(this); // Update map to contain starting pos
		
		// Remove all bad landings from consideration
		ArrayList<Feature> bad = new ArrayList<Feature>();
		for(Feature f:this.unvisited) {
			if(f.getProperty("coins").getAsDouble() < 0) {
				bad.add(f);
			}
		}
		for(Feature f:bad) {
			this.unvisited.remove(f);
		}
		bad = null; // Clear variable to allow it to be removed by the garbage collector
	}
	
	public void makeMove() {
		// If there are unvisited good nodes do this
		if (this.unvisited.size() > 0) {
			// Get a target if none exists
			if (this.target == null) {
				this.target = Map.nearestFeature(this.location, this.unvisited);
			}
			
			// Take one step towards target
			List<Double> targetCoords = ((Point)this.target.geometry()).coordinates();
			double angle = Math.atan2((targetCoords.get(1) - this.location.latitude), (targetCoords.get(0) -  this.location.longitude));
			this.location = this.location.nextPosition(Position.getDirection(angle));
			
			// If target reached remove it from the unvisited list and set target to null
			if (Map.distance(this.location, (Point)this.target.geometry())<=0.00025) {
				this.visited.add(this.target);
				this.unvisited.remove(this.target);
				this.target = null;
			}
		}
		
		// Here the code is similar to stateless node
		else {
			Position newPos;
			ArrayList<Feature> landings;
			
			for (Direction d : Direction.values()) {
				// Check to see if a move is within the play area
				newPos = location.nextPosition(d);
				if (!newPos.inPlayArea()) {
					continue;
				}
				
				landings = map.nearbyFeatures(newPos, 0.0003);
				
				// If going towards a bad landing skip this directon
				if (landings.size() > 0) {
					if (landings.get(0).getProperty("coins").getAsDouble() < 0) {
						continue;
					}
				}
				
				this.location = this.location.nextPosition(d);
				break;
			}
		}
		
		// Update map
		double[] values = map.update(this);	
		
		// Update no. coins and power
		this.coins = values[0];
		this.power = values[1] - 1.25;
	}
}
