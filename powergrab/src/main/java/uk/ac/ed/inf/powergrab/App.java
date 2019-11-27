package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;

import java.io.*;

public class App 
{
    public static void main( String[] args )
    {
    	int day, month, year;
    	double latitude, longitude;
    	int seed;
    	String state;
    	
    	Drone drone;
    	
    	Map map;
    	
    	// Parse command-line args
    	try {
    		if(args.length != 7) {
    			throw new Exception();
    		}
    		
    		day = Integer.parseInt(args[0]);
    		month = Integer.parseInt(args[1]);
    		year = Integer.parseInt(args[2]);
    		latitude = Double.parseDouble(args[3]);
    		longitude = Double.parseDouble(args[4]);
    		seed = Integer.parseInt(args[5]);
    		state = args[6];
    		
    		if (!(state == "stateless" || state == "stateful")) {
    			throw new Exception();
    		}
    	}
    	catch(Exception e) {
    		System.out.println("Incorrect argument format. Use: DD MM YYYY lat long seed stateless/stateful");
    		return;
    	}
    	
    	Position start = new Position(latitude, longitude);
    	
    	// Load map
    	String mapAddress = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%n/%n/%n/powergrabmap.geojson", year, month, day);
    	try {
    		map = new Map(mapAddress);
    	}
    	catch(Exception e) {
    		System.out.println("Map at address " + mapAddress + " is unreachable.");
    		return;
    	}
    	
    	// Get state and create drone
		if(state == "stateless") {
			drone = new Stateless(start, seed, map);
		}
		else {
			drone = new Stateful(start, map);
		}
		
		// Make moves until out of power or taken too many steps
		int count = 0;
		while(drone.power > 0 && count <= 250) {
			drone.makeMove();
			count++;
		}
		
		// Draw path trace onto map
		drone.map.features.add(Feature.fromGeometry(LineString.fromLngLats(drone.map.path)));
		String result = FeatureCollection.fromFeatures(drone.map.features).toJson();
		
		// Print out json and write to file
		System.out.println(result);
		String fileName = String.format("%s-%n-%n-%n.txt", state, day, month, year);
		
		try {
			writeToFile(result, fileName);
		}
		catch (IOException e) {
			System.out.println("Unable to write to file " + fileName);
			return;
		}
    }

	
    
    public static void writeToFile(String in, String fName) throws IOException{
    		    BufferedWriter writer = new BufferedWriter(new FileWriter(fName));
    		    writer.write(in);
    		     
    		    writer.close();
    }

}
