package uk.ac.cam.jk510.part2project.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.jk510.part2project.protocol.Logger;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;

public class DataConnectionManager {

	private static long[] keepAliveTimers;	//only for keepAlive messages	//TODO move this to deviceConnection class - its responsible for keeping itself alive
	private static long timeOfLastSend;	//one timer for all recipents.

	public static String getMyIP() throws SocketException {

		//TODO add secondary source. this is an uncontrollable single point of failure.
		URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");

			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendSessionToServer(Session session) throws UnknownHostException, IOException {
		SessionPackage pack = new SessionPackage(session);
		Socket sock = new Socket(Config.getServerIP(), Config.getServerPort());
		System.out.println("Connected to server");
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(pack);
		updateLastSendTime(0);
	}

	public static ByteBuffer receive(DeviceConnection conn, byte[] data) throws IOException, DroppedPacketException {
		ByteBuffer bb = conn.receiveData(data);
		Logger.download(data.length);
		return bb;
	}

	private static void updateLastSendTime(int device) {
		keepAliveTimers[device] = System.currentTimeMillis();
	}

	public static void keepAlive() {
		int index = 0;
		for(long timer: keepAliveTimers) {
			if(timer == 0 || timer+Config.getKeepAlivePeriod()<System.currentTimeMillis()) {
				ProtocolManager.getProtocolManager().sendKeepAliveMessage(index);
			}
			index++;
		}
	}

	public static void send(byte[] data, DeviceConnection conn) throws IOException {
		if(conn == null) System.out.println("Connection is null");
		if(data == null) System.out.println("Data is null");
		conn.sendGeneric(data, data.length);
		timeOfLastSend = System.currentTimeMillis();
		Logger.upload(data.length);
	}

	public static void sendCoordsToDevice(DeviceConnection conn, List<Coords> coordsList) {

		int fromDeviceID = Session.getSession().getThisDevice().getDeviceID();	//used to identify sender to the recipent.

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
		conn.sendGeneric(data, data.length);
	}

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
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.putInt(MessageType.request.ordinal());	//first 4 bytes: request header
		bb.putInt(Session.getSession().getThisDevice().getDeviceID());	//put fromID

		for(int device = 0; device<Session.getSession().numDevices(); device++) {
			if(requestArray[device].size() > 0) {
				bb.putInt(-1);
				bb.putInt(device);
				for(Integer index: requestArray[device]) {
					bb.putInt(index);
				}
			}
		}
		return data;
	}
	public static long timeOfLastSend() {
		return timeOfLastSend;
	}
}