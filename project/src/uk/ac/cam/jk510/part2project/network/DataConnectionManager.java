package uk.ac.cam.jk510.part2project.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.jk510.part2project.protocol.Logger;
import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class DataConnectionManager {

	//private static DatagramSocket socket;
	//private static DeviceConnection[] connections;
	private static long[] keepAliveTimers;	//only for keepAlive messages	//TODO move this to deviceConnection class - its responsible for keeping itself alive
	private static long timeOfLastSend;	//one timer for all recipents.

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
			String ip = in.readLine();
			return ip;

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

	public static ByteBuffer receive(DeviceConnection conn, byte[] data) throws IOException {
		ByteBuffer bb = conn.receiveData(data);
		Logger.download(data.length);	//TODO log header size?
		return bb;
	}

	//TODO What about p2p where each peer should have individual keepalive?
	/*
	 * How about:
	 * Keepalive message contains ids of all alive peers including their last heard from time.
	 */

	private static void updateLastSendTime(int device) {
		keepAliveTimers[device] = System.currentTimeMillis();
	}

	public static void keepAlive() {
		int index = 0;
		for(long timer: keepAliveTimers) {
			if(timer == 0 || timer+Config.getKeepAlivePeriod()<System.currentTimeMillis()) {
				//TODO send keep alive message
				ProtocolManager.getProtocolManager().sendKeepAliveMessage(index);
			}
			index++;
		}
	}

	public static void send(byte[] data, DeviceConnection conn) throws IOException {
		if(!Config.droppingPackets()) {
			if(conn == null) System.out.println("Connection is null");
			if(data == null) System.out.println("Data is null");
			conn.sendGeneric(data, data.length);
		}
		timeOfLastSend = System.currentTimeMillis();
		Logger.upload(data.length);
	}

	public static void sendCoordsToDevice(DeviceConnection conn, List<Coords> coordsList) {

		int fromDeviceID = Session.getThisDevice().getDeviceID();	//used to identify sender to the recipent.

		byte[] data = new byte[(1+1+5*coordsList.size())*4];	//1 int for coords header, 1 int for fromID, plus 5 (int|float)s for each coord
		ByteBuffer bb = ByteBuffer.wrap(data);

		bb.putInt(MessageType.datapoints.ordinal());	//put message header
		bb.putInt(fromDeviceID);	//
		//							This are to go at the start of each packet, not each coordinate (if >1 coord per packet)

		for(Coords coords: coordsList) {

			int aboutDeviceID = coords.getDevice();	//deviceID of the device whose location this point is.
			int lClock = coords.getLClock();
			float x = coords.getCoord(0);
			float y = coords.getCoord(1);
			float alt = coords.getCoord(2);

			bb.putInt(aboutDeviceID);
			bb.putInt(lClock);
			bb.putFloat(x);
			bb.putFloat(y);
			bb.putFloat(alt);
			System.out.println("sending. device "+aboutDeviceID+" lClock "+lClock+" x "+x+" y "+y+" alt "+alt);

		}
		//checkInit();
		conn.sendGeneric(data, data.length);
	}
//	public static void sendCoordsToAddress(final InetSocketAddress toSocketAddress, List<Coords> coordsList) {
//
//		System.out.println("sending to "+toSocketAddress.getAddress().getHostAddress()+":"+toSocketAddress.getPort());
//		int fromDeviceID = Session.getThisDevice().getDeviceID();	//used to identify sender to the recipent.
//
//		byte[] data = new byte[(1+1+5*coordsList.size())*4];	//1 int for coords header, 1 int for fromID, plus 5 (int|float)s for each coord
//		ByteBuffer bb = ByteBuffer.wrap(data);
//
//		bb.putInt(MessageType.datapoints.ordinal());	//put message header
//		bb.putInt(fromDeviceID);	//
//		//							This are to go at the start of each packet, not each coordinate (if >1 coord per packet)
//
//		for(Coords coords: coordsList) {
//
//			int aboutDeviceID = coords.getDevice();	//deviceID of the device whose location this point is.
//			int lClock = coords.getLClock();
//			float x = coords.getCoord(0);
//			float y = coords.getCoord(1);
//			float alt = coords.getCoord(2);
//
//			bb.putInt(aboutDeviceID);
//			bb.putInt(lClock);
//			bb.putFloat(x);
//			bb.putFloat(y);
//			bb.putFloat(alt);
//			System.out.println("sending. device "+aboutDeviceID+" lClock "+lClock+" x "+x+" y "+y+" alt "+alt);
//
//		}
//		try {
//			//checkInit();
//			DatagramPacket datagram = new DatagramPacket(data, data.length, toSocketAddress);
//			DataConnectionManager.send(datagram);
//
//			if(Config.getProtocol() == Proto.p2p && Config.debugMode()) {
//				DatagramPacket datagram2 = new DatagramPacket(data, data.length, new InetSocketAddress(Config.getServerIP(), Config.getServerPort()));
//				DataConnectionManager.send(datagram2);
//			}
//
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static byte[] createRequestMessageWithAddress(final InetSocketAddress socketAddress, LinkedList<Integer>[] requestArray) throws SocketException {
		System.out.println("Starting to create request data");

		int size = 0;	//total number of absent points
		int numMissingDevices = 0;
		for(int i=0; i<Session.getSession().numDevices(); i++) {
			size += requestArray[i].size();
			if(size>0) {
				numMissingDevices += 1;
			}
		}
		if(numMissingDevices == 0) {return null;}	//no need to make empty datagram
		byte[] data = new byte[4+4+4*size+8*numMissingDevices];
		/*
		 * 4 byte int header to identify the request message
		 * 4 byte int fromID
		 * 4 byte int for each missing point, of which there are size
		 * 2 4 byte ints preceeding each list of missing points for those devices that have any. thats a -1 marker, and then device ID
		 */
		if(data == null) System.out.println("Data is null before bb.wrap");
		ByteBuffer bb = ByteBuffer.wrap(data);
		if(data == null) System.out.println("Data is null after bb.wrap");
		bb.putInt(MessageType.request.ordinal());	//first 4 bytes: request header
		bb.putInt(Session.getThisDevice().getDeviceID());	//put fromID

		for(int device = 0; device<Session.getSession().numDevices(); device++) {
			System.out.println("requesting for device "+device);	//debug
			if(requestArray[device].size() > 0) {
				bb.putInt(-1);
				bb.putInt(device);
				for(Integer index: requestArray[device]) {
					bb.putInt(index);
				}
			}
		}
		//DatagramPacket datagram = new DatagramPacket(data, data.length, socketAddress);
		System.out.println("Data: "+data);
		return data;
	}
	public static long timeOfLastSend() {
		return timeOfLastSend;
	}
}