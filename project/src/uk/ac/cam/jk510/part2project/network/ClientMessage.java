package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;

public class ClientMessage {
	//NOTE: this uses CoordsTXYA hardcoded, as opposed to generic Coords

	public static void processData(ByteBuffer bb) {
		int typeHeader = bb.getInt();
		MessageType type = MessageType.values()[typeHeader];
		try {
			switch(type) {
			case datapoints:	processDatapointDatagram(bb);	break;
			case request: 		processRequestDatagram(bb);		break;
			default: 				break;
			}	} catch (IncompatibleCoordsException e) {
				e.printStackTrace();
			}
	}

	public static void processDatagram(final DatagramPacket datagram) {
		System.out.println("offset = "+datagram.getOffset());
		try {
			System.out.println("Length of received packet: "+datagram.getLength()+" offset: "+datagram.getOffset());	//debug

			ByteBuffer bb = ByteBuffer.wrap(datagram.getData());
			bb.limit(datagram.getLength());	//set bb limit

			//read metadata first
			int typeHeader = bb.getInt();
			MessageType type = MessageType.values()[typeHeader];

			switch(type) {
			case datapoints:	processDatapointDatagram(bb, datagram);	break;
			case request: 		processRequestDatagram(bb, datagram);	break;
			default: 				break;
			}		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processDatapointDatagram(ByteBuffer bb, final DatagramPacket datagram) {
		System.out.println("offset = "+datagram.getOffset());
		try {
			int numDataPoints = (datagram.getLength()-4*(2))/(5*4);	//total length - type header - device ID. divided by 5 numbers per each coords.

			//read metadata first
			//get fromID
			int deviceID = bb.getInt();

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				System.out.println("datapoint in packet "+dataPoint);	//debug
				int aboutID = bb.getInt();
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from device "+deviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(aboutID, lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(deviceID, coords);
			}
			updatePort(deviceID, datagram);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void processDatapointDatagram(ByteBuffer bb) {

		try {
			int numDataPoints = (bb.limit()-4*(2))/(5*4);	//total length - type header - device ID. divided by 5 numbers per each coords.

			//read metadata first
			//get fromID
			int deviceID = bb.getInt();
			System.out.println("Got datapoints from "+deviceID);

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				int aboutID = bb.getInt();
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from device "+deviceID+"about "+aboutID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(aboutID, lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(deviceID, coords);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processRequestDatagram(ByteBuffer bb, DatagramPacket datagram) throws IncompatibleCoordsException {

		int fromDeviceID = bb.getInt();

		@SuppressWarnings("unchecked")
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

		ProtocolManager.serviceRequestAsClient(fromDeviceID, requestArray);

		updatePort(fromDeviceID, datagram);
	}
	public static void processRequestDatagram(ByteBuffer bb) throws IncompatibleCoordsException {
		//only process this if the session is still live
		try {
			int fromDeviceID = bb.getInt();

			@SuppressWarnings("unchecked")
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

			ProtocolManager.serviceRequestAsClient(fromDeviceID, requestArray);
		} catch (Exception e) {
			if(ProtocolManager.isAlive()) {
				System.err.println("ERROR encountered when session still live");
				e.printStackTrace();
			}
			//if not alive, ignore any error and die
		}
	}

	private static void updatePort(int deviceID, DatagramPacket datagram) {
		if(deviceID != -1) {	//if not from server, update that devices port.
			((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device.
		}
	}
}
