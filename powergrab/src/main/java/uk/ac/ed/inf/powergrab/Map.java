package uk.ac.ed.inf.powergrab;
import java.net.*;
import java.io.*;
import java.util.*;

import com.mapbox.geojson.*;


public class Map {
	List<Feature> features;
	ArrayList<Feature> unused; // All unvisitied
	// Path traced by drone
	LinkedList<Point> path = new LinkedList<Point>();
	String mapSource;
	
	//Gets a map from url
	public Map(String url) throws Exception {
		URL mapURL;
		HttpURLConnection conn;
		
		//Local string builder to dynamically read from input stream
		StringBuilder mapSrc = new StringBuilder();
		
		//Create connection
		mapURL = new URL(url);
		conn = (HttpURLConnection)mapURL.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		
		//Read input stream into mapSrc
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) { 
            mapSrc.append(inputLine);
        	mapSrc.append('\n');
        }
        
        in.close();
        conn.disconnect();
        
        // Extract features from JSON
        this.mapSource = mapSrc.toString();
        this.features = FeatureCollection.fromJson(mapSrc.toString()).features();
        this.unused = new ArrayList<Feature>(this.features);
    }
	
	
	//Returns the nearest feature to a point p given a list of features
	public static Feature nearestFeature(Position p, List<Feature> features) {
		Feature nearest = features.get(0);
		for(Feature f : features) {
			if (f.geometry() instanceof Point) {
				if (distance(p, (Point)f.geometry()) <= distance(p, (Point)nearest.geometry())) {
					nearest = f;
				}
			}
		}
		return nearest;
	}


	// Returns the euclidean distance between two points
	public static double distance(Position a, Position b) {
		return Math.pow(Math.pow(a.latitude - b.latitude, 2) + Math.pow(a.longitude - b.longitude, 2), 0.5);
	}

	public static double distance(Position a, Point b) {
		return Math.pow(Math.pow(a.latitude - b.coordinates().get(1), 2) + Math.pow(a.longitude - b.coordinates().get(0), 2), 0.5);
	}

		
		
	// Returns an array of features within a distance d of p
	public ArrayList<Feature> nearbyFeatures(Position p, double d){
		ArrayList<Feature> near = new ArrayList<Feature>();

		for(Feature feature : this.unused) {
			if (feature.geometry() instanceof Point) {
				if (distance(p, (Point)feature.geometry()) <= d) {
					near.add(feature);
				}
			}
		}
		return near;
	}
	
	// Updates the features and returns an array containing final coin and power values
	public double[] update(Position loc, double dronePower, double droneCoins) {
		// Find stations in accessible radius
		ArrayList<Feature> near = nearbyFeatures(loc, 0.00025);
		double coins;
		double power;
		
		if (!near.isEmpty()) {
			// Get nearest station
			Feature f = nearestFeature(loc, near);
			// Remove station from future consideration
			
			// Get reservoir from station
			coins = f.getProperty("coins").getAsDouble();
			power = f.getProperty("power").getAsDouble();
	
			// Check if value is leaves drone in debt and update drone values accordingly
			if (-coins > droneCoins) {
				droneCoins = 0;
				f.removeProperty("coins");
				f.addNumberProperty("coins", droneCoins + coins);
			}
			else {
				droneCoins = coins + droneCoins;
				f.removeProperty("coins");
				f.addNumberProperty("coins", 0);
				this.unused.remove(f); // Once emptied, remove station from unused
			}

			if (-power > dronePower) {
				dronePower = 0;
				f.removeProperty("power");
				f.addNumberProperty("power", dronePower + coins);
			}
			else {
				dronePower = power + dronePower;
				f.removeProperty("power");
				f.addNumberProperty("power", 0);
			}
		}
		// Add step to path
		this.path.add(Point.fromLngLat(loc.longitude, loc.latitude));
		return new double[]{droneCoins, dronePower};
	}
	
	public void drawPath() {
		this.features.add(Feature.fromGeometry(LineString.fromLngLats(this.path)));
	}
	
}

