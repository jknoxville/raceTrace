package uk.ac.cam.jk510.part2project.server;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
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
		return (timeOfLastSend + Config.getServerResendPeriodMillis() <= System.currentTimeMillis()) || (numNewPoints>=Config.getServerNewPointsThreshold());
	}

}
