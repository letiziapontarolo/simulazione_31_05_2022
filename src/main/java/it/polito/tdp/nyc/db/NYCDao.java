package it.polito.tdp.nyc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.nyc.model.Arco;
import it.polito.tdp.nyc.model.Hotspot;

public class NYCDao {
	
	public List<String> listaProvider() {
		
		String sql = "SELECT distinct ny.Provider "
				+ "FROM nyc_wifi_hotspot_locations ny "
				+ "ORDER BY ny.Provider";
		List<String> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(res.getString("Provider"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
	public void creaVertici(List<String> listaQuartieri, String provider) {
		
		String sql = "SELECT distinct ny.City "
				+ "FROM nyc_wifi_hotspot_locations ny "
				+ "WHERE ny.Provider = (?) "
				+ "ORDER BY ny.City";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, provider);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				listaQuartieri.add(res.getString("City"));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}	
	}
	
	public void listaArchi(List<Arco> archi, String provider) {
		
		String sql = "SELECT ny1.City AS city1, ny2.City AS city2, "
				+ "AVG(ny1.Latitude) AS lat1, AVG(ny2.Latitude) AS lat2, "
				+ "AVG(ny1.Longitude) AS lon1, AVG(ny2.Longitude) AS lon2 "
				+ "FROM nyc_wifi_hotspot_locations ny1, nyc_wifi_hotspot_locations ny2 "
				+ "WHERE ny1.City > ny2.City AND ny1.Provider = ny2.Provider "
				+ "AND ny1.Provider = (?) "
				+ "GROUP BY ny1.City, ny2.City";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, provider);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				
				LatLng l1 = new LatLng(res.getDouble("lat1"), res.getDouble("lon1"));
				LatLng l2 = new LatLng(res.getDouble("lat2"), res.getDouble("lon2"));
				double distanza = LatLngTool.distance(l1, l2, LengthUnit.KILOMETER);
				
				Arco a = new Arco(res.getString("city1"), res.getString("city2"), distanza);
				archi.add(a);
				
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}	
		
		
		
	}

	
	public List<Hotspot> getAllHotspot(){
		String sql = "SELECT * FROM nyc_wifi_hotspot_locations";
		List<Hotspot> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Hotspot(res.getInt("OBJECTID"), res.getString("Borough"),
						res.getString("Type"), res.getString("Provider"), res.getString("Name"),
						res.getString("Location"),res.getDouble("Latitude"),res.getDouble("Longitude"),
						res.getString("Location_T"),res.getString("City"),res.getString("SSID"),
						res.getString("SourceID"),res.getInt("BoroCode"),res.getString("BoroName"),
						res.getString("NTACode"), res.getString("NTAName"), res.getInt("Postcode")));
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return result;
	}
	
}
