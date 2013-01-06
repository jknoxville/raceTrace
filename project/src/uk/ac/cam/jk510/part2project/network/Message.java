package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;

public class Message {
	//TODO, this uses CoordsTXYA hardcoded

	public static void processPeerDatagram(final DatagramPacket datagram) {
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
			int numDataPoints = 1;	//TODO make dynamic

			ByteBuffer bb = ByteBuffer.wrap(data);	

			//read metadata first
			//get fromID
			int deviceID = bb.getInt();
			
			//get aboutID
			int aboutID = bb.getInt();

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				System.out.println("Iteration "+dataPoint);	//debug
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from device "+deviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(Session.getDevice(deviceID), coords);
			}
			//ServerState.sendIfReady();	//This is in server variant.
			((DeviceHandleIP) Session.getDevice(deviceID).getHandle()).setPort(datagram.getPort());	//update known port of this device. Same for address useful?

			System.out.println("Received");	//debug
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//like processPeerDatagram but doesnt read the from field.
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
			int numDataPoints = 1;	//TODO make dynamic

			ByteBuffer bb = ByteBuffer.wrap(data);	

			//read metadata first
			
			//get aboutID
			int aboutID = bb.getInt();

			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				System.out.println("Iteration "+dataPoint);	//debug
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. from server. lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(Session.getDevice(aboutID), coords);
			}
			//ServerState.sendIfReady();	//This is in server variant.

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
