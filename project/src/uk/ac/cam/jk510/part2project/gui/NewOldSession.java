package uk.ac.cam.jk510.part2project.gui;


import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.session.SessionEnum;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public void newSession(final View view) throws Exception {
    	final Context context = this;
    	new Thread(new Runnable() {
    		/*
    		 * State left on thread exit:
    		 * some NewSessionActivity has been started.
    		 */
    		public void run() {
    			final Class newSessionActivity;
    			SessionEnum sesh = Config.getSesh();
    			switch(sesh) {
    			case singleUser: newSessionActivity = NewSessionActivitySingleUser.class;
    			break;
    			case bluetooth: newSessionActivity = SMBTserverorclient.class;
    			break;
    			case singleSession: newSessionActivity = NewSessionActivitySingleSession.class;
    			break;
    			default: try {
						throw new Exception();
					} catch (Exception e) {
						// TODO Do something if not an enum value?
						newSessionActivity = null;
						e.printStackTrace();
					}
    			}
    			
    			//get UI thread to advance
    			view.post(new Runnable() {
    				public void run() {
    					Intent intent = new Intent(context, newSessionActivity);
    					startActivityForResult(intent, 1);
    				}
    			});
    		}
    	}).start();

    	
    }
    
    public void loadSession(View view) {
		/*
		 * State left on exit:
		 * NewSessionActivityPredefined has been started.
		 */
    	Intent intent = new Intent(this, NewSessionActivityPredefined.class);
		startActivityForResult(intent, 1);
    }
    
    public void onBackPressed() {
    	finish();	//TODO check this is right. May be the case that calling finish here exits too early sometimes.
    }
    
}
