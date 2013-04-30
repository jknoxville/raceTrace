package uk.ac.cam.jk510.part2project.session;

import grouping.Request;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.jk510.part2project.server.SessionDeviceConnection;

public class RequestToJoin implements Processable {
	
	public Socket socket;
	public Device device;
	
	public RequestToJoin(Socket sock, Device dev) {
		socket = sock;
		device = dev;
	}

	public void process(List<Request> reqs) {
		ServerSessionCreator creator = new ServerSessionCreator();
		System.out.println("Made group");
		SessionDeviceConnection[] connections = new SessionDeviceConnection[reqs.size()];
		
		ArrayList<Device> devices = new ArrayList<Device>();

		for(Request<RequestToJoin> request: reqs) {
			RequestToJoin req = request.getValue();
			Device d = req.device;
			Socket s = req.socket;
			System.out.println("making sessionDevice conn: device:"+reqs.indexOf(request)+" of "+reqs.size());
			
			SessionDeviceConnection conn = new SessionDeviceConnection(reqs.indexOf(request), s, reqs.size());
			connections[reqs.indexOf(request)] = conn;
			System.out.println("about to conn and rec");
			
			devices.add(request.getValue().device);
		}
		creator.finaliseAndSendSession(connections, devices);
		
	}

}
