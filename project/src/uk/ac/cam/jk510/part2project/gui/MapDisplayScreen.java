package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.map.MapDrawer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

public class MapDisplayScreen extends Activity {
	MapDrawer mapDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_map_display_screen);
        
        mapDrawer = new MapDrawer(this);
        mapDrawer.setBackgroundColor(Color.WHITE);
        setContentView(mapDrawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map_display_screen, menu);
        return true;
    }
    
}
