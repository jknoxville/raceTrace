package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public abstract class ProtocolManager {
	
	private static ProtocolManager mgr;
	protected static Session session;
	
	public static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
			mgr = newProtocolManager(session);
			if(mgr == null) {
			System.err.println("at initialiseProtocolManager mgr is null");	//debug
			} else {
				System.err.println("at initialiseProtocolManager mgr is not null");	//debug
			}
		return mgr;
	}
	
	public static void testInputData() {
		for (int dev=0; dev<session.numDevices(); dev++) {
			testInputData(dev);
		}
	}
	
	public static void testInputData(int device) {
		//TODO remove the following test data
		//adds some random data for test
		Device lastDev = session.getDevice(device);
		for(int i=0; i<1; i++) {
			Coords coords = new CoordsTXYA((int) (Math.random()*100), (int) (Math.random()*100)+712026, (int) (Math.random()*100)+9828785, (int) (Math.random()*100));
			System.err.println("now inserting index: "+coords.getLClock());	//debug
			insertOriginalDataPoint(lastDev, coords);
			System.err.println("Finished inputting test data");	//debug
		}
	}
	
	private static ProtocolManager newProtocolManager(Session sess) throws Exception {
		Proto protocol = Config.getProtocol();
		switch(protocol) {
		case singleUser: mgr = new ProtocolManagerSingleUser(sess);
		break;
		default: throw new Exception();
		}
		return mgr;
	}
	
	public static ProtocolManager getProtocolManager() {
		return mgr;
	}
	
	public static void insertOriginalDataPoint(Device device, Coords coords) {
		//if(decision logic) {
		//TODO alert network module, maybe subscriber model so it sends it out.
		//should sending decision be made here or there, probably here because its ProtocolManager.
		//}
		PositionStore.insert(device, coords);
	}

	public static void spawnSendingThread() {
		
		
	}

//	@Deprecated
//	public static MapDrawer initialiseMapDrawer(Context context) throws IllegalAccessException, InstantiationException {
//		MapDrawer mapDrawer = new MapDrawer(context, session);
//		mapDrawer.setBackgroundColor(Config.getBackgroundColor());
//		return mapDrawer;
//	}

}
