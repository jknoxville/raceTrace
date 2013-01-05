package uk.ac.cam.jk510.part2project.gui;

import java.util.Currency;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.R.layout;
import uk.ac.cam.jk510.part2project.R.menu;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleSession;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class NewSessionActivitySingleSession extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_session_activity_single_session);


		SessionManagerSingleSession.spawnSetupThread(findViewById(R.id.stringy));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_session_activity_single_session, menu);
		return true;
	}

}
