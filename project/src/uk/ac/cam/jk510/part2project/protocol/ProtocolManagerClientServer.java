package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.Message;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerClientServer extends ProtocolManager {

	private DatagramSocket socket;
	private InetSocketAddress[] socketAddresses;

	@Override
	protected void giveToNetwork(Device device, Coords coords) {
		checkInit();
		sendCoordsToServer(device, coords);
	}

	@Override
	public void spawnReceivingThread() {
		new Thread(new Runnable() {
			public void run() {
				byte[] receivingData = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
				while(true) {
					try {
						socket.receive(datagram);
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
	private void sendCoordsToServer(Device toDevice, Coords coords) {
		int deviceID = Session.getThisDevice().getDeviceID();
		float x = coords.getCoord(0);
		float y = coords.getCoord(1);
		float alt = coords.getCoord(2);
		byte[] data = new byte[4*4];
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.putInt(deviceID);
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(alt);
		try {
			DatagramPacket datagram = new DatagramPacket(data, data.length, socketAddresses[toDevice.getDeviceID()]);
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
			for(int i=0; i<socketAddresses.length; i++) {	//create SocketAddress for each device.
				ArrayList<Device> deviceList = Session.getSession().getDevices();
				socketAddresses[i] = new InetSocketAddress(((DeviceHandleIP) deviceList.get(i).getHandle()).getIP(), ((DeviceHandleIP) deviceList.get(i).getHandle()).getPort());
			}
		}
	}

}
