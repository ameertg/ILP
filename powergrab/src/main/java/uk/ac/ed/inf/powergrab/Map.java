package uk.ac.ed.inf.powergrab;
import java.net.*;
import java.io.*;


public class Map {
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
        
        this.mapSource = mapSrc.toString();
    }
		
}
