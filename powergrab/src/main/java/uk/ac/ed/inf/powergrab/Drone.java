package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public abstract class Drone {
	public double coins;
	public double power;
	public Map map;
	
	public ArrayList<Direction> moves;

	public Position location;
	
	public abstract Direction makeMove();
}
