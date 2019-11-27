package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.FeatureCollection;
import java.io.*;

public class App 
{
    public static void main( String[] args )
    {
    	String day, month, year;
    	String latitude, longitude;
    	int seed;
    	String state = "";
    	
    	Drone drone;
    	
    	Map map;
    	
    	// Parse command-line args
    	try {
    		if(args.length != 7) {
    			throw new Exception();
    		}
    		
    		day = args[0];
    		month = args[1];
    		year = args[2];
    		latitude = args[3];
    		longitude = args[4];
    		seed = Integer.parseInt(args[5]);
    		state = args[6];
    		
    		if (!(state.equals("stateless") || state.equals("stateful"))) {
    			throw new Exception();
    		}
    	}
    	catch(Exception e) {
    		System.out.println("Incorrect argument format. Use: DD MM YYYY lat long seed stateless/stateful");
    		return;
    	}
    	
    	Position start = new Position(Double.parseDouble(latitude), Double.parseDouble(longitude));
    	
    	// Load map
    	String mapAddress = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day);
    	try {
    		map = new Map(mapAddress);
    	}
    	catch(Exception e) {
    		System.out.println("Map at address " + mapAddress + " is unreachable.");
    		return;
    	}
    	
    	// Get state and create drone
		if(state.equals("stateless")) {
			drone = new Stateless(start, seed, map);
		}
		else {
			drone = new Stateful(start, map);
		}
		
		// Make moves until out of power or taken too many steps write to file for each move
		String fileName = String.format("%s-%s-%s-%s.txt", state, day, month, year);
		String line;
		Direction move;
		Position before;
		Position after;
		int count = 0;
		try {
			FileWriter writer = new FileWriter(fileName);
			while(drone.power > 0 && count <= 250) {
				before = drone.location;
				move = drone.makeMove();
				after = drone.location;
				
				//Write values to file
				line = String.format("%f %f %s %f %f %f %f\n",
						before.latitude, before.longitude, move.toString(),
						after.latitude, after.longitude, drone.coins, drone.power);
				writer.write(line);
				count++;
			}
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Unable to write to file " + fileName);
		}
		
		// Draw path trace onto map
		drone.map.drawPath();
		
		String result = FeatureCollection.fromFeatures(drone.map.features).toJson();
		
		// Print out geojson and write to file
		System.out.println(result);
		fileName = String.format("%s-%s-%s-%s.geojson", state, day, month, year);
		
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(result);
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Unable to write to file " + fileName);
			return;
		}
		
		return;
    }


}
