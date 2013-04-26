package uk.ac.cam.jk510.part2project.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Grid;
import uk.ac.cam.jk510.part2project.session.GridIterator;
import uk.ac.cam.jk510.part2project.session.Point;
import uk.ac.cam.jk510.part2project.session.RequestToJoin;
import uk.ac.cam.jk510.part2project.session.ServerSessionCreator;
import uk.ac.cam.jk510.part2project.settings.Config;

public class ServerDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Loop: 
		 * 	accepting connections
		 * 	enter them into grid
		 * 	
		 * In parallel with another Loop:
		 * 	just does the grid searching and grouping.
		 * 	when it groups, it spawns a new thread: some method in serverSession that processes it.
		 */
		ServerSocket serverSock = null;
		final Grid<RequestToJoin> grid = new Grid<RequestToJoin>();
		try{
			LinkedList<Socket> connections = new LinkedList<Socket>();
			serverSock = new ServerSocket(60000);
		}catch (Exception e) {
			e.printStackTrace();
		}

		//spawn grid grouping thread
		new Thread(new Runnable() {
			public void run() {
				GridIterator<RequestToJoin> it = grid.iterator();
				while(true) {
					while(it.hasNext()) {
						List<Point<RequestToJoin>> group = it.next();
						ServerSessionCreator creator = new ServerSessionCreator();
						System.out.println("Made group");

						for(Point<RequestToJoin> point: group) {
							RequestToJoin req = point.getObject();
							Device d = req.device;
							Socket s = req.socket;
							SessionDeviceConnection conn = new SessionDeviceConnection(group.indexOf(point), s, group.size());
							conn.connectAndReceive(creator);
						}
						
					}
					it.reset();
				}
			}
		}).start();

		while(true) {
			final Socket sock;
			try {
				sock = serverSock.accept();
				System.out.println("Got connection");

				new Thread(new Runnable() {
					public void run() {
						try {
							InputStream is = sock.getInputStream();
							ObjectInputStream ois = new ObjectInputStream(is);
							String name = (String) ois.readObject();
							System.out.println("Name: "+name);
							Double x = (Double) ois.readObject();
							System.out.println("x: "+x);
							Double y = (Double) ois.readObject();
							System.out.println("y: "+y);
							System.out.println("Got device "+name+" at "+x+", "+y);
							//						String ip = (String) ois.readObject();
							//						int port = (Integer) ois.readObject();
							String ip = sock.getInetAddress().getHostAddress();
							int port = Config.getDefaultClientPort();
							DeviceHandleIP handle;
							handle = new DeviceHandleIP(InetAddress.getByName(ip), Config.getDefaultClientPort());

							Device dev = new Device(name, handle, new ProtocolXYA());

							RequestToJoin req = new RequestToJoin(sock, dev);
							grid.insert(x, y, req);


						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
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
				}).start();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
