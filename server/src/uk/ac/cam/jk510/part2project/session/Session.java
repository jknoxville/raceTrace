package uk.ac.cam.jk510.part2project.session;

import java.io.Serializable;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;

public class Session {

	private static Session session;
	
	private ArrayList<Device> devices;
	private Keys keys;
	private static Device me;
	//TODO put SocketAddresses here maybe.
	
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
	public Keys getKeys() {
		return keys;
	}
	public int numDevices() {
		return devices.size();
	}
	
	public static Session reconstructSession(SessionPackage pack) {
		int numDevices = pack.deviceNames.length;
		ArrayList<Device> devices = new ArrayList<Device>();
		for(int device = 0; device<numDevices; device++) {
			String name = pack.deviceNames[device];
			DeviceHandle handle = pack.deviceHandles[device];
			try {
				Device dev = new Device(name, handle, new ProtocolXYA());
				devices.add(dev);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Session session = new Session(devices, pack.keys);
		return session;
	}
	
}
