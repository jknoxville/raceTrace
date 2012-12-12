package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class Session {
	
	private static Session session;
	
	private ArrayList<Device> devices;
	private Keys keys;
	private static Device me;
	
	protected Session(ArrayList<Device> devices, Keys keys) {
		super();
		this.devices = devices;
		this.keys = keys;
		me = devices.get(0);
		
		session = this;
		System.err.println("just saved Session.session: "+session);	//debug
	}
	
	public static Session getSession() {
		assert(session != null);
		return session;
	}
	
	public static Device getThisDevice() {
		return me;
	}
	
	public Device getDevice(int n) {
		return devices.get(n);
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
	public int numDevices() {
		return devices.size();
	}
	
}
