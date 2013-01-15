package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;

public class Message {
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
			
			//read metadata first
			int typeHeader = bb.getInt();
			System.out.println("typeHeader: "+typeHeader);	//debug
			MessageType type = MessageType.values()[typeHeader];
			
			switch(type) {
			case datapoints: processDatapointDatagram(bb, datagram); break;
			default: break;
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




			//			byte[] coordinateData = new byte[data.length-Config.getDatagramMetadataSize()];
			//			System.arraycopy(data, Config.getDatagramMetadataSize(), coordinateData, 0, data.length-Config.getDatagramMetadataSize());

			//			int length = data.length;
			//			int numDataPoints = length/sizeOfDataPoint;
			//			if(length%sizeOfDataPoint != 0) {
			//				System.err.println("sizeOfDataPoint: "+sizeOfDataPoint+" length = "+length);	//debug
			//				//throw new Exception();
			//			}

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
			if(deviceID != -1) {	//if not from server, update that devices port.
				((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device. Same for address useful?
			}
			System.out.println("Received");	//debug

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//like processPeerDatagram but doesnt read the from field.
	@Deprecated
	public static void processServerDatagram(final DatagramPacket datagram) {
		byte[] data = new byte[datagram.getLength()];
		//System.arraycopy(datagram.getData(), datagram.getOffset(), data, 0, datagram.getLength()); not needed as offset = 0
		data = datagram.getData();
		System.out.println("offset = "+datagram.getOffset());
		try {
			//TODO any extra (sync?) data other than coords?




			//			byte[] coordinateData = new byte[data.length-Config.getDatagramMetadataSize()];
			//			System.arraycopy(data, Config.getDatagramMetadataSize(), coordinateData, 0, data.length-Config.getDatagramMetadataSize());

			//			int length = data.length;
			//			int numDataPoints = length/sizeOfDataPoint;
			//			if(length%sizeOfDataPoint != 0) {
			//				System.err.println("sizeOfDataPoint: "+sizeOfDataPoint+" length = "+length);	//debug
			//				//throw new Exception();
			//			}
			System.out.println("datagram size: "+datagram.getLength()+" numDatapoints: "+(datagram.getLength())/(5*4)); //debug
			System.err.println("datagram size: "+datagram.getLength()+" numDatapoints: "+(datagram.getLength())/(5*4)); //debug
			int numDataPoints = (datagram.getLength() - 4)/(5*4);

			ByteBuffer bb = ByteBuffer.wrap(data);
			//read metadata first
			//get fromID
			int deviceID = bb.getInt();

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				System.out.println("datapoint in packet "+dataPoint+" out of "+numDataPoints);	//debug
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
