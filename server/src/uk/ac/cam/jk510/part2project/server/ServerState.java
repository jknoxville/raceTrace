package uk.ac.cam.jk510.part2project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.network.Message;
import uk.ac.cam.jk510.part2project.protocol.ProtocolXYA;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;


public class ServerState implements PositionStoreSubscriber {

	/*
	 * This class holds the implementation state of the server, e.g which points from each device are new points.
	 * It subscribes to PositionStore updates so can stay updated.
	 */

	private static ArrayList<LinkedList<Integer>> globalNewPoints = new ArrayList<LinkedList<Integer>>();
	private static boolean initialised = false;
	private static long timeOfLastSend=0;
	private static int numNewPoints=0;
	private static DeviceConnection[] connections;

	public static void main(String[] args) {
		/*
		 * Receive serialized session from some device.
		 * Set up data storage structures
		 * Set up datagram socket.
		 * Spawn one listening thread.
		 * Start periodic sending thread.
		 * 
		 */

		//Receive serialized Session from some device
		/*
		 * Involves starting thread to listen for a connection
		 */

		Session session;

		if(Config.singleSession()) {

			int numDevices = Integer.parseInt(args[0]);
			connections = new DeviceConnection[numDevices];

			//spawn thread that accepts connections
			try {
				ServerSocket serverSock = new ServerSocket(60000);
				for(int i=0; i<numDevices; i++) {
					System.out.println("Waiting for device "+i);
					Socket sock = serverSock.accept();
					connections[i] = new DeviceConnection(i, sock, numDevices);
					connections[i].connectAndReceive();
					System.out.println("Connected to device "+i);
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			SessionPackage pack = NetworkInterface.getSessionPackage();
			session = Session.reconstructSession(pack);
			startMainProcessing(session);
		}


	}

	public static void startMainProcessing(Session session) {
		NetworkInterface net = NetworkInterface.getInstance();
		ServerState.init();

		//TODO spawn periodic sending thread
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(Config.getServerResendPeriodMillis());
						ServerState.sendIfReady();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		System.out.println("Now listening for data");	//debug

		//listen for incoming data and process it:
		while(true) {
			Message.processDatagram(net.receiveDatagram());
		}
	}

	public synchronized static void sendIfReady() {
		//init();	//init moved to Server.main
		if(ready()) {
			//TODO send points in batches, with configurable batch size
			//sendNewPoints();
			for(LinkedList<Integer> list: globalNewPoints) {
				int deviceNumber = globalNewPoints.indexOf(list);
				Device fromDevice = Session.getSession().getDevice(deviceNumber);
				NetworkInterface net = NetworkInterface.getInstance();
				for(int index: list) {
					Coords coords = PositionStore.getCoord(fromDevice, index);
					for(Device toDevice: Session.getSession().getDevices()) {
						net.sendCoordsToDevice(toDevice, fromDevice, coords);
					}
				}
			}

			for(LinkedList<Integer> list: globalNewPoints) {	//clear newPointsLists
				list.clear();
			}
			numNewPoints = 0;
			timeOfLastSend = System.currentTimeMillis();	//reset timer

		}
	}

	@Override
	public synchronized void notifyOfUpdate(Device d, LinkedList<Integer> givenNewPoints) {
		LinkedList<Integer> newPointsList = globalNewPoints.get(d.getDeviceID());
		newPointsList.addAll(givenNewPoints);
		numNewPoints += givenNewPoints.size();
		sendIfReady();
	}

	static void init() {
		if(!initialised) {
			PositionStore.subscribeToUpdates(new ServerState());	//subscribe to updates
			for(Device d: Session.getSession().getDevices()) {	//initialise lists
				globalNewPoints.add(d.getDeviceID(), new LinkedList<Integer>());
			}
			initialised = true;
		}
	}
	private static boolean ready() {
		//Note ready is always false when there is just one device in session.
		return (timeOfLastSend + Config.getServerResendPeriodMillis() <= System.currentTimeMillis()) || (numNewPoints>=Config.getServerNewPointsThreshold()) && Session.getSession().numDevices()!=1;
	}

	public static void sendSessionToAllDevices(Session session) {
		SessionPackage pack = new SessionPackage(session);
		for(DeviceConnection conn: connections) {
			conn.sendSessionPackage(pack);
		}

	}
	//TODO want to send session object just created to devices then start tracking.
}
