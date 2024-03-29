package uk.ac.cam.jk510.part2project.location;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class GPSDriver implements LocationListener {

	int logicalTime = 0;
	private static GPSDriver instance;
	private Device thisDevice;
	private TextView tv;
	private Location currentLocation;
	private LocationManager lm;

	private GPSDriver(LocationManager locationManager, TextView tv) {

		//TODO: might want to add extra providers and use the logic on android guide site to select between them.
		lm = locationManager;
		thisDevice = Session.getThisDevice();

		//Register for GPS updates
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, Config.getGPSUpdateDistance(), this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.getGPSUpdateTime(), Config.getGPSUpdateDistance(), this);
		
		instance = this;
		this.tv = tv;
		tv.setTextColor(Color.BLACK);
		this.tv.setText("Waiting for location...");
	}

	static public GPSDriver init(LocationManager lm, TextView tv) {
		if(instance == null) {
			new GPSDriver(lm, tv);
		}
		return instance;
	}

	public void onLocationChanged(Location l) {
		boolean useThisLocation = false;

		// logic for deciding whether to use new point
		if(currentLocation == null) {
			useThisLocation = true;
		} else {
			if(l.getTime()>currentLocation.getTime()) {
				if(l.getAccuracy()<=currentLocation.getAccuracy()) {
					useThisLocation = true;
				} else {
					if(l.getTime()>currentLocation.getTime()+5*1000 && l.getProvider() == LocationManager.GPS_PROVIDER) {
						useThisLocation = true;
					}
					else {
						if(l.getTime()>currentLocation.getTime()+10*1000) {
							useThisLocation = true;
						}
					}
				}
			}
		}
		if(useThisLocation) {


			Coords coords = toCartesian(l);

			tv.setText("Lat: "+coords.getCoord(0)+" Long: "+coords.getCoord(1)+" Accuracy: "+l.getAccuracy()+" Speed: "+l.getSpeed()+"m/s");

			ProtocolManager.insertOriginalDataPoint(thisDevice, coords);
			currentLocation = l;
		}

	}

	public static GPSDriver getGPSDriver() {
		if(instance == null) {
			return null;
		} else {
			return instance;
		}
	}

	private Coords toCartesian(Location location) {
		//TODO maybe remove this library? See how much difference it makes.
		double x = location.getLongitude();
		double y = location.getLatitude();
		double alt = location.getAltitude();
		
		LatLng ll = new LatLng(x, y);
		UTMRef utmref = ll.toUTMRef();
		Coords coords = new CoordsTXYA(useLogicalTime(), (float)utmref.getEasting(), (float)utmref.getNorthing(), (float)alt);
		return coords;

	}

	private int useLogicalTime() {
		int temp = logicalTime;
		logicalTime++;
		return temp;
	}

	public void onProviderDisabled(String arg0) {
		// TODO Want to do anything is GPS is disabled?
		/* may want to continue getting other players' updates and not quit app.
		 * may want to ask user to switch it on again.
		 */
		//ask user to switch it on again, but will continue if they dont want to
		//probably not because user must have switched it off themself.
		
		

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	public void destroy() {
		lm.removeUpdates(this);
		instance = null;
	}

}
