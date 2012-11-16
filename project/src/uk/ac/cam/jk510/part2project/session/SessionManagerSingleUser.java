package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.DataPointPresentException;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public class SessionManagerSingleUser extends SessionManager {

	@Override
	public void newSession() throws IllegalAccessException, InstantiationException {
		ArrayList<Device> devices = new ArrayList<Device>();
		DeviceHandle handle = new DeviceHandleSingleUser();
		Protocol protocol = new ProtocolXYA();
		Device me = new Device(Config.getName(), 0, handle, protocol);
		devices.add(me);
		//Using null for keys since its single user and no comms needed.
		session = new Session(devices, null);

	}

}