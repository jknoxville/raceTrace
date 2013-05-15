package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;

public class SessionManagerSingleUser extends SessionManager {

	@Override
	public void newSession(Activity activity) throws IllegalAccessException, InstantiationException {
		ArrayList<Device> devices = new ArrayList<Device>();
		DeviceHandle handle = new DeviceHandleSingleUser();
		Protocol protocol = new ProtocolXYA();
		Device me = new Device(Config.getName(), handle, protocol);
		devices.add(me);
		//Using null for keys since its single user and no comms needed.
		
		//this constructor saves static link to this instance from Session class
		new Session(devices, null, -1);
		System.out.println("1st device: "+Session.getDevice(0));

	}

}
