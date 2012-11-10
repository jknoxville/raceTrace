package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;


public class PositionStore {

	//private ArrayList<DeviceHistory> masterHistory;
	private static PositionStore instance;
	private LinkedList<PositionStoreSubscriber> subscribers;

	private PositionStore() {
		//Singleton Pattern, private constructor
	}

	public static PositionStore getInstance() {
		if(instance == null) {
			instance = new PositionStore();
		}
		return instance;
	}

	public void insert(Device device, Coords coords) throws IncompatibleCoordsException {

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

	private void notifyObservers(Device d) {
		for(PositionStoreSubscriber s : subscribers) {
			((PositionStoreSubscriber) s).notifyOfUpdate(d.getHistory().getNewPoints());
		}
	}

	public void subscribeToUpdates(PositionStoreSubscriber s) {
		subscribers.add(s);
	}

	private boolean updateReady(Device d) {
		//notify subscribers whenever a device has any new points.
		boolean result = (d.getHistory().getNewPoints().size()>0);
		return result;
	}
}
