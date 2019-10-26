package uk.ac.ed.inf.powergrab;
import java.util.*;
import com.mapbox.geojson.*;

public class Stateless{
	public Position location;
	public double power;
	public double coins;
	Random rng;
	
	private final double lookahead = 0.00025;
	
	public Stateless(Position loc, long seed) {
		this.location = loc;
		this.power = 250;
		this.coins = 0;
		rng = new Random(seed);
	}
	
	public void makeMove(Map map) {
		ArrayList<Feature> landings;
		LinkedList<Direction> notBad = new LinkedList<Direction>(Arrays.asList(Direction.values())); // List of all the good directions
		
		float sum = 0;
		Direction next = null;
		Position newPos;
		
		//Iterate over every direction to get the best move
		for (Direction d : Direction.values()) {
			newPos = location.nextPosition(d);
			// Check to see if move d is in play area
			if (!newPos.inPlayArea()) {
				notBad.remove(d);
				continue;
			}
			landings = map.nearbyFeatures(newPos, lookahead); // Get list of all landings within range
			
			float tempSum = 0;
			// Get sum of coins and power within range
			for (Feature l : landings) {
				tempSum = l.getProperty("coins").getAsFloat() + l.getProperty("power").getAsFloat();
			}
			// If sum is positive make this choice
			if (tempSum > sum) {
				next = d;
				break;
			}
			// If sum is negative remove this choice from the list
			else if (tempSum < 0) {
				notBad.remove(d);
			}
		}
		
		// If no positive directions are found take a random non-negative direction
		if (next == null && notBad.size() > 0) {
			next = notBad.get(rng.nextInt(notBad.size()));
		}
		
		// Update position and map
		location = location.nextPosition(next);
		double[] values = map.update(this);	
		
		// Update no. coins and power
		this.coins = values[0];
		this.power = values[1] - 1.25;
	}
}
