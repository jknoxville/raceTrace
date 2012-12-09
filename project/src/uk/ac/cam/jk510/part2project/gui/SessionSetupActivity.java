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
import android.widget.TextView;

public class SessionSetupActivity extends Activity {

	static ListView selectedPlayers;
	static TextView progressList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_setup);
        SessionManagerBluetooth.switchOnBluetooth(this);
        
        selectedPlayers = (ListView) findViewById(R.id.selected_players);
        ArrayList<String> pairedList = new ArrayList<String>();
        
        SessionManagerBluetooth.populateList(pairedList);	//Add bluetooth names to the list driving the ListView
        
        ArrayAdapter<String> listAdapter = new ArrayAdapter(this, R.layout.select_player_row, pairedList);
        selectedPlayers.setAdapter(listAdapter);
    }
    
    public void onDone(View view) {

    	SessionManagerBluetooth.updateSelection(selectedPlayers);
    	
    	setContentView(R.layout.bluetooth_progress_layout);
    	//destroy previous lists / adapters
    	
    	progressList = (TextView) findViewById(R.id.progressList);
    	SessionManagerBluetooth.spawnMasterBluetoothSetupThread();
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_session_setup, menu);
        return true;
    }
}
