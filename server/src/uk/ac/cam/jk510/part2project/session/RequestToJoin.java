package uk.ac.cam.jk510.part2project.session;

import java.net.Socket;

public class RequestToJoin {
	
	public Socket socket;
	public Device device;
	
	public RequestToJoin(Socket sock, Device dev) {
		socket = sock;
		device = dev;
	}

}
