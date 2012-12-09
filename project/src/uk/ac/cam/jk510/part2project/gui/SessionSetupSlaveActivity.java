package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.R.layout;
import uk.ac.cam.jk510.part2project.R.menu;
import uk.ac.cam.jk510.part2project.session.SessionManagerBluetooth;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class SessionSetupSlaveActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_setup_slave);
        SessionManagerBluetooth.switchOnBluetooth(this);
        
        TextView progress = (TextView) findViewById(R.id.slave_setup_progress);
        
        //start thread to open port and receive data then send data
        SessionManagerBluetooth.spawnSlaveBluetoothSetupThread(progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_session_setup_slave, menu);
        return true;
    }
}
