package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.SessionManager;


public class PositionStore {

	private ArrayList<DeviceHistory> masterHistory;
	private static PositionStore instance;
	
	private PositionStore() {
		//Singleton Pattern, private constructor
	}
	
	public static PositionStore getInstance() {
		if(instance == null) {
			instance = new PositionStore();
		}
		return instance;
	}
	
	public LinkedList<Integer> getNewDataPoints(int device) {
		DeviceHistory h = masterHistory.get(device);
		return h.getNewDataPoints();
	}
}
