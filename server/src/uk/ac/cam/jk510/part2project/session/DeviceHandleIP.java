package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;

public class DeviceHandleIP extends DeviceHandle {
	
	private static final long serialVersionUID = 8747021412699803248L;
	private InetAddress ip;
	
	public DeviceHandleIP(InetAddress a) {
		ip = a;
	}
	
	public InetAddress getIP() {
		return ip;
	}

}
