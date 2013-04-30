package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;

public class Session {

	private ArrayList<Device> devices;
	private Keys keys;
	private int meNumber = -1;
	private int deviceCount = 0;

	protected Session(ArrayList<Device> devices, Keys keys) {
		super();
		this.devices = devices;
		this.keys = keys;
		System.out.println("Starting "+devices.size()+" device session");
		for(Device d: devices) {
			System.out.println(d.getName()+" and "+Config.getName());
			//TODO use bluetooth MAC address instead of name.
			if(d.getName().equals(Config.getName())) {
				meNumber = deviceCount;
				System.out.println("I am device "+meNumber+" / "+devices.size());
				break;
			}
			
			deviceCount++;
		}
		//TODO make Config.name read name from some preferences (see android tutorials)
		//TODO have check when setting up session to see if names clash.
	}
	
	public Device getDevice(int n) {
		return devices.get(n);
	}

	public String[] getDeviceNames() {
		String[] names = new String[devices.size()];
		for(Device d: devices) {
			names[devices.indexOf(d)] = d.getName();
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

	public int getIndex(Device device) {
		return devices.indexOf(device);
	}

}
