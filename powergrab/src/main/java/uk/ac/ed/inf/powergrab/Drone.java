package uk.ac.ed.inf.powergrab;


// This class defines the general makeup of a drone
public abstract class Drone {
	public double coins;
	public double power;
	public Map map;
	

	public Position location;
	
	public abstract Direction makeMove();
}
