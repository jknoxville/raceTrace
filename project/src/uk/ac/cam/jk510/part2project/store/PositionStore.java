package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.settings.Config;


public class PositionStore {

	//private ArrayList<DeviceHistory> masterHistory;
	private static PositionStore instance;
	private static LinkedList<PositionStoreSubscriber> subscribers = new LinkedList<PositionStoreSubscriber>();

	private PositionStore() {
		//Singleton Pattern, private constructor
	}

	public static PositionStore getInstance() {
		if(instance == null) {
			instance = new PositionStore();
		}
		return instance;
	}

	public static Coords getCoord(Device d, int index) {
		return d.getHistory().getCoord(index);
	}

	public static void insert(Device device, Coords coords) throws IncompatibleCoordsException {

		try {
			//insert into the deviceHistory object, this method also adds it to it's newPoints.
			(device.getHistory()).insert(coords);

			//check for subscriber notification condition
			if(updateReady(device)) {
				notifyObservers(device);
			}
		} catch (DataPointPresentException e) {	//Already have the dataPoint being inserted
			//TODO Log this?
			//Want to do anything else here?
		}
	}

	//called whenever any device gets some new points.
	private static void notifyObservers(Device d) {
		LinkedList<Integer> newPoints = d.getHistory().getNewPoints();
		for(PositionStoreSubscriber s : subscribers) {
			s.notifyOfUpdate(d, newPoints);
		}
		d.getHistory().emptyNewPoints();
	}

	//externally called by other objects wanting to subscribe
	public static void subscribeToUpdates(PositionStoreSubscriber s) {
		if(!subscribers.contains(s)) {
			subscribers.add(s);
		}
	}

	//Boolean query that returns true when there is significant number of new points to justify plotting.
	private static boolean updateReady(Device d) {
		//notify subscribers whenever a device has any new points.
		boolean result = (d.getHistory().getNewPoints().size()>=Config.getMinUpdateRedrawSize());
		return result;
	}
}
