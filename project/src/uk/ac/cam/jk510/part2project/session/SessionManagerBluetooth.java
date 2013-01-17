package uk.ac.cam.jk510.part2project.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import uk.ac.cam.jk510.part2project.gui.NewSessionActivityBluetoothMaster;
import uk.ac.cam.jk510.part2project.gui.NewSessionActivityBluetoothSlave;
import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;

public class SessionManagerBluetooth extends SessionManager {

	public static final int REQUEST_ENABLE_BT = 1;
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

		//		new Thread(new Runnable() {
		//			public void run() {
		SparseBooleanArray boolArray = selectedPlayers.getCheckedItemPositions();
		selectedList = new LinkedList<BluetoothDevice>();
		//Add all devices from deviceList that are ticked, to selectedList
		for(int i=0; i<boolArray.size(); i++) {
			if(boolArray.get(boolArray.keyAt(i))) {
				selectedList.add(deviceList.get(boolArray.keyAt(i)));
			}
		}
		System.out.println("after making selectedList, size = "+selectedList.size());	//debug
		//			}
		//		}).start();
	}

	public static void spawnMasterBluetoothSetupThread(final View view, final NewSessionActivityBluetoothMaster activity) {

		SessionManager.setAlive();

		//run in seperate thread:
		new Thread(new Runnable() {

			public void run() {
				/* 		* Start for loop looping through list
				 * 		* Collect info of each device
				 * 		* Construct package containing all info and keys
				 * 		* Start new for loop sending package to each device
				 * 			-May be done online or using bluetooth
				 */

				try {

					System.out.println(selectedList.size());
					checkIfAlive();
					ArrayList<Device> devices = new ArrayList<Device>();
					checkIfAlive();
					Keys keys = null;	//TODO make actual keys

					Config.setName(bluetoothAdapter.getName());	//TODO unstable. this uses current name. Master creates session with name at pair time.
					checkIfAlive();
					//first add master to devices
					{
						try {
							Device device = new Device(Config.getName(), new DeviceHandleIP(InetAddress.getByName(DataConnectionManager.getMyIP()), Config.getDefaultClientPort()), new ProtocolXYA());
							devices.add(device);
							System.out.println("Added master device");	//debug
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}


					for(BluetoothDevice bluetoothDevice: selectedList) {
						try {
							checkIfAlive();
							bluetoothAdapter.cancelDiscovery();	//to speed up connection

							Config.setName(bluetoothAdapter.getName());	//TODO unstable. this uses current name. Master creates session with name at pair time.

							String ip = DataConnectionManager.getMyIP();
							System.out.println("My ip address: "+ip);	//debug
							checkIfAlive();
							BluetoothSocket sock = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Config.getUUIDString()));

							sock.connect();
							checkIfAlive();
							System.out.println("connected to "+bluetoothDevice.getName());	//debug

							//Send Master info, and then request slave's info
							OutputStream outputStream = sock.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(outputStream);
							checkIfAlive();
							sendMyAddressInfo(oos);
							checkIfAlive();

							System.out.println("sent my (master) address info");	//debug

							InputStream inputStream = sock.getInputStream();
							ObjectInputStream ois = new ObjectInputStream(inputStream);
							checkIfAlive();

							try{
								System.out.println("waiting for device info");	//debug
								String name = (String) ois.readObject();
								checkIfAlive();
								String address = (String) ois.readObject();
								checkIfAlive();
								Device device = new Device(name, new DeviceHandleIP(InetAddress.getByName(address), Config.getDefaultClientPort()), new ProtocolXYA());
								devices.add(device);
								checkIfAlive();
								System.out.println("Made device "+name);	//debug
							} catch (Exception e) {
								e.printStackTrace();
							}
							//then construct session object.
							//TODO then send this out to all devices


							sock.close();
						} catch (IOException e) {
							System.out.println("Error connecting to "+bluetoothDevice.getName());
							e.printStackTrace();
							continue;
						}
					}
					Session session = null;
					try{
						if(selectedList.size()==0) {
							//							(new SessionManagerSingleUser()).newSession(null);

							session = new Session(devices, keys);
						} else {
							session = new Session(devices, keys);	//now for master, session setup is complete
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					checkIfAlive();
					SessionPackage pack = new SessionPackage(session);

					//TODO the following sending of session isnt necessary in client server model, server could send them the session. So should be moved to protocolmanager.
					for(BluetoothDevice bluetoothDevice: selectedList) {
						checkIfAlive();

						BluetoothSocket sock;
						try {
							sock = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(Config.getUUIDString()));
							try {
								sock.connect();
							} catch (IOException e) {
								sock.connect();	//TODO something better. retries the thing.
							}

							checkIfAlive();

							//Send package to each device
							OutputStream outputStream = sock.getOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(outputStream);
							System.out.println("sending session to "+bluetoothDevice.getName());	//debug
							checkIfAlive();

							oos.writeObject(pack);

							checkIfAlive();
							System.out.println("sent");	//debug
							try {
								Thread.sleep(1000);		//debug to stop socket from closing too early for reciever to receieve.
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	//
							sock.close();

							checkIfAlive();


						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					try {
						ProtocolManager.initialiseProtocolManager(session);
						ProtocolManager pm = ProtocolManager.getProtocolManager();
						pm.distributeSession(session);	//TODO do this in another thread at the same time as the above thing.
						checkIfAlive();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					//get UI thread to call onSetupComplete()
					view.post(new Runnable() {

						public void run() {

							try {
								checkIfAlive();
								activity.onSetupComplete();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});
				} catch (StopThreadException e) {
					//do nothing ending thread
				}
			}
		}).start();
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

	public static void listenForMaster() {

	}

	public static void spawnSlaveBluetoothSetupThread(final View view, final NewSessionActivityBluetoothSlave activity) {
		new Thread(new Runnable() {
			public void run() {
				try {
					bluetoothAdapter.cancelDiscovery();	//to speed up connection

					checkIfAlive();
					String ip = DataConnectionManager.getMyIP();
					System.out.println("My ip address: "+ip);	//debug
					checkIfAlive();
					Config.setName(bluetoothAdapter.getName());	//TODO unstable. this uses current name. Master creates session with name at pair time.

					BluetoothServerSocket serverSock = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Config.getName(), UUID.fromString(Config.getUUIDString()));
					checkIfAlive();

					BluetoothSocket sock = serverSock.accept();
					checkIfAlive();

					System.out.println("connected to master");	//debug
					//Receive Master info, and then send slave's info
					InputStream inputStream = sock.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(inputStream);
					checkIfAlive();
					String masterName = (String) ois.readObject();
					checkIfAlive();
					String masterAddress = (String) ois.readObject();
					checkIfAlive();
					//tv.setText("master's name: "+masterName);

					System.out.println("got master info");	//debug
					OutputStream outputStream = sock.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(outputStream);
					checkIfAlive();
					sendMyAddressInfo(oos);
					checkIfAlive();
					//close connection while master fetches data from the other devices
					sock.close();
					checkIfAlive();

					System.out.println("sent my info");	//debug
					System.out.println("waiting for session from master...");	//debug

					//open new connection
					sock = serverSock.accept();
					checkIfAlive();

					//wait for package
					inputStream = sock.getInputStream();	//get new InputStream
					ois = new ObjectInputStream(inputStream);
					checkIfAlive();
					SessionPackage pack = (SessionPackage) ois.readObject();		//TODO socket closed here.
					checkIfAlive();
					Session.reconstructSession(pack); //construct and save session object from recieved object.
					checkIfAlive();

					//get UI thread to call onSetupComplete()
					view.post(new Runnable() {

						public void run() {

							try {
								activity.onSetupComplete();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StopThreadException e) {
					//end thread
				}

			}
		}).start();
	}

}
