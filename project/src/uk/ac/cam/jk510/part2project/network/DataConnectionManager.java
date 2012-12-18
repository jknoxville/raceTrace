package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;

public class DataConnectionManager {

	public static String getMyIP() throws SocketException {

		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()) {
					return inetAddress.getHostAddress().toString();
				}
			}
		}

		return null;
	}

	public static void sendSessionToServer(Session session) throws UnknownHostException, IOException {
		SessionPackage pack = new SessionPackage(session);
		System.out.println("Initiating connection with server");	//debug
		Socket sock = new Socket(Config.getServerIP(), Config.getServerPort());
		System.out.println("Connected to server");	//debug
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(pack);
	}

	public static DatagramSocket getDataSocket() {
		DatagramSocket sock;
		try {
			sock = new DatagramSocket(Config.getDefaultClientPort());
			return sock;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
