package uk.ac.cam.jk510.part2project.network;

import java.io.IOException;
import java.nio.ByteBuffer;

import uk.ac.cam.jk510.part2project.session.Device;

public class TCPConnection extends DeviceConnection {
	
	TCPConnection(Device device) {
		
	}

	@Override
	public ByteBuffer receiveData(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void send(byte[] data, int length) {
		// TODO Auto-generated method stub
		
	}

}
