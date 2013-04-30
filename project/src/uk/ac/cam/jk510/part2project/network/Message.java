package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;

public class Message {
	//TODO, this uses CoordsTXYA hardcoded

	public static void processDatagram(final DatagramPacket datagram) {
		System.out.println("offset = "+datagram.getOffset());
		try {
			
			ByteBuffer bb = ByteBuffer.wrap(datagram.getData());
			
			//read metadata first
			int typeHeader = bb.getInt();
			System.out.println("typeHeader: "+typeHeader);	//debug
			MessageType type = MessageType.values()[typeHeader];
			
			switch(type) {
			case datapoints: processDatapointDatagram(bb, datagram); break;
			default: break;
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
				int aboutID = bb.getInt();
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from device "+deviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(aboutID, lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(deviceID, coords);
			}
			if(deviceID != -1) {	//if not from server, update that devices port.
				((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device.
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//like processPeerDatagram but doesnt read the from field.
	@Deprecated
	public static void processServerDatagram(final DatagramPacket datagram) {
		byte[] data = new byte[datagram.getLength()];
		data = datagram.getData();
		System.out.println("offset = "+datagram.getOffset());
		try {
			System.out.println("datagram size: "+datagram.getLength()+" numDatapoints: "+(datagram.getLength())/(5*4)); //debug
			System.err.println("datagram size: "+datagram.getLength()+" numDatapoints: "+(datagram.getLength())/(5*4)); //debug
			int numDataPoints = (datagram.getLength() - 4)/(5*4);

			ByteBuffer bb = ByteBuffer.wrap(data);
			//read metadata first
			//get fromID
			@SuppressWarnings("unused")
			int deviceID = bb.getInt();

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				int aboutID = bb.getInt();
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from server. lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(aboutID, lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(-1, coords);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
