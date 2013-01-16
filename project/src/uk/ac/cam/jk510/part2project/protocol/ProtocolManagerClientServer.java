package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.Message;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerClientServer extends ProtocolManager {

	//private DatagramSocket socket;
	private InetSocketAddress serverSocketAddress;
	private static boolean alive = true;

	@Override
	protected void giveToNetwork(Device aboutDevice, Coords coords) {
		checkInit();

		coordsToSend[0].add(coords);

		if(readyToSend(0)) {
			sendCoordsToServer(aboutDevice, coordsToSend[0]);	//TODO move the "getThisDevice" to a later stage
			coordsToSend[0].clear();
		}
	}

	@Override
	public void spawnReceivingThread() {

		new Thread(new Runnable() {
			public void run() {
				checkInit();
				byte[] receivingData = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
				while(alive) {
					try {
						//socket.receive(datagram);
						DataConnectionManager.receive(datagram);	//this is a destructive method on the datagram object
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

	public void spawnKeepAliveThread() {
		new Thread(new Runnable() {
			public void run() {
				while(alive) {
					try {
						Thread.sleep(Config.getKeepAlivePeriod());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DataConnectionManager.keepAlive();
				}
			}
		}).start();
	}

	//this method was based on Server -> NetworkInterface.sendCoordsToDevice
	private void sendCoordsToServer(Device aboutDevice, List<Coords> coordsList) {

		checkInit();
		DataConnectionManager.sendCoordsToAddress(serverSocketAddress, aboutDevice, coordsList);

	}

	private void checkInit() {
		DataConnectionManager.initDataSocket();
		if(serverSocketAddress == null) {
			serverSocketAddress = new InetSocketAddress(Config.getServerIP(), Config.getServerPort());
		}
	}

	@Override
	protected void protocolSpecificDestroy() {
		alive = false;	//stop receiving thread TODO warning: thread may be blocking on network so wont actually stop until next packet arrives.
	}

	@Override
	public void distributeSession(Session session) throws UnknownHostException, IOException {
		DataConnectionManager.sendSessionToServer(session);
	}

	@Override
	public void sendKeepAliveMessage(int device) {
		//TODO send it.
	}

	@Override
	protected void sendMissingRequest() {
		try {
			checkInit();
			DatagramPacket datagram = DataConnectionManager.createRequestMessageWithAddress(serverSocketAddress, getRequestArray());
			DataConnectionManager.send(datagram);
		} catch (SocketException e) {
			//TODO 
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}