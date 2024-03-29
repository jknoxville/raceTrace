package uk.ac.cam.jk510.part2project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;

public class NetworkInterface {

	//private DatagramSocket socket;
	byte[] receiveData = new byte[1024];
	private InetSocketAddress[] socketAddresses;

	private static NetworkInterface net;

	
	//commented 23 Jan
//	private NetworkInterface() {
//		byte[] data = new byte[1024];	//TODO check this hard limit is ok
//		DatagramPacket datagram = new DatagramPacket(data, data.length);
//		DatagramSocket sock;
//		try {
//			sock = new DatagramSocket(Config.getServerPort());
//			socket = sock;
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Session session = Session.getSession();
//		socketAddresses = new InetSocketAddress[session.numDevices()];
//		for(Device d: session.getDevices()) {
//			socketAddresses[d.getDeviceID()] = new InetSocketAddress(((DeviceHandleIP) d.getHandle()).getIP().getHostName(), ((DeviceHandleIP) d.getHandle()).getPort());
//		}
//		net = this;
//	}

	public static NetworkInterface getInstance() {
		if(net == null) {
			new NetworkInterface();
		}
		return net;
	}


	public static String getMyIP() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		return in.readLine();
	}

	public static SessionPackage getSessionPackage() {
		try {
			SessionPackage pack = null;
			System.out.println(getMyIP());
			ServerSocket socket = new ServerSocket(Config.getServerPort());
			Socket sock = socket.accept();
			System.out.println("Connected to device");	//debug
			InputStream is = sock.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object receivedObject = ois.readObject();
			if(receivedObject instanceof SessionPackage) {
				pack = (SessionPackage) receivedObject;
				System.out.println("Got package");	//debug

			} else {
				System.out.println("Got something that's not a package");	//debug

			}
			socket.close();
			return pack;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

//	public void sendCoordsToDevice(Device toDevice, Device aboutDevice, Coords coords) {
//
//		if(!Config.listenOnly()) {
//
//			/*
//			 * When sending from server, no fromID is sent.
//			 */
//			int deviceID = aboutDevice.getDeviceID();
//			int lClock = coords.getLClock();
//			float x = coords.getCoord(0);
//			float y = coords.getCoord(1);
//			float alt = coords.getCoord(2);
//			byte[] data = new byte[5*4];
//			ByteBuffer bb = ByteBuffer.wrap(data);
//			bb.putInt(deviceID);
//			bb.putInt(lClock);
//			bb.putFloat(x);
//			bb.putFloat(y);
//			bb.putFloat(alt);
//			try {
//				InetSocketAddress sockadd = new InetSocketAddress(((DeviceHandleIP) toDevice.getHandle()).getIP().getHostName(), ((DeviceHandleIP) toDevice.getHandle()).getPort());
//				DatagramPacket datagram = new DatagramPacket(data, data.length, sockadd);
//				//DatagramPacket datagram = new DatagramPacket(data, data.length, ((DeviceHandleIP) toDevice.getHandle()).getIP(), ((DeviceHandleIP) toDevice.getHandle()).getPort());
//				//DatagramPacket datagram = new DatagramPacket(data, data.length, socketAddresses[toDevice.getDeviceID()]);
//				//TODO clean up the socketAddresses thing.
//				System.out.println("About to send datapoint "+lClock+" to "+((DeviceHandleIP) toDevice.getHandle()).getIP().getHostName()+":"+((DeviceHandleIP) toDevice.getHandle()).getPort());
//				socket.send(datagram);
//			} catch (SocketException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	//commented 23 Jan
//	public void sendDatagram(DatagramPacket datagram) throws IOException {
//		socket.send(datagram);
//	}
//
//	public synchronized DatagramPacket receiveDatagram() {
//		try {
//			DatagramPacket datagram = new DatagramPacket(receiveData, receiveData.length);
//			socket.receive(datagram);
//			return datagram;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//
//	}

}
