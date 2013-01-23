package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class DeviceHandleIP extends DeviceHandle {

	private static final long serialVersionUID = 8747021412699803248L;
	private InetAddress ip;
	private int port;
	private InetSocketAddress sockAdd;

	public DeviceHandleIP(InetAddress a, int port) {
		ip = a;
		this.port = port;
	}

	public InetAddress getIP() {
		return ip;
	}

	public synchronized int getPort() {
		return port;
	}

	public synchronized void setPort(int newPort) {
		port = newPort;
	}
	//The InetSocketAddress sockAdd should start as null and only be initialised when this is called, this eliminates the need to serialize it for session setup.
	public synchronized InetSocketAddress getSocketAddress() {
		if(sockAdd == null || (sockAdd.getPort() != port || sockAdd.getAddress() != ip)) {
			sockAdd = new InetSocketAddress(ip, port);
		}
		return sockAdd;
	}

}
