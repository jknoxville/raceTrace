package uk.ac.cam.jk510.part2project.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.protocol.Protocol;
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
			handle = new DeviceHandleIP(InetAddress.getByName("10.135.95.166"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Protocol protocol = new ProtocolXYA();	//TODO make this use ProtocolXYA.class instead
		Device dev = new Device(Config.getName(), 0, handle, protocol);
		devices.add(dev);
		
		//Device #2
		try {
			handle = new DeviceHandleIP(InetAddress.getByName("78.150.175.15"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dev = new Device(Config.getName(), 1, handle, protocol);
		devices.add(dev);
		//Using null for keys since its single user and no comms needed.
		
		//this constructor saves static link to this instance from Session class
		new Session(devices, null);
		
	}

}
