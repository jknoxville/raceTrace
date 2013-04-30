package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.session.SessionManagerPredefined;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewSessionActivityPredefined extends NewSessionActivity {
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session_activity_predefined);
        
		setUpSessionThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_session_activity_predefined, menu);
        return true;
    }
    
	@Override
	public void onSetupComplete(View view) throws Exception {
		ProtocolManager.initialiseProtocolManager(Session.getSession());
		//advance to next activity
		Intent intent = new Intent(this, MapDisplayScreen.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void setUpSession() throws Exception {
		// Create Session state:
		SessionManager smgr = new SessionManagerPredefined();
		SessionManager.checkIfAlive();
		smgr.newSession(this);
		System.err.println("Finished setUpSession()");	//debug
		
	}
}
