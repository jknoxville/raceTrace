package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;


public class PositionStore {

	private static LinkedList<PositionStoreSubscriber> subscribers = new LinkedList<PositionStoreSubscriber>();

	private PositionStore() {
		//Singleton Pattern, private constructor
	}

	public static Coords getCoord(Device d, int index) {
		return d.getHistory().getCoord(index);
	}
	
	public static Response[] fulfillRequest(LinkedList<Integer>[] requestArray) {
		Response[] responses = new Response[Session.getSession().numDevices()];
		//add all matching points to coordsList from each device
		for(Device d: Session.getSession().getDevices()) {
			int id = d.getDeviceID();
			responses[d.getDeviceID()] = d.getHistory().fulfillRequest(requestArray[id]);
		}
		return responses;
	}

	public static void insert(int fromDevice, Coords coords) {
		//note fromDevice not used. might want it for logger though.

		try {
			Device aboutDevice = Session.getDevice(coords.getDevice());
			//insert into the deviceHistory object, this method also adds it to it's newPoints.
			(aboutDevice.getHistory()).insert(coords);
			
			//TODO log this

			//check for subscriber notification condition
			if(updateReady(aboutDevice)) {
				notifyObservers(aboutDevice);	//TODO move this to after a bunch of points are inserted. not after each point.
			}
		} catch (DataPointPresentException e) {	//Already have the dataPoint being inserted
			//TODO Log this?
			System.err.println("just threw DataPointPresentException so shouldnt notify mapdrawer.");
			//Want to do anything else here?
		} catch (IncompatibleCoordsException e) {
			e.printStackTrace();
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
