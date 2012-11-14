package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class NewSessionActivity extends Activity {

	Session session;
	
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
    
    //called when session set-up is successful
    public void onSetupComplete(View view) throws IllegalAccessException, InstantiationException, Exception {
    	Intent intent = new Intent(this, MapDisplayScreen.class);    	
    	startActivity(intent);
    }
    
}
