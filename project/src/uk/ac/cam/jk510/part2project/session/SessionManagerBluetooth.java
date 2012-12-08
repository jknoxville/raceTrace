package uk.ac.cam.jk510.part2project.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.widget.ListView;

public class SessionManagerBluetooth extends SessionManager {

	private static final int REQUEST_ENABLE_BT = 1;
	private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static ArrayList<BluetoothDevice> deviceList;
	private static LinkedList<BluetoothDevice> selectedList;

	@Override
	public void newSession(Activity activity) throws IllegalAccessException,
	InstantiationException {
		/*
		 * 	x	* Set up bluetooth
		 * 
		 * 	x	* Select between Master and Slave
		 * 		* Get list of bluetooth devices in range
		 * 		* Let master user select those that are in session
		 * 		* slaves open bluetooth port as server
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

	public static void populateList(final ArrayList<String> list) {
		//run in seperate thread:
		new Thread(new Runnable() {

			public void run() {

				Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
				deviceList = new ArrayList<BluetoothDevice>();
				//iterate through all paired devices, adding them to a linkedlist, also add their name to list.
				for(BluetoothDevice device: devices) {
					deviceList.add(device);
					list.add(device.getName());
				}
			}
		}).start();
	}

	public static ArrayList<BluetoothDevice> getPairedBTDevices() {
		Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
		deviceList = new ArrayList<BluetoothDevice>();
		for(BluetoothDevice device: bluetoothDevices) {
			deviceList.add(device);
		}
		return deviceList;
	}

	public static void updateSelection(final ListView selectedPlayers) {
		//in new thread:
		//get list of chosen bluetooth devices
		//update SessionManager with selectedList
		//post advance to UI thread

		new Thread(new Runnable() {
			public void run() {
				SparseBooleanArray boolArray = selectedPlayers.getCheckedItemPositions();
				selectedList = new LinkedList<BluetoothDevice>();
				//Add all devices from deviceList that are ticked, to selectedList
				for(int i=0; i<boolArray.size(); i++) {
					if(boolArray.get(i)) {
						selectedList.add(deviceList.get(i));
					}
				}
			}
		}).start();
	}

	public static void spawnBluetoothSetupThread() {
		//run in seperate thread:
		new Thread(new Runnable() {

			public void run() {
				/* 		* Start for loop looping through list
				 * 		* Collect info of each device
				 * 		* Construct package containing all info and keys
				 * 		* Start new for loop sending package to each device
				 * 			-May be done online or using bluetooth
				 */

				for(BluetoothDevice device: selectedList) {
					try {
						bluetoothAdapter.cancelDiscovery();	//to speed up connection

						System.out.println(UUID.randomUUID().toString());
						BluetoothSocket sock = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID.randomUUID().toString()));
						sock.connect();

						//Send Master info, and then request slave's info
						OutputStream outputStream = sock.getOutputStream();
						sendMyAddressInfo(outputStream);
						
						InputStream inputStream = sock.getInputStream();
						receiveAddressInfo(inputStream);

						sock.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private static void sendMyAddressInfo(OutputStream os) {
		
	}
	
	private static void receiveAddressInfo(InputStream is) {
		
	}

}
