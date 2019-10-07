package uk.ac.ed.inf.powergrab;
import java.net.*;
import java.io.*;
import java.util.*;

import com.mapbox.geojson.*;


public class Map {
	List<Feature> features;
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
	
	// Returns the euclidean distance between two points
	public double distance(Position a, Position b) {
		return Math.pow(Math.pow(a.latitude - b.latitude, 2) + Math.pow(a.longitude - b.longitude, 2), 0.5);
	}
	
	public double distance(Position a, Point b) {
		return Math.pow(Math.pow(a.latitude - b.coordinates().get(1), 2) + Math.pow(a.longitude - b.coordinates().get(0), 2), 0.5);
	}
}
