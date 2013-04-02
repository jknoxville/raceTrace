package uk.ac.cam.jk510.part2project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import uk.ac.cam.jk510.part2project.network.DeviceConnection;
import uk.ac.cam.jk510.part2project.network.MessageType;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.DeviceHandleIP;
import uk.ac.cam.jk510.part2project.session.Keys;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsTXYA;
import uk.ac.cam.jk510.part2project.store.IncompatibleCoordsException;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;
import uk.ac.cam.jk510.part2project.store.Response;

public class ServerSession implements PositionStoreSubscriber {

	//one linkedlist for each device to send to.
	protected LinkedList<Coords>[] coordsToSend;
	private PositionStore posStore;
	
	private ArrayList<LinkedList<Integer>> globalNewPoints = new ArrayList<LinkedList<Integer>>();
	private long timeOfLastSend=0;
	private int numNewPoints=0;
	private SessionDeviceConnection[] sessionSetupConnections;
	private DeviceConnection[] connections;
	private boolean alive = true;
	
	public PositionStore getStore() {
		return posStore;
	}
	
	private static int sizeOfDataPoint = 4*4;	//4 times size of int, as in CoordsTXYA. TODO change it to use char or something.
	
	private ArrayList<Device> devices;
	private Keys keys;
	private int meNumber = -1;
	private int deviceCount = 0;

