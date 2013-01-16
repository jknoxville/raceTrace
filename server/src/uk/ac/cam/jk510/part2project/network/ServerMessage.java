package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.server.ServerState;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public class ServerMessage {
	//TODO, this uses CoordsTXYA hardcoded, and no other.

	private static int sizeOfDataPoint = 4*4;	//4 times size of int, as in CoordsTXYA. TODO change it to use char or something.

	public static void processDatagram(final DatagramPacket datagram) {
		//System.arraycopy(datagram.getData(), datagram.getOffset(), data, 0, datagram.getLength()); not needed as offset = 0
		//byte[] data = datagram.getData();	//this is larger than necessary. data.length >= datagram.getLength()
		System.out.println("offset = "+datagram.getOffset());
		try {
			//TODO any extra (sync?) data other than coords?

			System.out.println("Length of received packet: "+datagram.getLength()+" offset: "+datagram.getOffset());	//debug
			//			int numDataPoints = (datagram.getLength()-4*(1+1))/(5*4);	//type header, fromID
			//			System.out.println("Number of datapoints in this packet: "+numDataPoints);	//debug

			ByteBuffer bb = ByteBuffer.wrap(datagram.getData());
			bb.limit(datagram.getLength());	//set bb limit

			//read metadata first
			int typeHeader = bb.getInt();
			MessageType type = MessageType.values()[typeHeader];

			switch(type) {
			case datapoints: processDatapointDatagram(bb, datagram); break;
			case request: processRequestDatagram(bb, datagram); break;
			default: break;
			}		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void processDatapointDatagram(ByteBuffer bb, DatagramPacket datagram) throws IncompatibleCoordsException {
		
		int fromDeviceID = bb.getInt();

		int numDataPoints = (datagram.getLength()-8)/(5*4);	//type header, fromID
		System.out.println("Data length: "+datagram.getLength()+" Number of datapoints in this packet: "+numDataPoints);	//debug

		for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
			int aboutDeviceID = bb.getInt();
			System.out.println("Iteration "+dataPoint);	//debug
			int lTime = bb.getInt();
			float x = bb.getFloat();
			float y = bb.getFloat();
			float alt = bb.getFloat();
			//System.out.println("receiving from "+datagram.getAddress().getHostName()+":"+datagram.getPort()+" device "+aboutDeviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
			CoordsTXYA coords = new CoordsTXYA(aboutDeviceID, lTime, x, y, alt);
			PositionStore.insert(aboutDeviceID, coords);
			if(Config.serverDuplicationTest() && aboutDeviceID == 0) {
				System.out.println("Adding dupe");
				CoordsTXYA coords2 = new CoordsTXYA(fromDeviceID, lTime, x+10, y, alt);
				PositionStore.insert(aboutDeviceID+1, coords2);
			}
		}
		updatePort(fromDeviceID, datagram);
		ServerState.sendIfReady();	//This is in server variant.
	}

	public static void processRequestDatagram(ByteBuffer bb, DatagramPacket datagram) throws IncompatibleCoordsException {
		
		int fromDeviceID = bb.getInt();
		
		LinkedList<Integer>[] requestArray = new LinkedList[Session.getSession().numDevices()];
		for(int dev = 0; dev<requestArray.length; dev++) {
			requestArray[dev] = new LinkedList<Integer>();
		}
		
		int currentDevice = -1;
		while(bb.hasRemaining()) {
			int i;
			if((i = bb.getInt()) == -1) {
				//device seperator
				currentDevice = bb.getInt();
			} else {
				//request data
				requestArray[currentDevice].add(i);
			}
		}
		
		ServerState.serviceRequest(fromDeviceID, requestArray);
		
		updatePort(fromDeviceID, datagram);
	}

	private static void updatePort(int fromDeviceID, DatagramPacket datagram) {
		int oldPort = ((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).getPort();
		((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).setPort(datagram.getPort());
		int newPort = ((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).getPort();
		if(oldPort != newPort) {
			System.out.println("Device port changed from "+oldPort+" to "+newPort);
		}
	}
	
	public static DatagramPacket createRequestMessageWithAddress(final InetSocketAddress socketAddress, LinkedList<Integer>[] requestArray) throws SocketException {

		int size = 0;	//total number of absent points
		int numMissingDevices = 0;
		for(int i=0; i<Session.getSession().numDevices(); i++) {
			size += requestArray[i].size();
			if(size>0) {
				numMissingDevices += 1;
			}
		}
		byte[] data = new byte[4+4*size+8*numMissingDevices];
		/*
		 * 4 byte int header to identify the request message
		 * 4 byte int for each missing point, of which there are size
		 * 2 4 byte ints preceeding each list of missing points for those devices that have any. thats a -1 marker, and then device ID
		 */
		ByteBuffer bb = ByteBuffer.wrap(data);

		bb.putInt(MessageType.request.ordinal());	//first 4 bytes: request header
		bb.putInt(-1);	//put fromID

		for(int device = 0; device<Session.getSession().numDevices(); device++) {
			if(requestArray[device].size() > 0) {
				bb.putInt(-1);
				bb.putInt(device);
				for(Integer index: requestArray[device]) {
					bb.putInt(index);
				}
			}
		}
		DatagramPacket datagram = new DatagramPacket(data, data.length, socketAddress);
		return datagram;
	}

}
