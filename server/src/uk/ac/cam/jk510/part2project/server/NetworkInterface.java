package uk.ac.cam.jk510.part2project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class NetworkInterface {

	private DatagramSocket socket;
	byte[] receiveData = new byte[1024];
	private InetSocketAddress[] socketAddresses;

	private static NetworkInterface net;

	private NetworkInterface() {
		byte[] data = new byte[1024];	//TODO check this hard limit is ok
		DatagramPacket datagram = new DatagramPacket(data, data.length);
		DatagramSocket sock;
		try {
			sock = new DatagramSocket(Config.getServerPort());
			socket = sock;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Session session = Session.getSession();
		socketAddresses = new InetSocketAddress[session.numDevices()];
		for(Device d: session.getDevices()) {
			socketAddresses[d.getDeviceID()] = new InetSocketAddress(((DeviceHandleIP) d.getHandle()).getIP(), ((DeviceHandleIP) d.getHandle()).getPort());
		}
		net = this;
	}

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

	public void sendCoordsToDevice(Device toDevice, Device fromDevice, Coords coords) {
		int deviceID = fromDevice.getDeviceID();
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

	public synchronized DatagramPacket receiveDatagram() {
		try {
			DatagramPacket datagram = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(datagram);
			return datagram;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}
