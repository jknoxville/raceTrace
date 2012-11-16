package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import android.content.Context;

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
		//TODO remove the following test data
		//adds some random data for test
		Device me = session.getDevices().get(0);
		for(int i=0; i<100; i++) {
			Coords coords = new CoordsTXYA(i, (int) (Math.random()*100), (int) (Math.random()*100), (int) (Math.random()*100));
			try {
				PositionStore.insert(me, coords);
			} catch (IncompatibleCoordsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println("Finished inputting test data");
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

	public static MapDrawer initialiseMapDrawer(Context context) throws IllegalAccessException, InstantiationException {
		MapDrawer mapDrawer = new MapDrawer(context, session);
		mapDrawer.setBackgroundColor(Config.getBackgroundColor());
		return mapDrawer;
	}

}
