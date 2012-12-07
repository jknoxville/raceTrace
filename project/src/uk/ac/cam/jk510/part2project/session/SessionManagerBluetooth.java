package uk.ac.cam.jk510.part2project.session;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class SessionManagerBluetooth extends SessionManager {
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
	
	public static void getPairedBTDevices() {
		Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device: bluetoothDevices) {
			device.getName();
			//want to make some sort of adapter so i can do listview to make scrollable list of bluetooth devices.
			//will have checkbox or other in each view in list view.
		}
	}

}
