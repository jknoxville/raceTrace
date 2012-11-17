package uk.ac.cam.jk510.part2project.location;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSDriver implements LocationListener {
	
	int logicalTime = 0;
	private static GPSDriver driver;
	private static Device thisDevice;

	private GPSDriver(LocationManager locationManager) {
		
		//TODO: might want to add extra providers and use the logic on android guide site to select between them.
		
		thisDevice = Session.getThisDevice();
		
		//Register for GPS updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
		driver = this;
	}
	
	static public void init(LocationManager lm) {
		if(driver == null) {
			new GPSDriver(lm);
		}
	}
	
	public void onLocationChanged(Location l) {
		Coords coords = toCartesian(l);
		try {
			PositionStore.insert(thisDevice, coords);
		} catch (IncompatibleCoordsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static GPSDriver getGPSDriver() {
		if(driver == null) {
			return null;
		} else {
			return driver;
		}
	}
	
	private Coords toCartesian(Location location) {
		//TODO do proper conversion
		int x = (int) location.getLatitude();
		int y = (int) location.getLongitude();
		int alt = (int) location.getAltitude();
		Coords coords = new CoordsTXYA(useLogicalTime(), x, y, alt);
		return coords;
	}
	
	private int useLogicalTime() {
		int temp = logicalTime;
		logicalTime++;
		return temp;
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
