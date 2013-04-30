package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;

public class UDPConnection extends DeviceConnection {

	UDPConnection(Device device) throws SocketException {
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

		//Do encryption on data here
		DatagramPacket datagram;
		datagram = new DatagramPacket(data, length, socketAddress);
		socket.send(datagram);

	}
	protected void send(byte[] data, int length) throws IOException {

		DatagramPacket datagram;
		datagram = new DatagramPacket(data, length, socketAddress);
		socket.send(datagram);

	}

	//synchronized because only need one at a time
	public synchronized ByteBuffer receiveEncryptedData(byte[] data) throws IOException {
		//encryption placeholder
		return null;
	}
	
	//synchronized because only need one at a time
	public synchronized ByteBuffer abstractReceiveData(byte[] data) throws IOException {
		DatagramPacket datagram = new DatagramPacket(data, data.length);
		socket.receive(datagram);
		//update port of sender
		//30/04 was getting ID=1 from server for some reason, so skipped port update
		//updatePort(datagram);
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.limit(datagram.getLength());
		return bb;
	}
	
	@SuppressWarnings("unused")
	private static void updatePort(DatagramPacket datagram) {
		ByteBuffer.wrap(datagram.getData()).getInt();	//read past the message type and ignore it
		int deviceID = ByteBuffer.wrap(datagram.getData()).getInt();
		System.out.println("deviceID receiving from: "+deviceID);
		if(deviceID != -1) {	//if not from server, update that devices port.
			int newPort = datagram.getPort();
			Session.updateDevicePort(deviceID, newPort);
		}
	}

}
