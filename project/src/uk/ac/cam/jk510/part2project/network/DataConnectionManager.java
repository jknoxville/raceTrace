package uk.ac.cam.jk510.part2project.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;

public class DataConnectionManager {

	private static DatagramSocket socket;
	private static ArrayList<Long> lastSendTimers;

	public static String getMyIP() throws SocketException {

		//		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		//			NetworkInterface intf = en.nextElement();
		//			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		//				InetAddress inetAddress = enumIpAddr.nextElement();
		//				if (!inetAddress.isLoopbackAddress()) {
		//					System.out.println("MY IP: "+inetAddress.getHostAddress().toString());
		//					return inetAddress.getHostAddress().toString();
		//				}
		//			}
		//		}

		//TODO make thread that sleeps for t time, on wake check if last receipt<now-t if so
		//check ip address again and send server change message.
		//alternatively, have server monitor addresses and adapt dynamically. - probably better.
		//TODO add secondary source. this is an uncontrollable point of failure.
		URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");

			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		updateLastSendTime(0);
	}
	
	public static void receive(DatagramPacket datagram) throws IOException {
		socket.receive(datagram);
	}

	//TODO What about p2p where each peer should have individual keepalive?
	/*
	 * How about:
	 * Keepalive message contains ids of all alive peers including their last heard from time.
	 */

	
	private static void updateLastSendTime(int device) {
		lastSendTimers.add(device, System.currentTimeMillis());
	}
	
	public static void keepAlive() {
		int index = 0;
		for(Long timer: lastSendTimers) {
			if(timer == null || timer+Config.getKeepAlivePeriod()<System.currentTimeMillis()) {
				//TODO send keep alive message
				ProtocolManager.getProtocolManager().sendKeepAliveMessage(index);
			}
			index++;
		}
	}

	public static void send(DatagramPacket datagram) throws IOException {
		socket.send(datagram);
	}

	public static void initDataSocket() {
		if(socket == null) {
			try {
				socket = new DatagramSocket(Config.getDefaultClientPort());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
