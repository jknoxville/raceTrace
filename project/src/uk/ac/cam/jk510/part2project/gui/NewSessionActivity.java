package uk.ac.cam.jk510.part2project.gui;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import uk.ac.cam.jk510.part2project.R;

public class NewSessionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_session, menu);
        return true;
    }
}
