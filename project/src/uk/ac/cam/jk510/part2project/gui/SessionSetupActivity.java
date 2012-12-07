package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.R.layout;
import uk.ac.cam.jk510.part2project.R.menu;
import uk.ac.cam.jk510.part2project.session.SessionManagerBluetooth;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SessionSetupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_setup);
        SessionManagerBluetooth.setUpBluetooth(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_session_setup, menu);
        return true;
    }
}
