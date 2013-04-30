package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.session.SessionManagerQuickstart;
import android.os.Bundle;
import android.view.Menu;

public class NewSessionActivitySingleSession extends NewSessionActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session_activity_single_session);
		SessionManagerQuickstart.spawnSetupThread(findViewById(R.id.stringy), this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_session_activity_single_session, menu);
		return true;
	}

	@Override
	protected void setUpSession() throws Exception {
	}

}
