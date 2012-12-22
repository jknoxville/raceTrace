package uk.ac.cam.jk510.part2project.protocol;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.HistoryType;



public class ProtocolManagerSingleUser extends ProtocolManager {

	public ProtocolManagerSingleUser() {
	}

	@Override
	protected void giveToNetwork(Device device, Coords coords) {
		// Do nothing because no network
		
	}

	@Override
	public void spawnReceivingThread() {
		//do nothing
		
	}

	@Override
	protected void stopReceivingThread() {
		// No receiving thread so nothing needed here
		
	}

	@Override
	protected void protocolSpecificDestroy() {
		// No state to destroy
		
	}

}
