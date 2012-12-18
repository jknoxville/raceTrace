package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.HistoryType;



public class ProtocolManagerSingleUser extends ProtocolManager {

	public ProtocolManagerSingleUser(Session session) {
		this.session = session;
	}

	@Override
	protected void giveToNetwork(Device device, Coords coords) {
		// Do nothing because no network
		
	}

}
