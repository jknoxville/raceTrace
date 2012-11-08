package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class DeviceHistory {
	
	private int blockSize;
	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	private ArrayList<ArrayList<int[]>> listOfIntLists;
	private ArrayList<boolean[]> dataPointPresentList;
	private LinkedList<Integer> newDataPoints;

	public void insert(int index, Coords coords) throws IncompatibleCoordsException {
		//Check that right type of Coords object has been provided
		if (!(checkClass(coords))) {
			throw new IncompatibleCoordsException();
		}
		
		//if index is not within range of currently allocated arrays then allocate until it is.
		while(!(index<blockSize*listSize())) {
			for(ArrayList<int[]> l: listOfIntLists) {
				l.add(new int[blockSize]);
			}
			dataPointPresentList.add(new boolean[blockSize]);
		}
		
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;
		
		// Let i = the coordinate dimension. Store each coordinate in corresponding array.
		for(int i = 0; i<coords.getSize(); i++) {
			ArrayList<int[]> l = listOfIntLists.get(i);
			l.get(arrayNumber)[offset] = coords.getCoord(i);
		}
		//whenever a DataPoint is inserted, it's index should be added to newDataPoints so that MapDrawer will know.
		newDataPoints.add(index);
	}
	
	protected abstract boolean checkClass(Coords coords);
	protected abstract int listSize();

	//Wipe newDataPoints and return what was in it.
	public LinkedList<Integer> getNewDataPoints() {
		LinkedList<Integer> result = newDataPoints;
		newDataPoints = null;
		return result;
	}

}
