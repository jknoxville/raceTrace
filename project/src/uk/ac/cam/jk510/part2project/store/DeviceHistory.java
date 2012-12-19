package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.settings.Config;

public abstract class DeviceHistory {
	
	protected int blockSize;
	//listOfLists contains all of the float Lists used, so that they can be iterated through to update all lists.
	protected ArrayList<ArrayList<float[]>> listOfLists;
	protected ArrayList<boolean[]> dataPointPresentList;
	protected LinkedList<Integer> newPoints;
	protected CoordsType coordsType;

	protected abstract Coords getCoord(int index);
	
	protected void insert(Coords coords) throws IncompatibleCoordsException, DataPointPresentException {
		//Check that right type of Coords object has been provided
		if (!(checkClass(coords))) {
			System.err.println(coords.coordsType+" "+coordsType);
			throw new IncompatibleCoordsException();
		}
		
		//if index is not within range of currently allocated arrays then allocate until it is.
		int index = coords.getLClock();
		while(!(index<historyLength())) {
			System.err.println("Allocating new block, index: "+index+" historyLength: "+historyLength());	//debug
			for(ArrayList<float[]> l: listOfLists) {
				l.add(new float[blockSize]);
			}
			dataPointPresentList.add(new boolean[blockSize]);
		}
		
		//Calculate which array and the offset within it.
		int arrayNumber = arrayNumber(index);
		int offset = offset(index);
		
		//Check to see if already have data for this time range.
		if(dataPointPresentList.get(arrayNumber)[offset]) {
			System.err.println("Datapoint already present");	//debug
			throw new DataPointPresentException();
		} else {
			System.err.println("Datapoint not present");	//debug
		}
		
		// Let i = the coordinate dimension. Store each coordinate in corresponding array.
		for(int i = 0; i<coords.getSize(); i++) {
			ArrayList<float[]> l = listOfLists.get(i);
			l.get(arrayNumber)[offset] = coords.getCoord(i);
		}
		//Set dataPointPresent value to true
		dataPointPresentList.get(arrayNumber)[offset] = true;
		
		//Add point to device's newPoints list
		newPoints.add(index);
	}
	
	protected int arrayNumber(int index) {
		return index / blockSize;
	}
	
	protected int offset(int index) {
		return index % blockSize;
	}
	
	private boolean checkClass(Coords coords) {
		return coords.coordsType==coordsType;
	}
	
	protected int historyLength() {
		return dataPointPresentList.size()*blockSize;
	}
	
	protected LinkedList<Integer> getNewPoints() {
		return newPoints;
	}
	
	protected void emptyNewPoints() {
		newPoints.clear();
	}

	public static DeviceHistory newHistory() {
//		ProtocolManager mgr = ProtocolManager.getProtocolManager();
//		if(mgr == null) {//debug
//			System.err.println("mgr is null");
//		}
		HistoryType historyType = Config.getHistoryType();
		DeviceHistory history = null;
		switch(historyType) {
		case XYA: history = new DeviceHistoryXYA();
		break;
		default: try {
				throw new Exception();
			} catch (Exception e) {
				// TODO anything in this case?
				e.printStackTrace();
			}
		}
		return history;
	}
}
