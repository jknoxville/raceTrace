package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.cam.jk510.part2project.protocol.Logger;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;

public class Session {

	private static Session session;

	private HashMap<Integer, Device> devices;
	private Keys keys;
	private int meNumber = -1;

	protected Session(ArrayList<Device> devices, Keys keys) {
		super();
		synchronized(this) {
			this.devices = new HashMap<Integer, Device>();
			//make new device list with IDs 1,2,3...
			for(Device d: devices) {
				this.devices.put(d.getDeviceID(), d);
			}
			this.keys = keys;
			int deviceCount = 0;
			for(Device d: this.devices.values()) {
				System.out.println(d.getName()+" and "+Config.getName());
				//TODO use bluetooth MAC address instead of name.
				if(d.getName().equals(Config.getName())) {
					meNumber = d.getDeviceID();
					System.out.println("I am device "+meNumber+" / "+devices.size());
					break;
				}

				deviceCount++;
			}
			//TODO make Config.name read name from some preferences (see android tutorials)
			//TODO have check when setting up session to see if names clash.

			session = this;
			if(Config.loggingEnabled()) {
				new Logger(this);
			}
			System.err.println("just saved Session.session: "+session+" Has "+session.numDevices()+" devices.");	//debug
		}
	}

	public static synchronized Session getSession() {
		assert(session != null);
		return session;
	}

	public synchronized Device getThisDevice() {
		return getDevice(session.meNumber);
	}

	public static synchronized Device getDevice(int n) {
		return session.devices.get(n);
	}

//	public String[] getDeviceNames() {
//		String[] names = new String[devices.size()];
//		for(Device d: devices) {
//			names[d.getDeviceID()] = d.getName();
//		}
//		return names;
//	}

	public synchronized Collection<Device> getDevices() {
		return devices.values();
	}
	public Keys getKeys() {
		return keys;
	}
	public synchronized int numDevices() {
		return devices.size();
	}

	public static synchronized Session reconstructSession(SessionPackage pack) {
		System.out.println("Reconstructing session");	//debug
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
		for(Device d: session.getDevices()) {
			if(d.devicePath != null) {
				System.out.println("devicePath not null");	//debug
			} else {
				System.out.println("devicePath is null");	//debug
			}
		}
		return session;
	}

	public static synchronized void destroy() {
		session = null;
		Device.reset();
	}

	public static synchronized int getIndex(Device device) {
		for(Entry<Integer, Device> e: session.devices.entrySet()) {
			if(e.getValue()==device) {
				return e.getKey();
			}
		}
		//not found
		return -2;
	}

	public static synchronized void updateDevicePort(int device, int newPort) {
		if(session != null) {
			((DeviceHandleIP) session.devices.get(device).getHandle()).setPort(newPort);
		}
	}

}
