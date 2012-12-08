package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.widget.ListView;

public class SessionManagerBluetooth extends SessionManager {

	private static final int REQUEST_ENABLE_BT = 1;
	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static ArrayList<BluetoothDevice> deviceList;

	@Override
	public void newSession(Activity activity) throws IllegalAccessException,
	InstantiationException {
		/*
		 * 	x	* Set up bluetooth
		 * 
		 * 	x	* Select between Server and Client
		 * 		* Get list of bluetooth devices in range
		 * 		* Let server user select those that are in session
		 * 		* Clients enable discoverability
		 * 		* Start for loop looping through list
		 * 		* Collect info of each device
		 * 		* Construct package containing all info and keys
		 * 		* Start new for loop sending package to each device
		 * 			-May be done online or using bluetooth
		 */


	}

	public static void setUpBluetooth(Activity activity) {
		if(bluetoothAdapter == null) {
			System.err.println("Bluetoot not supported on device");	//TODO proper error reporting
		}
		if(!bluetoothAdapter.isEnabled()) {
			Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBT, REQUEST_ENABLE_BT);
		}
	}

	public static BluetoothDevice[] getPairedBTDevices() {
		Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
		BluetoothDevice[] deviceArray = new BluetoothDevice[bluetoothDevices.size()];
		int i=0;
		for(BluetoothDevice device: bluetoothDevices) {
			deviceArray[i] = device;
			i++;
		}
		return deviceArray;
	}
	
	public void updateSelection(final ListView selectedPlayers) {
    	//in new thread:
    	//get list of chosen bluetooth devices
    	//update SessionManager with selectedList
    	//post advance to UI thread
    	
    	new Thread(new Runnable() {
			public void run() {
				SparseBooleanArray boolArray = selectedPlayers.getCheckedItemPositions();
				LinkedList<BluetoothDevice> selectedList = new LinkedList<BluetoothDevice>();
				for(int i=0; i<boolArray.size(); i++) {
					if(boolArray.get(i)) {
						selectedList.add()
					}
				}
			}
		}).start();
	}

}
