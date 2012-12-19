package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.Message;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerClientServer extends ProtocolManager {

	private DatagramSocket socket;
	private SocketAddress serverSocketAddress;

	@Override
	protected void giveToNetwork(Device device, Coords coords) {
		checkInit();
		sendCoordsToServer(device, coords);

	}

	@Override
	public void spawnReceivingThread() {
		checkInit();
		new Thread(new Runnable() {
			public void run() {
				byte[] receivingData = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
				while(true) {
					try {
						socket.receive(datagram);
						System.out.println("Recieved datagram");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message.processDatagram(datagram);
				}
			}
		}).start();
	}

	//this method was based on Server -> NetworkInterface.sendCoordsToDevice
	private void sendCoordsToServer(Device fromDevice, Coords coords) {
		int deviceID = fromDevice.getDeviceID();
		int lClock = coords.getLClock();
		float x = coords.getCoord(0);
		float y = coords.getCoord(1);
		float alt = coords.getCoord(2);
		byte[] data = new byte[5*4];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.putInt(deviceID);	//TODO This is to go at the start of each packet, not each coordinate (if >1 coord per packet)
		bb.putInt(lClock);
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(alt);
		System.out.println("sending. device "+deviceID+" lClock "+lClock+" x "+x+" y "+y+" alt "+alt);
		try {
			checkInit();
			DatagramPacket datagram = new DatagramPacket(data, data.length, serverSocketAddress);
			socket.send(datagram);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkInit() {
		if(socket == null) {
			socket = DataConnectionManager.getDataSocket();
			serverSocketAddress = new InetSocketAddress(Config.getServerIP(), Config.getServerPort());
		}
	}

}
