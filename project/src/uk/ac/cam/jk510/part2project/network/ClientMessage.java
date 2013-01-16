package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;

public class ClientMessage {
	//TODO, this uses CoordsTXYA hardcoded

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
			System.out.println("typeHeader: "+typeHeader);	//debug
			MessageType type = MessageType.values()[typeHeader];

			switch(type) {
				case datapoints:	processDatapointDatagram(bb, datagram);	break;
				case request: 		processRequestDatagram(bb, datagram);	break;
			default: 				break;
			}		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void processDatapointDatagram(ByteBuffer bb, final DatagramPacket datagram) {

		//TODO check header for what type of message it is.
		//byte[] data = new byte[datagram.getLength()];
		//System.arraycopy(datagram.getData(), datagram.getOffset(), data, 0, datagram.getLength()); not needed as offset = 0
		System.out.println("offset = "+datagram.getOffset());
		try {
			//TODO any extra (sync?) data other than coords?

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
			System.out.println("Received");	//debug

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		ProtocolManager.serviceRequestAsClient(fromDeviceID, requestArray);

		updatePort(fromDeviceID, datagram);
	}

	private static void updatePort(int deviceID, DatagramPacket datagram) {
		if(deviceID != -1) {	//if not from server, update that devices port.
			((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device. Same for address useful?
		}
	}
}
