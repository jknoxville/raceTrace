package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleUser;
import android.os.Bundle;
import android.view.Menu;

public class NewSessionActivitySingleUser extends NewSessionActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session_activity_single_user);

		setUpSessionThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_session_activity_single_user, menu);
		return true;
	}

	@Override
	protected void setUpSession() throws Exception {
		// Create Session state:
		SessionManager smgr = new SessionManagerSingleUser();
		smgr.newSession(this);
		onSetupComplete();
	}

}
