package uk.ac.cam.jk510.part2project.session;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.store.DeviceHistory;

public class Device {

	private String name;
	private int deviceID;
	private DeviceHandle handle;
	private DeviceHistory history;
	
	public Device(String name, int id, DeviceHandle handle, Protocol protocol) throws IllegalAccessException, InstantiationException {
		this.name = name;
		this.deviceID = id;
		this.handle = handle;
		this.history = (DeviceHistory) (protocol.deviceHistoryClass).newInstance();
	}

	public String getName() {
		return name;
	}
	public int getDeviceID() {
		return deviceID;
	}
	public DeviceHistory getHistory() {
		return history;
	}

}
