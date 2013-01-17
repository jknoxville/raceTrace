package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import android.widget.TextView;

import uk.ac.cam.jk510.part2project.gui.MapDisplayScreen;
import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.ClientMessage;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerP2P extends ProtocolManager {

	//private DatagramSocket socket;
	public static boolean alive = true;	//TODO move this to ProtocolManager class and make private and alive().

	@Override
	public void spawnReceivingThread() {
		// The following is copied from ProtocolManagerClientServer
		// could be abstracted? TODO

		new Thread(new Runnable() {
			public void run() {
				checkSocketIsOpen();

				//debug:
				TextView debugInfo = MapDisplayScreen.debugInfo;

				byte[] receivingData = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
				checkSocketIsOpen();
				while(alive) {
					if(MapDisplayScreen.debugInfo != null) {
						//	debugInfo.setText("Device 0: "+((DeviceHandleIP) Session.getDevice(0).getHandle()).getIP().getHostAddress()+":"+((DeviceHandleIP) Session.getDevice(0).getHandle()).getPort());
					}
					try {
						DataConnectionManager.receive(datagram);
						System.out.println("Recieved datagram");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ClientMessage.processDatagram(datagram);
				}
			}
		}).start();

	}

	@Override
	protected void giveToNetwork(Coords coords) {
		checkSocketIsOpen();
		for(Device toDevice: Session.getSession().getDevices()) {
			System.out.println("Sending to device "+toDevice.getDeviceID());	//debug
			sendCoordsToPeer(toDevice, coords);
		}
	}

	@Override
	protected void respondToNetwork(int requester, List<Coords> response) {
		if(Config.replyToRequestsToMultiplePeers()) {
			for(Device toDevice: relientPeers()) {
				sendCoordsListToPeer(toDevice, response);
			}
		} else {
			sendCoordsListToPeer(Session.getDevice(requester), response);
		}
	}

	public synchronized void sendCoordsToPeer(Device toDevice, Coords coords) {
		System.out.println(coordsToSend[toDevice.getDeviceID()]);	//debug
		coordsToSend[toDevice.getDeviceID()].add(coords);
		if(readyToSend(toDevice.getDeviceID())) {
			DataConnectionManager.sendCoordsToAddress(((DeviceHandleIP) toDevice.getHandle()).getSocketAddress(), coordsToSend[toDevice.getDeviceID()]);
			coordsToSend[toDevice.getDeviceID()].clear();
		}
	}

	public synchronized void sendCoordsListToPeer(Device toDevice, List<Coords> coordsList) {
		System.out.println(coordsToSend[toDevice.getDeviceID()]);	//debug
		coordsToSend[toDevice.getDeviceID()].addAll(coordsList);
		if(readyToSend(toDevice.getDeviceID())) {
			DataConnectionManager.sendCoordsToAddress(((DeviceHandleIP) toDevice.getHandle()).getSocketAddress(), coordsToSend[toDevice.getDeviceID()]);
			coordsToSend[toDevice.getDeviceID()].clear();
		}
	}

	@Override
	protected void protocolSpecificDestroy() {
		alive = false;	//TODO see ProtocolManagerClientServer.

	}

	private void checkSocketIsOpen() {

		DataConnectionManager.initDataSocket();
	}

	@Override
	public void distributeSession(Session session) throws UnknownHostException,
	IOException {
		//TODO move sneding of session by bluetooth to here

	}

	@Override
	public void sendKeepAliveMessage(int index) {
		// TODO Send it

	}

	@Override
	protected void sendMissingRequest() {
		// TODO this
		try {
			checkSocketIsOpen();
			//send request to all devices
			//TODO make recieve discard packets from self.
			for(Device toDevice: requestablePeers()) {
				DatagramPacket datagram = DataConnectionManager.createRequestMessageWithAddress(((DeviceHandleIP) toDevice.getHandle()).getSocketAddress(), getRequestArray());
				DataConnectionManager.send(datagram);
			}
		} catch (SocketException e) {
			//TODO 
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected List<Device> requestablePeers() {
		//TODO use setting from config to decide which peers you can request from. e.g have a request circle / tree etc.
		return Session.getSession().getDevices();
	}
	@Override
	protected List<Device> relientPeers() {
		//TODO use setting from config to decide which peers you can request from. e.g have a request circle / tree etc.
		return Session.getSession().getDevices();
	}
}
