package uk.ac.cam.jk510.part2project.session;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.store.DeviceHistory;

public class Device {
	
	private static int deviceCount;

	private String name;
	private int deviceID;
	private DeviceHandle handle;
	private DeviceHistory history;
	
	public Device(String name, DeviceHandle handle, Protocol protocol) throws IllegalAccessException, InstantiationException {
		this.name = name;
		this.deviceID = deviceCount;
		deviceCount++;
		this.handle = handle;
		//this.history = (DeviceHistory) (protocol.deviceHistoryClass).newInstance();
		this.history = DeviceHistory.newHistory();
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
	public DeviceHandle getHandle() {
		return handle;
	}

}
