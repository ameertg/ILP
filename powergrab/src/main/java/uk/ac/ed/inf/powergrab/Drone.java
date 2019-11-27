package uk.ac.ed.inf.powergrab;

public abstract class Drone {
	public double coins;
	public double power;
	public Map map;

	public Position location;
	
	public abstract void makeMove();
}
