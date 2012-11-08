package uk.ac.cam.jk510.part2project.session;

public class Device {

	private String name;
	private int deviceID;
	private DeviceHandle handle;
	
	public Device(String name, int id, DeviceHandle handle) {
		this.name = name;
		this.deviceID = id;
		this.handle = handle;
	}

	public String getName() {
		return name;
	}
	public int getDeviceID() {
		return deviceID;
	}

}
