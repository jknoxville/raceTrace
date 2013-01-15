package uk.ac.cam.jk510.part2project.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.server.ServerState;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;

public class Message {
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
			
			//read metadata first
			int typeHeader = bb.getInt();
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
	
	public static void processDatapointDatagram(ByteBuffer bb, DatagramPacket datagram) throws IncompatibleCoordsException {
		//TODO dont need both data and datagram.
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
			PositionStore.insert(Session.getSession().getDevice(aboutDeviceID), coords);
			if(Config.serverDuplicationTest() && aboutDeviceID == 0) {
				System.out.println("Adding dupe");
				CoordsTXYA coords2 = new CoordsTXYA(fromDeviceID, lTime, x+10, y, alt);
				PositionStore.insert(Session.getSession().getDevice(aboutDeviceID+1), coords2);
			}
		}
		int oldPort = ((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).getPort();
		((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).setPort(datagram.getPort());
		int newPort = ((DeviceHandleIP) Session.getSession().getDevice(fromDeviceID).getHandle()).getPort();
		if(oldPort != newPort) {
			System.out.println("Device port changed from "+oldPort+" to "+newPort);
		}
		ServerState.sendIfReady();	//This is in server variant.
	}

}
