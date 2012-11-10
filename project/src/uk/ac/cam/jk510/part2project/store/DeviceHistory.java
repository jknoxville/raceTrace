package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class DeviceHistory {
	
	private int blockSize;
	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	private ArrayList<ArrayList<int[]>> listOfIntLists;
	private ArrayList<boolean[]> dataPointPresentList;
	private LinkedList<Integer> newPoints;

	protected void insert(Coords coords) throws IncompatibleCoordsException, DataPointPresentException {
		//Check that right type of Coords object has been provided
		if (!(checkClass(coords))) {
			throw new IncompatibleCoordsException();
		}
		
		//if index is not within range of currently allocated arrays then allocate until it is.
		int index = coords.lClock;
		while(!(index<blockSize*historyLength())) {
			for(ArrayList<int[]> l: listOfIntLists) {
				l.add(new int[blockSize]);
			}
			dataPointPresentList.add(new boolean[blockSize]);
		}
		
		//Calculate which array and the offset within it.
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;
		
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
	
	private boolean checkClass(Coords coords) {
		return coords.getClass().equals(this.getClass());
	}
	
	protected abstract int historyLength();
	
	protected LinkedList<Integer> getNewPoints() {
		return newPoints;
	}
	
	protected void emptyNewPoints() {
		newPoints.clear();
	}
}
