package uk.ac.cam.jk510.part2project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;

public class NetworkInterface {

	public static String getMyIP() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		return in.readLine();
	}

	public static SessionPackage getSessionPackage() {
		try {
			SessionPackage pack = null;
			System.out.println(getMyIP());
			ServerSocket socket = new ServerSocket(Config.getServerPort());
			Socket sock = socket.accept();
			System.out.println("Connected to device");	//debug
			InputStream is = sock.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object receivedObject = ois.readObject();
			if(receivedObject instanceof SessionPackage) {
				pack = (SessionPackage) receivedObject;
				System.out.println("Got package");	//debug

			} else {
				System.out.println("Got something that's not a package");	//debug

			}
			socket.close();
			return pack;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
