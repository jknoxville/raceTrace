package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.HistoryType;
import android.content.Context;

public abstract class ProtocolManager {
	
	private static ProtocolManager mgr;
	protected static Session session;
	protected static HistoryType historyType;
	
	public static ProtocolManager initialiseProtocolManager(Session session) throws Exception {
			mgr = newProtocolManager(session);
		return mgr;
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
	
	public static HistoryType getHistoryType() {
		return historyType;
	}

}
