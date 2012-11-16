package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class Session {
	
	private static Session session;
	
	private ArrayList<Device> devices;
	private Keys keys;
	
	protected Session(ArrayList<Device> devices, Keys keys) {
		super();
		this.devices = devices;
		this.keys = keys;
		session = this;
	}
	
	public static Session getSession() {
		assert(session != null);
		return session;
	}
	
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
