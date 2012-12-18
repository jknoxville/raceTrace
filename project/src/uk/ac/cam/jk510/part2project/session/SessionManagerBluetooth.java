package uk.ac.cam.jk510.part2project.session;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import android.widget.TextView;

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

	public static void switchOnBluetooth(Activity activity) {
		if(bluetoothAdapter == null) {
			System.err.println("Bluetoot not supported on device");	//TODO proper error reporting
		}
		if(!bluetoothAdapter.isEnabled()) {
			Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBT, REQUEST_ENABLE_BT);
		}
	}

	public static void populateList(final ArrayList<String> list) {
		// TODO run in seperate thread?

		Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
		deviceList = new ArrayList<BluetoothDevice>();
		//iterate through all paired devices, adding them to a linkedlist, also add their name to list.
		for(BluetoothDevice device: devices) {
			deviceList.add(device);
			list.add(device.getName());
		}
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

	public static void spawnMasterBluetoothSetupThread() {
		//run in seperate thread:
		new Thread(new Runnable() {

			public void run() {
				/* 		* Start for loop looping through list
				 * 		* Collect info of each device
				 * 		* Construct package containing all info and keys
				 * 		* Start new for loop sending package to each device
				 * 			-May be done online or using bluetooth
				 */

				ArrayList<Device> devices = new ArrayList<Device>();
				Keys keys = null;	//TODO make actual keys

				for(BluetoothDevice bluetoothDevice: selectedList) {
					try {
						bluetoothAdapter.cancelDiscovery();	//to speed up connection


						String ip = DataConnectionManager.getMyIP();
						System.out.println("My ip address: "+ip);	//debug

						BluetoothSocket sock = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Config.getUUIDString()));
						sock.connect();

						//Send Master info, and then request slave's info
						OutputStream outputStream = sock.getOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(outputStream);
						sendMyAddressInfo(oos);

						InputStream inputStream = sock.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(inputStream);
						try{
							String name = (String) ois.readObject();
							String address = (String) ois.readObject();
							Device device = new Device(name, new DeviceHandleIP(InetAddress.getByName(address), Config.getDefaultClientPort()), new ProtocolXYA());
							devices.add(device);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//then construct session object.
						//TODO then send this out to all devices


						sock.close();
					} catch (IOException e) {
						System.out.println("Error connecting to "+bluetoothDevice.getName());
						continue;
					}
				}
				Session session = new Session(devices, keys);	//now for master, session setup is complete
				SessionPackage pack = new SessionPackage(session);
				for(BluetoothDevice bluetoothDevice: selectedList) {

					BluetoothSocket sock;
					try {
						sock = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Config.getUUIDString()));
						sock.connect();

						//Send package to each device
						OutputStream outputStream = sock.getOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(outputStream);
						oos.writeObject(pack);

						sock.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Deprecated
	private static void sendMyAddressInfo2(OutputStream os) {
		/*send:
		 * name
		 * ip address
		 */
		try {
			String name = Config.getName();
			byte[] nameData = name.getBytes("UTF-16LE");
			String ip = DataConnectionManager.getMyIP();

			System.out.println("My ip address: "+ip);	//debug

			byte[] ipData = ip.getBytes("UTF-16LE");

			for(int i=0; i<nameData.length; i++) {
				if(nameData[i]==Byte.MAX_VALUE) {
					System.out.println("Warning, incorrect encoding in SessionManagerBluetooth");
				}
			}
			for(int i=0; i<ipData.length; i++) {
				if(ipData[i]==Byte.MAX_VALUE) {
					System.out.println("Warning, incorrect encoding in SessionManagerBluetooth");
				}
			}
			byte[] data = new byte[nameData.length+ipData.length+1];
			System.arraycopy(nameData, 0, data, 0, nameData.length);
			data[nameData.length] = Byte.MAX_VALUE;	//seperator between values
			System.arraycopy(ipData, 0, data, nameData.length+1, ipData.length);
			System.out.println(ip);
			os.write(nameData);
		} catch (Exception e) {
			System.out.println("Exception occured");
		}
	}

	private static void sendMyAddressInfo(ObjectOutputStream os) {
		/*send:
		 * name
		 * ip address
		 */
		try{
			os.writeObject(Config.getName());
			os.writeObject(DataConnectionManager.getMyIP());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Deprecated
	private static String receiveAddressInfo(InputStream is) {

		//TODO overflow size etc
		byte[] buffer = new byte[100];
		try {
			is.read(buffer);
			String string = new String(buffer, "UTF-16LE");
			System.out.println(string);
			return string;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}

	}

	public static void listenForMaster() {

	}

	public static void spawnSlaveBluetoothSetupThread(final TextView tv) {
		new Thread(new Runnable() {
			public void run() {
				try {
					bluetoothAdapter.cancelDiscovery();	//to speed up connection

					String ip = DataConnectionManager.getMyIP();
					System.out.println("My ip address: "+ip);	//debug

					BluetoothServerSocket serverSock = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Config.getName(), UUID.fromString(Config.getUUIDString()));
					BluetoothSocket sock = serverSock.accept();

					//Receive Master info, and then send slave's info
					InputStream inputStream = sock.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(inputStream);
					String masterName = (String) ois.readObject();
					String masterAddress = (String) ois.readObject();
					tv.setText("master's name: "+masterName);

					OutputStream outputStream = sock.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(outputStream);
					sendMyAddressInfo(oos);
					//close connection while master fetches data from the other devices
					sock.close();

					//open new connection
					sock = serverSock.accept();
					//wait for package
					inputStream = sock.getInputStream();	//get new InputStream
					ois = new ObjectInputStream(inputStream);
					SessionPackage pack = (SessionPackage) ois.readObject();
					Session.reconstructSession(pack); //construct and save session object from recieved object.


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	@Deprecated
	private static void awaitPackage(InputStream in) throws IOException {
		DataInputStream din = new DataInputStream(in);
		int length;
		byte[] buffer = new byte[1000];
		int offset = 0;
		String name;
		String ipAddress;
		while((name = din.readUTF()) != null) {
			ipAddress = din.readUTF();


		}

		//construct session object.
		new Session(null, null);
	}

}
