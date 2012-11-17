package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.location.GPSDriver;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MapDisplayScreen extends Activity {
	MapDrawer mapDrawer;
	boolean testDataUsed = false;	//debug

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
		
		mapDrawer = (MapDrawer) findViewById(R.id.mapDrawer);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		TextView info = (TextView) findViewById(R.id.mapScreenInfo);
		GPSDriver.init(locationManager, info);
		
        
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
