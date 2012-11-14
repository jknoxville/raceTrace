package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MapDisplayScreen extends Activity {
	MapDrawer mapDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map_display_screen);
        
//        mapDrawer = new MapDrawer(this);
//        mapDrawer.setBackgroundColor(Color.WHITE);
        MapDrawer mapDrawer = null;
		try {
			mapDrawer = ProtocolManager.initialiseMapDrawer(this);
			ProtocolManager.testInputData();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setContentView(mapDrawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map_display_screen, menu);
        return true;
    }
    
}
