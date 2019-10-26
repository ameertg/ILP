package uk.ac.ed.inf.powergrab;
import java.net.*;
import java.io.*;
import java.util.*;

import com.mapbox.geojson.*;


public class Map {
	List<Feature> features;
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

		for(Feature feature : this.features) {
			if (feature.geometry() instanceof Point) {
				if (distance(p, (Point)feature.geometry()) <= d) {
					near.add(feature);
				}
			}
		}
		return near;
	}
	
	
	
	// Updates the features and returns an array containing final coin and power values
	public double[] update(Stateless drone) {
		ArrayList<Feature> near = nearbyFeatures(drone.location, 0.00025);
		double coins;
		double power;
		for(Feature f: near) {
			coins = f.getProperty("coins").getAsDouble();
			power = f.getProperty("power").getAsDouble();
			if (-coins > drone.coins) {
				drone.coins = 0;
				f.removeProperty("coins");
				f.addNumberProperty("coins", drone.coins + coins);
			}
			else {
				drone.coins = coins + drone.coins;
				f.removeProperty("coins");
				f.addNumberProperty("coins", 0);
			}
			
			if (-power > drone.power) {
				drone.power = 0;
				f.removeProperty("power");
				f.addNumberProperty("power", drone.power + coins);
			}
			else {
				drone.power = coins + drone.power;
				f.removeProperty("power");
				f.addNumberProperty("power", 0);
			}
		}
		this.path.add(Point.fromLngLat(drone.location.longitude, drone.location.latitude));
		return new double[]{drone.coins, drone.power};
	}
	
	// Updates the features and returns an array containing final coin and power values
		public double[] update(Stateful drone) {
			ArrayList<Feature> near = nearbyFeatures(drone.location, 0.00025);
			double coins;
			double power;
			for(Feature f: near) {
				coins = f.getProperty("coins").getAsDouble();
				power = f.getProperty("power").getAsDouble();
				if (-coins > drone.coins) {
					drone.coins = 0;
					f.removeProperty("coins");
					f.addNumberProperty("coins", drone.coins + coins);
				}
				else {
					drone.coins = coins + drone.coins;
					f.removeProperty("coins");
					f.addNumberProperty("coins", 0);
				}
				
				if (-power > drone.power) {
					drone.power = 0;
					f.removeProperty("power");
					f.addNumberProperty("power", drone.power + coins);
				}
				else {
					drone.power = coins + drone.power;
					f.removeProperty("power");
					f.addNumberProperty("power", 0);
				}
			}
			this.path.add(Point.fromLngLat(drone.location.longitude, drone.location.latitude));
			return new double[]{drone.coins, drone.power};
		}
}
