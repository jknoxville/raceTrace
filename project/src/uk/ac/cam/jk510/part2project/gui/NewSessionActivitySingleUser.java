package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.protocol.SessionEnum;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleUser;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewSessionActivitySingleUser extends NewSessionActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session_activity_single_user);

		setUpSessionThread(); //this starts a new thread which will set up the session
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_session_activity_single_user, menu);
		return true;
	}
	
	@Override
	public void onSetupComplete(View view) throws Exception {
		
		//advance to next activity
		Intent intent = new Intent(this, MapDisplayScreen.class);
		startActivity(intent);
	}

	@Override
	protected void setUpSession() throws Exception {
		// TODO Auto-generated method stub
		SessionManager smgr = new SessionManagerSingleUser();
		smgr.newSession();
		System.err.println("about to init PM");	//debug
		//TODO make this a new thread
		ProtocolManager.initialiseProtocolManager(smgr.getSession());
		
	}

}
