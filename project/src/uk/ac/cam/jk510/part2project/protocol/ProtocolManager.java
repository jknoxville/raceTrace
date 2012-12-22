package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public abstract class ProtocolManager {

	private static ProtocolManager instance;

	public static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
		instance = newProtocolManager();
		instance.spawnReceivingThread();
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
		instance.stopReceivingThread();
		instance.protocolSpecificDestroy();
		instance = null;
	}
	
	protected abstract void stopReceivingThread();
	
	protected abstract void protocolSpecificDestroy();

}
