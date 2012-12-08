package uk.ac.cam.jk510.part2project.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.session.SessionManagerBluetooth;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SessionSetupActivity extends Activity {

	static ListView selectedPlayers;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_setup);
        SessionManagerBluetooth.setUpBluetooth(this);
        selectedPlayers = (ListView) findViewById(R.id.selected_players);
        
        ArrayList<String> pairedList = new ArrayList<String>();
        
        ArrayAdapter<String> listAdapter = new ArrayAdapter(this, R.layout.select_player_row, pairedList);
        selectedPlayers.setAdapter(listAdapter);
        
        populateList(pairedList);	//Add bluetooth names to the list driving the ListView
        
    }
    
    public void onDone(View view) {

    	
    	System.out.println("10th element: "+selectedPlayers.getCheckedItemPositions().get(10));
    	System.out.println("size: "+selectedPlayers.getCheckedItemPositions().size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_session_setup, menu);
        return true;
    }
    
    private void populateList(final ArrayList<String> list) {
    	//run in seperate thread:
    	new Thread(new Runnable() {

			public void run() {
				BluetoothDevice[] devices = SessionManagerBluetooth.getPairedBTDevices();
				for (BluetoothDevice device: devices) {
					list.add(device.getName());
				}
			}
		}).start();
    	
    	
    	
    }
}
