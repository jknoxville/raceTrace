package uk.ac.cam.jk510.part2project.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import uk.ac.cam.jk510.part2project.session.ServerSessionCreator;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;

public class DeviceConnection {
	
	/*
	 * This class holds the connection state of devices for the session setup procedure.
	 */

	final int device;
	final Socket sock;
	final int devices;

	public DeviceConnection(int i, Socket s, int numDevices) {
		device = i;
		sock = s;
		devices = numDevices;
	}

	public void connectAndReceive() {
		new Thread(new Runnable() {
			public void run() {
				try {
					InputStream is = sock.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(is);
					String name = (String) ois.readObject();
					System.out.println("Got device "+name);
//					String ip = (String) ois.readObject();
//					int port = (Integer) ois.readObject();
					String ip = sock.getInetAddress().getHostAddress();
					int port = Config.getDefaultClientPort();

					ServerSessionCreator.addDevice(device, devices, name, ip, port);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void sendSessionPackage(SessionPackage pack) {
		try {
			OutputStream os = sock.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(pack);
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
