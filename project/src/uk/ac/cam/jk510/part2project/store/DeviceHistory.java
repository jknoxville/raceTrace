package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.settings.Config;

public abstract class DeviceHistory {
	
	protected int blockSize;
	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	protected ArrayList<ArrayList<int[]>> listOfIntLists;
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
		int index = coords.lClock;
		System.err.println(this);	//debug
		System.err.println(listOfIntLists);	//debug
		while(!(index<historyLength())) {
			System.err.println("Allocating new block, index: "+index+" historyLength: "+historyLength());	//debug
			for(ArrayList<int[]> l: listOfIntLists) {
				l.add(new int[blockSize]);
			}
			dataPointPresentList.add(new boolean[blockSize]);
		}
		
		//Calculate which array and the offset within it.
		int arrayNumber = arrayNumber(index);
		int offset = offset(index);
		
		//Check to see if already have data for this time range.
		if(dataPointPresentList.get(arrayNumber)[offset]) {
			throw new DataPointPresentException();
		}
		
		// Let i = the coordinate dimension. Store each coordinate in corresponding array.
		for(int i = 0; i<coords.getSize(); i++) {
			ArrayList<int[]> l = listOfIntLists.get(i);
			l.get(arrayNumber)[offset] = coords.getCoord(i);
		}
		
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
		ProtocolManager mgr = ProtocolManager.getProtocolManager();
		if(mgr == null) {//debug
			System.err.println("mgr is null");
		}
		HistoryType historyType = Config.getHistoryType();
		DeviceHistory history = null;
		switch(historyType) {
		case XYA: history = new DeviceHistoryXYA();
		break;
		default: try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return history;
	}
}