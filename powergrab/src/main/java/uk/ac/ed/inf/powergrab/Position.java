package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		double newlat;
		double newlong;
		
		double angle = Math.toRadians(getAngle(direction));
		final double r = 0.0003;
		
		// Calculate the vertical/horizontal change and 
		// add this to the previous coordinates
		newlat = r * Math.cos(angle) + this.latitude;
		newlong = r * Math.sin(angle) + this.longitude;
		
		return new Position(newlat, newlong);
	}
	
	public boolean inPlayArea() {
		// Check to see whether or not drone is within play area square
		if ((this.latitude > 55.942617) 
			&& (this.latitude < 55.946233) 
			&& (this.longitude > -3.192473)
			&& (this.longitude < -3.184319)) {
			return true;
		}
		else return false;
	}
	
	// Function to convert direction to angle in degrees
	private double getAngle(Direction direction) {
		switch (direction) {
			case N: return 0;
			case NNE: return 22.5;
			case NE: return 45;
			case ENE: return 67.5;
			case E: return 90;
			case ESE: return 112.5;
			case SE: return 135;
			case SSE: return 157.5;
			case S: return 180;
			case SSW: return 202.5;
			case SW: return 225;
			case WSW: return 247.5;
			case W: return 270;
			case WNW: return 292.5;
			case NW: return 315;
			case NNW: return 337.5;
			default: return 0; 
		}
	}
	
	// Takes an angle in radians and returns a compass direction approximately in the same direction
	public static Direction getDirection(double angle) {
		Direction next = Direction.N;	
		// Convert angle to a number between -16 and 16 for each compass direction
		angle = angle * 16/Math.PI;

		if(-1 <= angle && angle < 1) {
			next = Direction.E;
		}
		else if(1 <= angle && angle < 3) {
			next = Direction.ENE;
		}
		else if(3 <= angle && angle < 5) {
			next = Direction.NE;
		}
		else if(5 <= angle && angle < 7) {
			next = Direction.NNE;
		}
		else if(7 <= angle && angle < 9) {
			next = Direction.N;
		}
		else if(9 <= angle && angle < 11) {
			next = Direction.NNW;
		}
		else if(11 <= angle && angle < 13) {
			next = Direction.NW;
		}
		else if(13 <= angle && angle < 15) {
			next = Direction.WNW;
		}
		else if(angle >= 15 || angle < -15) {
			next = Direction.W;
		}
		else if(-15 <= angle && angle < -13) {
			next = Direction.WSW;
		}
		else if(-13 <= angle && angle < -11) {
			next = Direction.SW;
		}
		else if(-11 <= angle && angle < -9) {
			next = Direction.SSW;
		}
		else if(-9 <= angle && angle < -7) {
			next = Direction.S;
		}
		else if(-7 <= angle && angle < -5) {
			next = Direction.SSE;
		}
		else if(-5 <= angle && angle < -3) {
			next = Direction.SE;
		}
		else if(-3 <= angle && angle < -1) {
			next = Direction.ESE;
		}
		return next;
	}
}