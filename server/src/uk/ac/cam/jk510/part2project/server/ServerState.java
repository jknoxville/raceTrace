package uk.ac.cam.jk510.part2project.server;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;


public class ServerState implements PositionStoreSubscriber {

	private static ArrayList<LinkedList<Integer>> globalNewPoints = new ArrayList<LinkedList<Integer>>();
	private static boolean initialised = false;
	private static long timeOfLastSend=0;
	private static int numNewPoints=0;

	public synchronized static void sendIfReady() {
		init();
		if(ready()) {
			//TODO send the new points

			for(LinkedList<Integer> list: globalNewPoints) {	//clear newPointsLists
				list.clear();
			}
			numNewPoints = 0;
			timeOfLastSend = System.currentTimeMillis();	//reset timer

		}
	}

	@Override
	public synchronized void notifyOfUpdate(Device d, LinkedList<Integer> givenNewPoints) {
		init();	//check state is initisalised
		LinkedList<Integer> newPointsList = globalNewPoints.get(d.getDeviceID());
		newPointsList.addAll(givenNewPoints);
		numNewPoints += givenNewPoints.size();
		sendIfReady();
	}

	private static void init() {
		if(!initialised) {
			PositionStore.subscribeToUpdates(new ServerState());	//subscribe to updates
			for(Device d: Session.getSession().getDevices()) {	//initialise lists
				globalNewPoints.add(d.getDeviceID(), new LinkedList<Integer>());
			}
			initialised = true;
		}
	}
	private static boolean ready() {
		return (timeOfLastSend + Config.getServerResendPeriodMillis() <= System.currentTimeMillis()) || (numNewPoints>Config.getServerNewPointsThreshold());
	}

}
