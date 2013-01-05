package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.server.ServerState;

public class ServerSessionCreator {
	
	static Device[] devices;
	
	public synchronized static void addDevice(int index, int numDevices, String name, String ip, int port) {
		if(devices == null) {
			devices = new Device[numDevices];
		}
		DeviceHandleIP handle;
		try {
			handle = new DeviceHandleIP(InetAddress.getByName(ip), port);
			devices[index] = new Device(name, handle, new ProtocolXYA());
			
			boolean listComplete = true;
			for(Device d:devices) {
				if (d == null) {
					listComplete = false;
				}
			}
			if(listComplete) {
				Session session = new Session(new ArrayList<Device>(Arrays.asList(devices)), null);
				ServerState.sendSessionToAllDevices(session);
				ServerState.startMainProcessing(session);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
