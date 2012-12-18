package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;

public class DeviceHandleIP extends DeviceHandle {
	
	private static final long serialVersionUID = 8747021412699803248L;
	private InetAddress ip;
	private int port;
	
	public DeviceHandleIP(InetAddress a, int port) {
		ip = a;
		this.port = port;
	}
	
	public InetAddress getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}

}
