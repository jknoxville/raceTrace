package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.server.ServerState;
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
		System.arraycopy(datagram.getData(), datagram.getOffset(), data, 0, datagram.getLength());
		try {
			//TODO any extra (sync?) data other than coords?
			int deviceID = data[0];
			
			((DeviceHandleIP) Session.getSession().getDevice(deviceID).getHandle()).setPort(datagram.getPort());

			byte[] coordinateData = new byte[data.length-Config.getDatagramMetadataSize()];
			System.arraycopy(data, Config.getDatagramMetadataSize(), coordinateData, 0, data.length-Config.getDatagramMetadataSize());
			insertCoordinateData(data, deviceID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void insertCoordinateData(byte[] data, int deviceID) throws Exception {
		int length = data.length;
		int numDataPoints = length/sizeOfDataPoint;
		if(length%sizeOfDataPoint != 0) {
			throw new Exception();
		}
		ByteBuffer bb = ByteBuffer.wrap(data);	//TODO use the other varient of .wrap and get rid of this function abstraction
		for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
			
			int lTime = bb.getInt();
			int x = bb.getInt();
			int y = bb.getInt();
			int alt = bb.getInt();
			CoordsTXYA coords = new CoordsTXYA(lTime, x, y, alt);
			PositionStore.insert(Session.getSession().getDevice(deviceID), coords);
		}
		ServerState.sendIfReady();
	}

}
