package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;

public class UDPConnection extends DeviceConnection {

	UDPConnection(Device device) throws SocketException {
		System.out.println("Making connection");
		if(device == null) {
			socketAddress = new InetSocketAddress(Config.getServerIP(), Config.getServerPort());	//server
		} else {
			socketAddress = ((DeviceHandleIP) device.getHandle()).getSocketAddress();
		}
		if(socket == null) {
			socket = new DatagramSocket(Config.getDefaultClientPort());
		}
	}

	private SocketAddress socketAddress;
	private static DatagramSocket socket;	//used for all UDPConnection objects

	protected void sendEncrypted(byte[] data, int length) throws IOException {

		//TODO encrypt data
		
		
		DatagramPacket datagram;
		datagram = new DatagramPacket(data, length, socketAddress);
		socket.send(datagram);

		//TODO the following should probably not be here
		if(Config.getProtocol() == Proto.p2p && Config.debugMode()) {
			//TODO if desired, serverConnection.send(data, length);
		}

	}
	protected void send(byte[] data, int length) throws IOException {

		DatagramPacket datagram;
		datagram = new DatagramPacket(data, length, socketAddress);
		socket.send(datagram);

		//TODO the following should probably not be here
		if(Config.getProtocol() == Proto.p2p && Config.debugMode()) {
			//TODO if desired, serverConnection.send(data, length);
		}

	}

	//synchronized because only need one at a time
	public synchronized ByteBuffer receiveEncryptedData(byte[] data) throws IOException {
		//TODO
		return null;
	}
	//synchronized because only need one at a time
	public synchronized ByteBuffer receiveData(byte[] data) throws IOException {
		DatagramPacket datagram = new DatagramPacket(data, data.length);
		socket.receive(datagram);
		//TODO update port of sender
		updatePort(datagram);
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.limit(datagram.getLength());
		return bb;
	}
	private static void updatePort(DatagramPacket datagram) {
		ByteBuffer.wrap(datagram.getData()).getInt();
		int deviceID = ByteBuffer.wrap(datagram.getData()).getInt();
		if(deviceID != -1) {	//if not from server, update that devices port.
			((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device. Same for address useful?
		}
	}

}
