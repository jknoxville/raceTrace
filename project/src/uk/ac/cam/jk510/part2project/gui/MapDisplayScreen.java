package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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
        //setContentView(R.layout.activity_map_display_screen);
        
//        mapDrawer = new MapDrawer(this);
//        mapDrawer.setBackgroundColor(Color.WHITE);
        
		//the folling call is now deprecated
		//mapDrawer = ProtocolManager.initialiseMapDrawer(this);
		mapDrawer = (MapDrawer) findViewById(R.id.mapDrawer);
		//ProtocolManager.testInputData();
        setContentView(R.layout.activity_map_display_screen);
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
    		testDataUsed = true;
    	}
    	
    }
    
}
