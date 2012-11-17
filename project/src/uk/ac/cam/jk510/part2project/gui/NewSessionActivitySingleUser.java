package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.location.GPSDriver;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleUser;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.View;

public class NewSessionActivitySingleUser extends NewSessionActivity {

	@Override
	public void onSetupComplete(View view) throws Exception {
		//TODO make this a new thread
		SessionManager smgr = new SessionManagerSingleUser();
		smgr.newSession();
		System.err.println("about to init PM");	//debug
		//TODO make this a new thread
		ProtocolManager.initialiseProtocolManager(smgr.getSession());
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		new GPSDriver(locationManager);
		Intent intent = new Intent(this, MapDisplayScreen.class);
		startActivity(intent);
	}
}
