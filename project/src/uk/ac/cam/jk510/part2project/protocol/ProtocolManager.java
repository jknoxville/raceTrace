package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

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

	protected LinkedList<Coords>[] coordsToSend;	//one linkedlist for each device to send to. Client server so only one.

	private static ProtocolManager instance;
	private static boolean alive = true;
	public static TextView debugInfo;

	public synchronized static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
		if(instance == null) {
			instance = newProtocolManager();
			if(instance instanceof ProtocolManagerClientServer) {
				instance.coordsToSend = new LinkedList[1];
				instance.coordsToSend[0] = new LinkedList<Coords>();
			} else if(instance instanceof ProtocolManagerP2P) {
				instance.coordsToSend = new LinkedList[session.numDevices()];	//TODO make this one size less so none for self.
				System.out.println("Initialising ProtocolManager and thing is "+instance.coordsToSend[0]);	//debug
				for(int device=0; device < session.numDevices(); device++) {
					instance.coordsToSend[device] = new LinkedList<Coords>();
				}
			}
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

	public static void testInputData(int device) {
		//TODO remove the following test data
		//adds some random data for test
		Device deviceObject = Session.getDevice(device);
		for(int i=0; i<1; i++) {
			Coords coords = new CoordsTXYA(device, (int) (Math.random()*100), (int) (Math.random()*100)+712026, (int) (Math.random()*100)+9828785, (int) (Math.random()*100));
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

		PositionStore.insert(device.getDeviceID(), coords);
	}

	public static void insertNetworkDataPoint(int fromDevice, Coords coords) {
		PositionStore.insert(fromDevice, coords);
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
	protected boolean readyToSend(int deviceNumber) {
		//TODO
		if (coordsToSend[deviceNumber].size() >= Config.getMinCoordsPerPacket()) {
			return true;
		}
		return false;
	}
	
	protected LinkedList<Integer>[] getRequestArray() {
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] requests = new LinkedList[Session.getSession().numDevices()];
		for(Device device: Session.getSession().getDevices()) {
			requests[device.getDeviceID()] = device.getAbsentList();
		}
		return requests;
	}
	
	protected abstract void sendMissingRequest();

	protected abstract void protocolSpecificDestroy();

	public abstract void distributeSession(Session session) throws UnknownHostException, IOException;

	public abstract void sendKeepAliveMessage(int index);

}
