package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.session.SessionManagerBluetooth;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class NewSessionActivityBluetoothSlave extends NewSessionActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_setup_slave);
		
		SessionManagerBluetooth.switchOnBluetooth(this);

		@SuppressWarnings("unused")
		TextView progress = (TextView) findViewById(R.id.slave_setup_progress);
		View thisView = findViewById(android.R.id.content);

		try {
			//start thread to open port and receive data then send data
			SessionManagerBluetooth.spawnSlaveBluetoothSetupThread(thisView, this);
			//at end of spawnSlave.. it posts this.onSetupComplete()
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_session_setup_slave, menu);
		return true;
	}

	@Override
	protected void setUpSession() throws Exception {
	}
	
	protected void onActivityResult(int pRequestCode, int resultCode, Intent data) {
		if(pRequestCode == SessionManagerBluetooth.REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_CANCELED) {
				onBackPressed();
			}
		}
	}
}
