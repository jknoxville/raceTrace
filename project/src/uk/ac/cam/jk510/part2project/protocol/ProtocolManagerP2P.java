package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import uk.ac.cam.jk510.part2project.network.ClientMessage;
import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.DeviceConnection;
import uk.ac.cam.jk510.part2project.network.DroppedPacketException;
import uk.ac.cam.jk510.part2project.network.TCPConnection;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerP2P extends ProtocolManager {

	//private DatagramSocket socket;
	public static boolean alive = true;	//TODO move this to ProtocolManager class and make private and alive().

	public void spawnReceivingThread() {

		
		try {
			connectToPeers();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(final Device device: Session.getSession().getDevices()) {
			if(!device.equals(Session.getSession().getThisDevice())) {

				new Thread(new Runnable() {
					public void run() {

						byte[] receivingData = new byte[1024];
						//checkSocketIsOpen();
						
						//moved to above
//						try {
//							connectToPeers();
//						} catch (UnknownHostException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
						while(alive) {
							try {
								System.out.println("connection "+device.getDeviceID()+": "+connections[device.getDeviceID()]);//debug
								ByteBuffer bb = DataConnectionManager.receive(connections[device.getDeviceID()], receivingData);
								System.out.println("Recieved datagram");
								ClientMessage.processData(bb);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DroppedPacketException e) {
								Logger.droppingPacket();
							}

						}
					}
				}).start();

			}
		}
	}
	//commented 23rd jan
	//	@Override
	//	public void spawnReceivingThread() {
	//		// The following is copied from ProtocolManagerClientServer
	//		// could be abstracted? TODO
	//
	//		new Thread(new Runnable() {
	//			public void run() {
	//				checkSocketIsOpen();
	//
	//				//debug:
	//				TextView debugInfo = MapDisplayScreen.debugInfo;
	//
	//				byte[] receivingData = new byte[1024];
	//				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
	//				checkSocketIsOpen();
	//				while(alive) {
	//					if(MapDisplayScreen.debugInfo != null) {
	//						//	debugInfo.setText("Device 0: "+((DeviceHandleIP) Session.getDevice(0).getHandle()).getIP().getHostAddress()+":"+((DeviceHandleIP) Session.getDevice(0).getHandle()).getPort());
	//					}
	//					try {
	//						DataConnectionManager.receive(datagram);
	//						System.out.println("Recieved datagram");
	//					} catch (IOException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
	//					ClientMessage.processDatagram(datagram);
	//				}
	//			}
	//		}).start();
	//
	//	}

	@Override
	protected void giveToNetwork(Coords coords) {
		//checkSocketIsOpen();
		for(Device toDevice: Session.getSession().getDevices()) {
			System.out.println("Sending to device "+toDevice.getDeviceID());	//debug
			if(Config.dontSendPointsToOwner() && (coords.getDevice() == toDevice.getDeviceID())) {
				//don't send
			} else {
				//do send
				if(toDevice != Session.getSession().getThisDevice()) {
					sendCoordsToPeer(toDevice, coords);
				}
			}

		}
	}

	@Override
	protected void respondToNetwork(int requester, List<Coords> response) {
		System.out.println("Got request from "+requester);
		if(requester == -1) {
			System.out.println("Ignoring request from server");

		} else {
			if(Config.replyToRequestsToMultiplePeers()) {
				for(Device toDevice: relientPeers()) {
					sendCoordsListToPeer(toDevice, response);
				}
			} else {
				sendCoordsListToPeer(Session.getDevice(requester), response);
			}
		}
	}

	public synchronized void sendCoordsToPeer(Device toDevice, Coords coords) {
		System.out.println(coordsToSend[toDevice.getDeviceID()]);	//debug
		coordsToSend[toDevice.getDeviceID()].add(coords);
		if(readyToSend(toDevice.getDeviceID())) {
			flushToNetwork(toDevice.getDeviceID());
		}
	}

	protected void flushToNetwork(int device) {
		Device toDevice = Session.getSession().getDevice(device);
		//DataConnectionManager.sendCoordsToAddress(((DeviceHandleIP) toDevice.getHandle()).getSocketAddress(), coordsToSend[toDevice.getDeviceID()]);
		DataConnectionManager.sendCoordsToDevice(connections[toDevice.getDeviceID()], coordsToSend[toDevice.getDeviceID()]);
		coordsToSend[device].clear();
	}

	public synchronized void sendCoordsListToPeer(Device toDevice, List<Coords> coordsList) {
		System.out.println(coordsToSend[toDevice.getDeviceID()]);	//debug
		coordsToSend[toDevice.getDeviceID()].addAll(coordsList);
		if(readyToSend(toDevice.getDeviceID())) {
			flushToNetwork(toDevice.getDeviceID());
		}
	}

	@Override
	protected void protocolSpecificDestroy() {
		alive = false;	//TODO see ProtocolManagerClientServer.
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

	public void connectToPeers() throws UnknownHostException, IOException {
		connections = new DeviceConnection[Session.getSession().numDevices()];
		System.out.println("About to open sockets");
		
		switch(Config.transportProtocol()) {
		case UDP:  for(Device d: Session.getSession().getDevices()) {connections[d.getDeviceID()] = DeviceConnection.newConnection(d);} System.out.println("conn 0"+connections[0]); return;
		case TCP: {TCPConnection.getConnectable(connections);} break;
		}

		//tcp only?
//		//TODO make it ProtocolManager.numConnections instead or make it do it or something for server and all.
//		for(Device device: Session.getSession().getDevices()) {
//
//			connections[device.getDeviceID()] = DeviceConnection.newConnection(device);
//
//		}
		System.out.println("conn 0"+connections[0]);	//debug
	}

	@Override
	protected void sendMissingRequest() {
		// TODO this
		try {
			//checkSocketIsOpen();
			//send request to all devices
			//TODO make recieve discard packets from self.
			for(Device toDevice: requestablePeers()) {
				if(toDevice.getHandle() instanceof DeviceHandleIP) {
				byte[] data = DataConnectionManager.createRequestMessageWithAddress(((DeviceHandleIP) toDevice.getHandle()).getSocketAddress(), requestArray);
				if(data == null) {return;}	//if no missing data, dont send any request
				DataConnectionManager.send(data, connections[toDevice.getDeviceID()]);
				} else {
					System.err.println("Not sending request because not DeviceHandleIP");
					break;
				}
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
	protected Collection<Device> requestablePeers() {
		//TODO use setting from config to decide which peers you can request from. e.g have a request circle / tree etc.
		return Session.getSession().getDevices();
	}
	@Override
	protected Collection<Device> relientPeers() {
		//TODO use setting from config to decide which peers you can request from. e.g have a request circle / tree etc.
		return Session.getSession().getDevices();
	}
}
