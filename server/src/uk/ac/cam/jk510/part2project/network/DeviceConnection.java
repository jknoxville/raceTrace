package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.settings.Config;

public abstract class DeviceConnection {
	
	//public constructor that constructs the correct type of DeviceConnection
	//use null device to create server connection
	public static DeviceConnection newConnection(Device device) throws SocketException {
		switch(Config.transportProtocol()) {
		case UDP: return new UDPConnection(device);
		case TCP: return new TCPConnection(device);
		default: return null;
		}
	}
	
	public abstract ByteBuffer receiveData(byte[] data) throws IOException;

	public void sendGeneric(byte[] data, int length) {
		if(!Config.droppingPackets()) {
			send(data, length);
		}
	}
	
	protected abstract void send(byte[] data, int length);
	
}
