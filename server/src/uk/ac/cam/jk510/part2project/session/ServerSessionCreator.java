package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.server.ServerSession;
import uk.ac.cam.jk510.part2project.settings.Config;

public class ServerSessionCreator {
	
	Device[] devices;
	
	public synchronized void addDevice(int index, int numDevices, String name, String ip, int port) {
		if(devices == null) {
			devices = new Device[numDevices];
		}
		DeviceHandleIP handle;
		try {
			handle = new DeviceHandleIP(InetAddress.getByName(ip), Config.getDefaultClientPort());	//TODO could do setup by udp then server could use the port it came from here.
			devices[index] = new Device(name, handle, new ProtocolXYA());
			
			System.out.println(ip+" Device "+index+" @ "+handle.getIP().getHostName()+":"+handle.getPort());
			
			boolean listComplete = true;
			for(int d=0; d<devices.length; d++) {
				if (devices[d] == null) {
					if(!Config.serverDuplicationTest()) {
						listComplete = false;
					} else {
						devices[d] = new Device(Integer.toString(d), new DeviceHandleIP(((DeviceHandleIP) devices[0].getHandle()).getSocketAddress().getAddress(), 60001), new ProtocolXYA());
					}
					
				}
			}
			if(listComplete) {
				Session session = new Session(new ArrayList<Device>(Arrays.asList(devices)), null);
				ServerSession servSesh = new ServerSession(new ArrayList<Device>(Arrays.asList(devices)), null);
				servSesh.sendSessionToAllDevices(session);
				servSesh.startMainProcessing(session);
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
