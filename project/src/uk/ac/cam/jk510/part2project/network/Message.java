package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public class Message {
	//TODO, this uses CoordsTXYA hardcoded, and no other.

	private static int sizeOfDataPoint = 4*4;	//4 times size of int, as in CoordsTXYA. TODO change it to use char or something.

	public static void processDatagram(final DatagramPacket datagram) {
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
			int deviceID = bb.getInt();
			
			for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
				System.out.println("Iteration "+dataPoint);	//debug
				int lTime = bb.getInt();
				float x = bb.getFloat();
				float y = bb.getFloat();
				float alt = bb.getFloat();
				System.out.println("receiving. device "+deviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
				CoordsTXYA coords = new CoordsTXYA(lTime, x, y, alt);
				ProtocolManager.insertNetworkDataPoint(Session.getSession().getDevice(deviceID), coords);
			}
			//ServerState.sendIfReady();	//This is in server variant.
			((DeviceHandleIP) Session.getSession().getDevice(deviceID).getHandle()).setPort(datagram.getPort());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private static void insertCoordinateData(byte[] coordinateData, int deviceID) throws Exception {
////		int length = data.length;
////		int numDataPoints = length/sizeOfDataPoint;
////		if(length%sizeOfDataPoint != 0) {
////			System.err.println("sizeOfDataPoint: "+sizeOfDataPoint+" length = "+length);	//debug
////			//throw new Exception();
////		}
//		int numDataPoints = 1;	//TODO make dynamic
//		
//		ByteBuffer bb = ByteBuffer.wrap(coordinateData);	//TODO use the other varient of .wrap and get rid of this function abstraction
//		
//		int deviceID = bb.readInt();
//		
//		for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
//			System.out.println("Iteration "+dataPoint);	//debug
//			int lTime = bb.getInt();
//			float x = bb.getFloat();
//			float y = bb.getFloat();
//			float alt = bb.getFloat();
//			System.out.println("receiving. device "+deviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
//			CoordsTXYA coords = new CoordsTXYA(lTime, x, y, alt);
//			ProtocolManager.insertNetworkDataPoint(Session.getSession().getDevice(deviceID), coords);
//		}
//		//ServerState.sendIfReady();	//This is in server variant.
//		((DeviceHandleIP) Session.getSession().getDevice(deviceID).getHandle()).setPort(datagram.getPort());
//	}

}
