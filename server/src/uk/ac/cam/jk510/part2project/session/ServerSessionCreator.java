package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.server.ServerDriver;
import uk.ac.cam.jk510.part2project.server.ServerSession;
import uk.ac.cam.jk510.part2project.server.SessionDeviceConnection;
import uk.ac.cam.jk510.part2project.settings.Config;

public class ServerSessionCreator {
	static boolean listening = false;
	
	public void finaliseAndSendSession(SessionDeviceConnection[] connections, ArrayList<Device> devices) {
		System.out.println("List complete, devices: "+devices.size());
		Session session = new Session(devices, null);
		ServerSession servSesh = new ServerSession(devices, null, connections);
		int sessionID = ServerDriver.putSession(servSesh);
		servSesh.sendSessionToAllDevices(session, sessionID);
		//servSesh.startMainProcessing();
		if(!listening) {
			ServerSession.listen();
			listening = true;
		}
	}

}
