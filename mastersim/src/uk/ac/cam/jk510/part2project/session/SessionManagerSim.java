package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class SessionManagerSim extends SessionManager {

	@Override
	public void newSession() throws IllegalAccessException,
	InstantiationException {

		int numDevices = 5;	//TODO read from command line? or something...
		ArrayList<Device> devices = new ArrayList<Device>();

		
		for(int i = 0; i<numDevices; i++) {
			DeviceHandle handle = new DeviceHandleIP();	//TODO change to deviceHandleSim? or IP?
			Device dev = new Device(Integer.toString(i), handle);
			devices.add(dev);
			//Using null for keys since its single user and no comms needed.
		}

		//this constructor saves static link to this instance from Session class
		new Session(devices, null);

	}

}
