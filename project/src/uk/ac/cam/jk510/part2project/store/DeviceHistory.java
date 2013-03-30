package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.protocol.Logger;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.settings.Config;

public abstract class DeviceHistory {

	protected int blockSize;
	//listOfLists contains all of the float Lists used, so that they can be iterated through to update all lists.
	protected ArrayList<ArrayList<float[]>> listOfLists;
	protected ArrayList<boolean[]> dataPointPresentList;
	protected LinkedList<Integer> newPoints;
	protected CoordsType coordsType;
	protected int device;
	protected int indexOfLatestPoint;
	
	private HashSet<Integer> absentSet = new HashSet<Integer>();

	protected abstract Coords getCoord(int index);

	protected synchronized void insert(Coords coords) throws IncompatibleCoordsException, DataPointPresentException {
		//Check that right type of Coords object has been provided
		if (!(checkClass(coords))) {
			System.err.println(coords.coordsType+" "+coordsType);
			throw new IncompatibleCoordsException();
		}

		//if index is not within range of currently allocated arrays then allocate until it is.
		int index = coords.getLClock();

		//TODO Sanity check here before allocation.
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
		}
		
		

		// Let i = the coordinate dimension. Store each coordinate in corresponding array.
		for(int i = 0; i<coords.getSize(); i++) {
			ArrayList<float[]> l = listOfLists.get(i);
			l.get(arrayNumber)[offset] = coords.getCoord(i);
		}
		//Set dataPointPresent value to true
		dataPointPresentList.get(arrayNumber)[offset] = true;
		
		
		if(absentSet.contains(index)) {
			absentSet.remove(index);
		}
		
		
		
		
		if(index > indexOfLatestPoint) {
			for(int i=indexOfLatestPoint+1; i<=index; i++) {
				absentSet.add(i);
			}
			indexOfLatestPoint = index;
			Logger.newLatestPoint(coords.aboutDevice);
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

	public static DeviceHistory newHistory(int device) {
		HistoryType historyType = Config.getHistoryType();
		DeviceHistory history = null;
		switch(historyType) {
		case XYA: history = new DeviceHistoryXYA(device);
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
	
	public Collection<Integer> getAbsentList() {
		return absentSet;
	}

	public Response fulfillRequest(LinkedList<Integer> list) {
		Response response = new Response();
		for(Integer index: list) {
			//Calculate which array and the offset within it.
			int arrayNumber = arrayNumber(index);
			int offset = offset(index);

			//if have this point then get it and add it to the return list
			if(dataPointPresentList.get(arrayNumber)[offset]) {
				response.hits.add(getCoord(index));
			} else {
				response.remainingPoints.add(index);
			}
		}
		return response;
	}
}
