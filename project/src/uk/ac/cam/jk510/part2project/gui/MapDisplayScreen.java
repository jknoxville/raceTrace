package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.location.GPSDriver;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MapDisplayScreen extends Activity {
	MapDrawer mapDrawer;
	boolean testDataUsed = false;	//debug
	static MapDisplayScreen instance;
	static NewSessionActivity sessionActivity;

//	//Commented out 13.53 friday
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_map_display_screen);
//        
////        mapDrawer = new MapDrawer(this);
////        mapDrawer.setBackgroundColor(Color.WHITE);
//        MapDrawer mapDrawer = null;
//		try {
//			mapDrawer = ProtocolManager.initialiseMapDrawer(this);
//			//ProtocolManager.testInputData();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        setContentView(mapDrawer);
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_map_display_screen);	
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		TextView info = (TextView) findViewById(R.id.mapScreenInfo);
		System.out.println(info);
		GPSDriver.init(locationManager, info);	//TODO do in seperate thread?
		instance = this;
        
    }
    
    //if back is pressed, skip back to the new / old session screen (main menu)
    @Override
    public void onBackPressed() {
    	new AlertDialog.Builder(this)
    	.setTitle("Quit Session?")
    	.setMessage("Are you sure you want to quit the current session?")
    	.setNegativeButton(android.R.string.no, null)
    	.setPositiveButton(android.R.string.yes, new OnClickListener() {
    		public void onClick(DialogInterface di, int arg) {
    			//MapDisplayScreen.super.onBackPressed();
    			//send intent to main menu activity with FLAG_ACTIVITY_CLEAR_TOP set to clear stack
    			//startActivity(new Intent(instance, NewOldSession.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    			instance.finish();
    			NewSessionActivity.newSessionActivity.finish();
    		}
    	}).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map_display_screen, menu);
        return true;
    }
    
    public void addTestData(View view) {
    	if(testDataUsed) {
    		//do nothing
    	} else {
    		ProtocolManager.testInputData();
    		//testDataUsed = true;
    	}
    	
    }
    
}
