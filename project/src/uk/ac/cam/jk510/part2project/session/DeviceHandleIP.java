package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;

public class DeviceHandleIP extends DeviceHandle {
	
	private InetAddress ip;
	
	public DeviceHandleIP(InetAddress a) {
		ip = a;
	}

}
