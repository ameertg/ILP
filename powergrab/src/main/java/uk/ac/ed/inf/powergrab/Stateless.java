package uk.ac.ed.inf.powergrab;
import java.util.*;
import com.mapbox.geojson.*;

public class Stateless {
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
	
	public Direction moveChoice(Map map) {
		ArrayList<Feature> landings;
		float sum = 0;
		Direction next = null;
		Position newPos;
		//Iterate over every direction to get the best move
		for (Direction d : Direction.values()) {
			newPos = location.nextPosition(d);
			landings = map.nearbyFeatures(newPos, lookahead);
			float tempSum = 0;
			for (Feature l : landings) {
				tempSum = l.getProperty("coins").getAsFloat() + l.getProperty("power").getAsFloat();
			}
			if (tempSum >= sum) {
				next = d;
			}
		}
		
		if (next == null) {
			//==== Ignore bad directions ====
			// Randomly choose direction
			next = Direction.values()[rng.nextInt(15)];
		}
		
		return next;	
	}
}
