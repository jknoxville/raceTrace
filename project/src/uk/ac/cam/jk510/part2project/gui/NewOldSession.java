package uk.ac.cam.jk510.part2project.gui;


import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.SessionEnum;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewOldSession extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_old_session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_old_session, menu);
        return true;
    }
    
    //go to set up session screen
    public void newSession(View view) throws Exception {
    	Class newSessionActivity;
    	SessionEnum sesh = Config.getSesh();
    	switch(sesh) {
    		case singleUser: newSessionActivity = NewSessionActivitySingleUser.class;
    		break;
    		default: throw new Exception();
    	}
    	Intent intent = new Intent(this, newSessionActivity);
    	startActivity(intent);
    }
}
