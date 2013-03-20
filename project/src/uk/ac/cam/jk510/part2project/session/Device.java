package uk.ac.cam.jk510.part2project.session;

import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.store.DeviceHistory;
import uk.ac.cam.jk510.part2project.store.DevicePath;

public class Device {
	
	private static int deviceCount = 0;

	private String name;
	//private int deviceID;
	private DeviceHandle handle;
	private DeviceHistory history;
	public DevicePath devicePath;	//may be null before it gets set by MapDrawer
	
	public Device(String name, DeviceHandle handle, Protocol protocol) throws IllegalAccessException, InstantiationException {
		this.name = name;
		//this.deviceID = deviceCount;
		
		this.handle = handle;
		//this.history = (DeviceHistory) (protocol.deviceHistoryClass).newInstance();
		this.history = DeviceHistory.newHistory(deviceCount);
		System.out.println("Made device "+name+" ID: "+deviceCount);	//debug
		
		deviceCount++;
	}
	
	public String getName() {
		return name;
	}
	public int getDeviceID() {
		//return deviceID;
		return Session.getIndex(this);
	}
	public DeviceHistory getHistory() {
		return history;
	}
	public DeviceHandle getHandle() {
		return handle;
	}

	public static void reset() {
		deviceCount = 0;
		
	}

	public void setDevicePath(DevicePath dp) {
		devicePath = dp;
	}
	public void nullifyDevicePath() {
		devicePath = null;
	}
	public LinkedList<Integer> getAbsentList() {
		return devicePath.getAbsentList();
	}

}
