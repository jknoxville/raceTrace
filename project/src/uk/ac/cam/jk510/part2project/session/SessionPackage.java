package uk.ac.cam.jk510.part2project.session;

import java.io.Serializable;

public class SessionPackage implements Serializable {
	

	private static final long serialVersionUID = 4692646468065788680L;
	public String[] deviceNames;
	public DeviceHandle[] deviceHandles;
	public Keys keys;
	public int sessionID;

	public SessionPackage(Session session, int sessionID) {
		int devices = session.numDevices();
		deviceNames = new String[devices];
		deviceHandles = new DeviceHandle[devices];
		for (int i=0; i<devices; i++) {
			Device d = session.getDevice(i);
			deviceNames[i] = d.getName();
			deviceHandles[i] = d.getHandle();
		}
		keys = session.getKeys();
		this.sessionID = sessionID;
	}

}
