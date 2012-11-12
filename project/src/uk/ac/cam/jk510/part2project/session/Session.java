package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class Session {
	
	private Session session;
	
	private ArrayList<Device> devices;
	private Keys keys;
	
	public String[] getDeviceNames() {
		String[] names = new String[devices.size()];
		for(Device d: devices) {
			names[d.getDeviceID()] = d.getName();
		}
		return names;
	}
	
	public ArrayList<Device> getDevices() {
		return devices;
	}
	
}