	public void processData(ByteBuffer bb) {
		//System.arraycopy(datagram.getData(), datagram.getOffset(), data, 0, datagram.getLength()); not needed as offset = 0
		//byte[] data = datagram.getData();	//this is larger than necessary. data.length >= datagram.getLength()

		try {

			//read metadata first
			int typeHeader = bb.getInt();
			MessageType type = MessageType.values()[typeHeader];

			switch(type) {
			case datapoints: processDatapointDatagram(bb); break;
			case request: processRequestDatagram(bb); break;
			default: System.out.println("ERROR: not datapoints or request"); break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processDatapointDatagram(ByteBuffer bb) throws IncompatibleCoordsException {
		
		int fromDeviceID = bb.getInt();

		int numDataPoints = (bb.limit()-8)/(5*4);	//type header, fromID

		for(int dataPoint=0; dataPoint<numDataPoints; dataPoint++) {
			int aboutDeviceID = bb.getInt();
			int lTime = bb.getInt();
			float x = bb.getFloat();
			float y = bb.getFloat();
			float alt = bb.getFloat();
			//System.out.println("receiving from "+datagram.getAddress().getHostName()+":"+datagram.getPort()+" device "+aboutDeviceID+" lClock "+lTime+" x "+x+" y "+y+" alt "+alt);
			CoordsTXYA coords = new CoordsTXYA(aboutDeviceID, lTime, x, y, alt);
			posStore.insert(aboutDeviceID, coords);
			if(Config.serverDuplicationTest() && aboutDeviceID == 0) {
				System.out.println("Adding dupe");
				CoordsTXYA coords2 = new CoordsTXYA(fromDeviceID, lTime, x+10, y, alt);
				posStore.insert(aboutDeviceID+1, coords2);
			}
		}
		sendIfReady();	//This is in server variant.
	}
	

	public void processRequestDatagram(ByteBuffer bb) throws IncompatibleCoordsException {
		
		int fromDeviceID = bb.getInt();
		
		LinkedList<Integer>[] requestArray = new LinkedList[numDevices()];
		for(int dev = 0; dev<requestArray.length; dev++) {
			requestArray[dev] = new LinkedList<Integer>();
		}
		int currentDevice = -1;
		while(bb.hasRemaining()) {
			int i;
			if((i = bb.getInt()) == -1) {
				//device seperator
				currentDevice = bb.getInt();
				System.out.println("device "+currentDevice);
			} else {
				//request data
				requestArray[currentDevice].add(i);
			}
		}
		
		serviceRequest(fromDeviceID, requestArray);
		
	}

	private void updatePort(int fromDeviceID, DatagramPacket datagram) {
		int oldPort = ((DeviceHandleIP) getDevice(fromDeviceID).getHandle()).getPort();
		((DeviceHandleIP) getDevice(fromDeviceID).getHandle()).setPort(datagram.getPort());
		int newPort = ((DeviceHandleIP) getDevice(fromDeviceID).getHandle()).getPort();
		if(oldPort != newPort) {
			System.out.println("Device port changed from "+oldPort+" to "+newPort);
		}
	}
	
	public DatagramPacket createRequestMessageWithAddress(final InetSocketAddress socketAddress, LinkedList<Integer>[] requestArray) throws SocketException {

		int size = 0;	//total number of absent points
		int numMissingDevices = 0;
		for(int i=0; i<numDevices(); i++) {
			size += requestArray[i].size();
			if(size>0) {
				numMissingDevices += 1;
			}
		}
		if(size == 0) {
			return null;
		}
		System.out.println("Sending request to "+socketAddress.getHostName()+" of size "+size);
		byte[] data = new byte[4+4+4*size+8*numMissingDevices];
		/*
		 * 4 byte int header to identify the request message
		 * 4 byte fromID
		 * 4 byte int for each missing point, of which there are size
		 * 2 4 byte ints preceeding each list of missing points for those devices that have any. thats a -1 marker, and then device ID
		 */
		ByteBuffer bb = ByteBuffer.wrap(data);

		bb.putInt(MessageType.request.ordinal());	//first 4 bytes: request header
		bb.putInt(-1);	//put fromID

		for(int device = 0; device<numDevices(); device++) {
			if(requestArray[device].size() > 0) {
				bb.putInt(-1);
				bb.putInt(device);
				for(Integer index: requestArray[device]) {
					bb.putInt(index);
				}
			}
		}
		DatagramPacket datagram = new DatagramPacket(data, data.length, socketAddress);
		return datagram;
	}
	
	public ServerSession(ArrayList<Device> devices, Keys keys) {
		super();
		this.devices = devices;
		this.keys = keys;
		for(Device d: devices) {
			System.out.println(d.getName()+" and "+Config.getName());
			//TODO use bluetooth MAC address instead of name.
			if(d.getName().equals(Config.getName())) {
				meNumber = deviceCount;
				System.out.println("I am device "+meNumber+" / "+devices.size());
				break;
			}
			
			deviceCount++;
		}
		for(int i=0; i<numDevices(); i++) {	//initialise lists
			globalNewPoints.add(new LinkedList<Integer>());
		}
		posStore = new PositionStore(this);
		posStore.subscribeToUpdates(this);
		//TODO make Config.name read name from some preferences (see android tutorials)
		//TODO have check when setting up session to see if names clash.
		
	}
	
	public Device getDevice(int n) {
		return devices.get(n);
	}

	public String[] getDeviceNames() {
		String[] names = new String[devices.size()];
		for(Device d: devices) {
			names[devices.indexOf(d)] = d.getName();
		}
		return names;
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}
	public Keys getKeys() {
		return keys;
	}
	public int numDevices() {
		return devices.size();
	}

	public int getIndex(Device device) {
		return devices.indexOf(device);
	}
	
	public synchronized void sendIfReady() {
		//init();	//init moved to Server.main
		if(ready()) {
			//TODO send points in batches, with configurable batch size
			//sendNewPoints();
			for(LinkedList<Integer> list: globalNewPoints) {
				int deviceNumber = globalNewPoints.indexOf(list);
				Device fromDevice = getDevice(deviceNumber);

				for(int index: list) {
					Coords coords = posStore.getCoord(fromDevice, index);
					for(Device toDevice: getDevices()) {

						if(Config.dontSendPointsToOwner() && (coords.getDevice() == getIndex(toDevice))) {
							//don't send
						} else {
							//do send
							coordsToSend[getIndex(toDevice)].add(coords);
						}

						//byte[] data = new byte[1024];
						//DatagramPacket datagram = new DatagramPacket(data, data.length, sockadd);


						//net.sendCoordsToDevice(toDevice, fromDevice, coords);
					}
				}
			}
			for(LinkedList<Integer> list: globalNewPoints) {	//clear newPointsLists
				list.clear();
			}
			sendCoordsInQueue();

			numNewPoints = 0;
		}
	}

	private synchronized void sendCoordsInQueue() {
		for(Device toDevice: getDevices()) {
			if(!coordsToSend[getIndex(toDevice)].isEmpty()) {
				System.out.println("Sending to device "+getIndex(toDevice));	//debug
				InetSocketAddress sockadd = new InetSocketAddress(((DeviceHandleIP) toDevice.getHandle()).getIP().getHostName(), ((DeviceHandleIP) toDevice.getHandle()).getPort());
				DataConnectionManager.sendCoordsToDevice(connections[getIndex(toDevice)], coordsToSend[getIndex(toDevice)]);
				//sendCoordsToAddress(sockadd, coordsToSend[toDevice.getDeviceID()]);
				coordsToSend[getIndex(toDevice)].clear();
			}
		}
		timeOfLastSend = System.currentTimeMillis();	//reset timer
	}

	@Override
	public synchronized void notifyOfUpdate(Device d, LinkedList<Integer> givenNewPoints) {
		LinkedList<Integer> newPointsList = globalNewPoints.get(getIndex(d));
		newPointsList.addAll(givenNewPoints);
		numNewPoints += givenNewPoints.size();
		sendIfReady();
	}

	private boolean ready() {
		//Note ready is always false when there is just one device in session.
		return (timeOfLastSend + Config.getServerResendPeriodMillis() <= System.currentTimeMillis()) || (numNewPoints>=Config.getServerNewPointsThreshold()) && numDevices()>1;
	}

	public void sendSessionToAllDevices(Session session) {
		SessionPackage pack = new SessionPackage(session);
		for(SessionDeviceConnection conn: sessionSetupConnections) {
			conn.sendSessionPackage(pack);
		}

	}

	public void serviceRequest(int fromID, LinkedList<Integer>[] requestArray) {
		Response[] responses = posStore.fulfillRequest(requestArray);
		//LinkedList<Coords> response = PositionStore.fulfillRequest(requestArray);
		List<Coords> coordsList = Response.getCoordsList(responses);
		respondToNetwork(fromID, coordsList);
		
		/*
		 * This is server code, so since server doesnt have the "remainder" data,
		 * only the generator of it does, so send each device a request asking
		 * for just their contribution.
		 */
		
		//initialise array with empty lists
		LinkedList<Integer>[] newRequestArray = new LinkedList[numDevices()];
		for(int i=0; i<numDevices(); i++) {
			newRequestArray[i] = new LinkedList<Integer>();
		}
		
		//clear all lists so only send one devices request:
		for(int i=0; i<numDevices(); i++) {
			for(int j=0; j<numDevices(); j++) {
				newRequestArray[j].clear();
			}
			newRequestArray[i] = responses[i].remainingPoints;
			
			//issue new request to culprit to get the remaining points.
			Device toDevice = getDevice(i);
			InetSocketAddress sockAdd = new InetSocketAddress(((DeviceHandleIP) toDevice.getHandle()).getIP().getHostName(), ((DeviceHandleIP) toDevice.getHandle()).getPort());
			try {
				byte[] data = DataConnectionManager.createRequestMessageWithAddress(this, sockAdd, newRequestArray);
				//DatagramPacket datagram = ServerMessage.createRequestMessageWithAddress(sockAdd, newRequestArray);
				if(data != null) {
					connections[getIndex(toDevice)].sendGeneric(data, data.length);
					//NetworkInterface.getInstance().sendDatagram(datagram);
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//note this doesnt wait before sending. Will respond as soon as it can.
	private synchronized void respondToNetwork(int fromID, List<Coords> response) {
		coordsToSend[fromID].addAll(response);
		sendCoordsInQueue();
	}
	
	public void startMainProcessing(Session session) {

		//TODO spawn periodic sending thread
		new Thread(new Runnable() {
			public void run() {
				while(alive) {
					try {
						Thread.sleep(Config.getServerResendPeriodMillis()+100);
						sendIfReady();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		System.out.println("Now listening for data");	//debug
		initDataSockets();
		for(final Device device: getDevices()) {

				new Thread(new Runnable() {
					public void run() {

						byte[] receivingData = new byte[1024];
						
						while(alive) {
							try {
								ByteBuffer bb = DataConnectionManager.receive(connections[getIndex(device)], receivingData);
								System.out.println("Recieved datagram");
								processData(bb);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).start();
		}

		//listen for incoming data and process it:
//		while(true) {
//			ServerMessage.processDatagram(net.receiveDatagram());
//		}
	}
	
	public synchronized void initDataSockets() {
		if(connections == null) {
			connections = new DeviceConnection[numDevices()];
			//TODO make it ProtocolManager.numConnections instead or make it do it or something for server and all.
			for(Device device: getDevices()) {
				try {
					System.out.println("device");
					connections[getIndex(device)] = DeviceConnection.newConnection(device);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
