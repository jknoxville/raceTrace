package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.Logger;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;

public abstract class DeviceConnection {
	
	//public constructor that constructs the correct type of DeviceConnection
	//use null device to create server connection
	public static DeviceConnection newConnection(Device device) throws UnknownHostException, IOException {
		switch(Config.transportProtocol()) {
		case UDP: System.err.println("UDP"); return new UDPConnection(device);
		case TCP: System.err.println("TCP"); return new TCPConnection(device);
		default: System.err.println("Unknown transport protocol"); return null;
		}
	}
	
	public ByteBuffer receiveData(byte[] data) throws IOException, DroppedPacketException {
		if(!Config.droppingPackets()) {
			return abstractReceiveData(data);
		}
		else {
			throw new DroppedPacketException();
		}
	}
	
	public abstract ByteBuffer receiveEncryptedData(byte[] data) throws IOException;
	
	public abstract ByteBuffer abstractReceiveData(byte[] data) throws IOException;

	public void sendGeneric(byte[] data, int length) {
		if(!Config.droppingPackets()) {
			try {
				send(data, length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Logger.droppingPacket();
		}
	}
	public static void getConnectable(DeviceConnection[] connections) throws UnknownHostException, IOException {
		switch (Config.transportProtocol()) {
		case UDP: UDPConnection.getConnectable(connections); break;
		case TCP: TCPConnection.getConnectable(connections); break;
		}
	}
	
	protected abstract void sendEncrypted(byte[] data, int length) throws IOException;
	protected abstract void send(byte[] data, int length) throws IOException;
	
}
