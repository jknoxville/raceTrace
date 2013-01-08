package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.widget.TextView;

import uk.ac.cam.jk510.part2project.gui.MapDisplayScreen;
import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public abstract class ProtocolManager {

	private static ProtocolManager instance;
	private static boolean alive = true;
	public static TextView debugInfo;

	public static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
		if(instance == null) {
			instance = newProtocolManager();
			instance.spawnReceivingThread();
		}
		return instance;
	}

	//TODO when sending to server, must attach the device number thats sending it so server knows which its coming from, not just which it's about. (e.g. for swicthing port numbers)

	public static void testInputData() {
		//		for (int dev=0; dev<Session.getSession().numDevices(); dev++) {
		//			testInputData(dev);
		//		}
		new Thread(new Runnable() {
			public void run() {
				int thisDeviceNumber = Session.getThisDevice().getDeviceID();
				testInputData(thisDeviceNumber);	//changed from above as needed
			}
		}).start();

	}


	protected void sendCoordsToAddress(final InetSocketAddress toSocketAddress, Device aboutDevice, Coords coords) {

		System.out.println("sending to "+toSocketAddress.getAddress().getHostAddress()+":"+toSocketAddress.getPort());
		if(debugInfo != null) {
			if(MapDisplayScreen.instance != null) {
				if(MapDisplayScreen.instance.mapDrawer != null) {
					MapDisplayScreen.instance.mapDrawer.post(new Runnable() {
						public void run() {
							debugInfo.setText("sending to "+toSocketAddress.getAddress().getHostAddress()+":"+toSocketAddress.getPort());
						}
					});
				}
			}

		}
		int fromDeviceID = Session.getThisDevice().getDeviceID();	//used to identify sender to the recipent.
		int aboutDeviceID = aboutDevice.getDeviceID();	//deviceID of the device whose location this point is.

		int lClock = coords.getLClock();
		float x = coords.getCoord(0);
		float y = coords.getCoord(1);
		float alt = coords.getCoord(2);
		byte[] data = new byte[5*5];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.putInt(fromDeviceID);	//	TODO this is added, update all recipents so it doesnt shift everything wrongly
		bb.putInt(aboutDeviceID);	//TODO These two are to go at the start of each packet, not each coordinate (if >1 coord per packet)
		bb.putInt(lClock);
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(alt);
		System.out.println("sending. device "+aboutDeviceID+" lClock "+lClock+" x "+x+" y "+y+" alt "+alt);
		try {
			//checkInit();
			DatagramPacket datagram = new DatagramPacket(data, data.length, toSocketAddress);
			DataConnectionManager.send(datagram);

			if(Config.debugMode()) {
				DatagramPacket datagram2 = new DatagramPacket(data, data.length, new InetSocketAddress(Config.getServerIP(), Config.getServerPort()));
				DataConnectionManager.send(datagram2);
			}


		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testInputData(int device) {
		//TODO remove the following test data
		//adds some random data for test
		Device deviceObject = Session.getDevice(device);
		for(int i=0; i<1; i++) {
			Coords coords = new CoordsTXYA((int) (Math.random()*100), (int) (Math.random()*100)+712026, (int) (Math.random()*100)+9828785, (int) (Math.random()*100));
			System.err.println("now inserting test index: "+coords.getLClock()+" to device "+device);	//debug
			insertOriginalDataPoint(deviceObject, coords);
			System.err.println("Finished inputting test data");	//debug
		}
	}

	private static ProtocolManager newProtocolManager() throws Exception {
		Proto protocol = Config.getProtocol();
		switch(protocol) {
		case singleUser: instance = new ProtocolManagerSingleUser(); break;
		case clientServer: instance = new ProtocolManagerClientServer(); break;
		case p2p: instance = new ProtocolManagerP2P(); break;
		default: throw new Exception();
		}
		return instance;
	}

	public static ProtocolManager getProtocolManager() {
		return instance;
	}

	/* This method is specifically for original data points.
	 * Distinction made so that on insert, it can also notify the network module. This doesnt have to be done
	 * for network received data points, (un-original ones).
	 */
	public static void insertOriginalDataPoint(final Device device, final Coords coords) {
		//if(decision logic) {	what decision logic?
		//TODO alert network module, maybe subscriber model so it sends it out.
		//should sending decision be made here or there, probably here because its ProtocolManager.
		//}
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				instance.giveToNetwork(device, coords);
			}
		}).start();
		
		//tell Logger
		Logger.generatedPoint(coords.getLClock());

		PositionStore.insert(device, coords);
	}

	public static void insertNetworkDataPoint(Device device, Coords coords) {
		PositionStore.insert(device, coords);
	}

	public abstract void spawnReceivingThread();

	//	@Deprecated
	//	public static MapDrawer initialiseMapDrawer(Context context) throws IllegalAccessException, InstantiationException {
	//		MapDrawer mapDrawer = new MapDrawer(context, session);
	//		mapDrawer.setBackgroundColor(Config.getBackgroundColor());
	//		return mapDrawer;
	//	}

	protected abstract void giveToNetwork(Device device, Coords coords);

	public static void destroy() {
		ProtocolManager.stopReceivingThread();
		if(instance != null) {
			instance.protocolSpecificDestroy();
		}
		instance = null;
	}

	protected static void stopReceivingThread() {
		alive = false;
	}

	protected abstract void protocolSpecificDestroy();

	public abstract void distributeSession(Session session) throws UnknownHostException, IOException;

	public abstract void sendKeepAliveMessage(int index);

}
