package uk.ac.cam.jk510.part2project.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.Message;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class ProtocolManagerP2P extends ProtocolManager {
	
	private DatagramSocket socket;
	public static boolean alive;	//TODO move this to ProtocolManager class and make private and alive().

	@Override
	public void spawnReceivingThread() {
		// The following is copied from ProtocolManagerClientServer
		// could be abstracted? TODO
		
		new Thread(new Runnable() {
			public void run() {
				checkSocketIsOpen();
				byte[] receivingData = new byte[1024];
				DatagramPacket datagram = new DatagramPacket(receivingData, receivingData.length);
				while(alive) {
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

	@Override
	protected void giveToNetwork(Device device, Coords coords) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void protocolSpecificDestroy() {
		// TODO Auto-generated method stub

	}
	
	private void checkSocketIsOpen() {
		if(socket == null) {
			socket = DataConnectionManager.getDataSocket();
		}
	}

}
