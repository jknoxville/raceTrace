package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;
import java.util.List;

import uk.ac.cam.jk510.part2project.server.ServerSession;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;


public class PositionStore {

	private LinkedList<PositionStoreSubscriber> subscribers = new LinkedList<PositionStoreSubscriber>();
	private ServerSession servSesh;
	
	public PositionStore(ServerSession sesh) {
		this.servSesh = sesh;
	}

	public Coords getCoord(Device d, int index) {
		return d.getHistory().getCoord(index);
	}
	
	public Response[] fulfillRequest(LinkedList<Integer>[] requestArray) {
		Response[] responses = new Response[servSesh.numDevices()];
		//add all matching points to coordsList from each device
		for(int id=0; id<servSesh.numDevices(); id++) {
			responses[id] = servSesh.getDevice(id).getHistory().fulfillRequest(requestArray[id]);
		}
		return responses;
	}

	public void insert(int fromDevice, Coords coords) {
		//note fromDevice not used. might want it for logger though.

		try {
			Device aboutDevice = servSesh.getDevice(coords.getDevice());
			//insert into the deviceHistory object, this method also adds it to it's newPoints.
			(aboutDevice.getHistory()).insert(coords);
			System.out.println("inserting into device "+aboutDevice.getDeviceID());	//debug
			

			//check for subscriber notification condition
			if(updateReady(aboutDevice)) {
				notifyObservers(aboutDevice);	//TODO move this to after a bunch of points are inserted. not after each point.
			}
		} catch (DataPointPresentException e) {	//Already have the dataPoint being inserted
			//TODO Log this?
		} catch (IncompatibleCoordsException e) {
			e.printStackTrace();
		}
	}

	//called whenever any device gets some new points.
	private void notifyObservers(Device d) {
		LinkedList<Integer> newPoints = d.getHistory().getNewPoints();
		for(PositionStoreSubscriber s : subscribers) {
			s.notifyOfUpdate(d, newPoints);
		}
		d.getHistory().emptyNewPoints();
	}

	//externally called by other objects wanting to subscribe
	public void subscribeToUpdates(PositionStoreSubscriber s) {
		if(!subscribers.contains(s)) {
			subscribers.add(s);
		}
	}

	//Boolean query that returns true when there is significant number of new points to justify plotting.
	private boolean updateReady(Device d) {
		//notify subscribers whenever a device has any new points.
		boolean result = (d.getHistory().getNewPoints().size()>=Config.getMinUpdateRedrawSize());
		return result;
	}
}
