package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.HistoryType;

public class ProtocolManagerSingleUser extends ProtocolManager {

	public ProtocolManagerSingleUser() {
	}

	@Override
	protected void giveToNetwork(Coords coords) {
		// Do nothing because no network
		
	}

	@Override
	public void spawnReceivingThread() {
		//do nothing
		
	}

	@Override
	protected void protocolSpecificDestroy() {
		// No state to destroy
		
	}

	@Override
	public void distributeSession(Session session) throws UnknownHostException,
			IOException {
		// Nowhere to distribute to.
		
	}

	@Override
	public void sendKeepAliveMessage(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendMissingRequest() {
		//no-one to send to.
	}

	@Override
	protected List<Device> requestablePeers() {
		return null;
	}

	@Override
	protected void respondToNetwork(int requester, List<Coords> response) {
		// nothing to send to
		
	}

	@Override
	protected List<Device> relientPeers() {
		//has no peers
		return null;
	}

	@Override
	protected void flushToNetwork(int device) {
		// nothing to do
	}

	@Override
	protected void connectToPeers() {
		// no sockets to initialise
		
	}

}
