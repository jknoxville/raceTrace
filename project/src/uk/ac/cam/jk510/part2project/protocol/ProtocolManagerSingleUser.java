package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.HistoryType;



public class ProtocolManagerSingleUser extends ProtocolManager {

	public ProtocolManagerSingleUser(Session session) {
		historyType = HistoryType.XYA;
		this.session = session;
	}

}
