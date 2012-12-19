package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public abstract class ProtocolManager {

	private static ProtocolManager mgr;

	public static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
		mgr = newProtocolManager();
		mgr.spawnReceivingThread();
		return mgr;
	}

	public static void testInputData() {
//		for (int dev=0; dev<Session.getSession().numDevices(); dev++) {
//			testInputData(dev);
//		}
		testInputData(0);	//changed from above as needed
	}

	public static void testInputData(int device) {
		//TODO remove the following test data
		//adds some random data for test
		Device lastDev = Session.getSession().getDevice(device);
		for(int i=0; i<1; i++) {
			Coords coords = new CoordsTXYA((int) (Math.random()*100), (int) (Math.random()*100)+712026, (int) (Math.random()*100)+9828785, (int) (Math.random()*100));
			System.err.println("now inserting test index: "+coords.getLClock()+" to device "+device);	//debug
			insertOriginalDataPoint(lastDev, coords);
			System.err.println("Finished inputting test data");	//debug
		}
	}

	private static ProtocolManager newProtocolManager() throws Exception {
		Proto protocol = Config.getProtocol();
		switch(protocol) {
		case singleUser: mgr = new ProtocolManagerSingleUser(); break;
		case clientServer: mgr = new ProtocolManagerClientServer(); break;
		default: throw new Exception();
		}
		return mgr;
	}

	public static ProtocolManager getProtocolManager() {
		return mgr;
	}

	/* This method is specifically for original data points.
	 * Distinction made so that on insert, it can also notify the network module. This doesnt have to be done
	 * for network received data points, (un-original ones).
	 */
	public static void insertOriginalDataPoint(Device device, Coords coords) {
		//if(decision logic) {	what decision logic?
		//TODO alert network module, maybe subscriber model so it sends it out.
		//should sending decision be made here or there, probably here because its ProtocolManager.
		//}
		mgr.giveToNetwork(device, coords);
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

}
