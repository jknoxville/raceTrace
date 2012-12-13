package uk.ac.cam.jk510.part2project.session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.protocol.Protocol;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;

public class SessionManagerPredefined extends SessionManager {

	@Override
	public void newSession(Activity activity) throws IllegalAccessException,
			InstantiationException {
		// TODO this

		ArrayList<Device> devices = new ArrayList<Device>();
		DeviceHandle handle = null;
		
		//device #1
		try {
			handle = new DeviceHandleIP(InetAddress.getByName(DataConnectionManager.getMyIP()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Protocol protocol = new ProtocolXYA();	//TODO make this use ProtocolXYA.class instead
		Device dev = new Device(Config.getName(), handle, protocol);
		devices.add(dev);
		
		//Device #2
		try {
			handle = new DeviceHandleIP(InetAddress.getByName("78.150.175.15"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dev = new Device(Config.getName(), handle, protocol);
		devices.add(dev);
		//Using null for keys since its single user and no comms needed.
		
		//this constructor saves static link to this instance from Session class
		Session session = new Session(devices, null);
		try {
			
		DataConnectionManager.sendSessionToServer(session);
		ProtocolManager.spawnSendingThread();
		
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
