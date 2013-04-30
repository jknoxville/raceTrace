package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

//Session Manager Bluetooth - Master or slave selection screen
public class SMBTserverorclient extends Activity {

	static final int GET_PLAYER_LIST = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smbtserverorclient);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_smbtserverorclient, menu);
		return true;
	}

	public void beServer(View view) {
		Intent toSetupSession = new Intent(this, NewSessionActivityBluetoothMaster.class);
		startActivityForResult(toSetupSession, GET_PLAYER_LIST);

	}

	public void beClient(View view) {
		Intent toSetupSession = new Intent(this, NewSessionActivityBluetoothSlave.class);
		startActivity(toSetupSession);
	}
}
