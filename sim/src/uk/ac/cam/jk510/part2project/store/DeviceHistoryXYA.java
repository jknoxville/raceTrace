package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.settings.Config;

public class DeviceHistoryXYA extends DeviceHistory {

	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	//private ArrayList<ArrayList<int[]>> listOfIntLists;

	private ArrayList<float[]> xList;
	private ArrayList<float[]> yList;
	private ArrayList<float[]> altList;
	
	protected synchronized Coords getCoord(int index) {
		int arrayNumber = arrayNumber(index);
		int offset = offset(index);
		assert(dataPointPresentList.get(arrayNumber)[offset]);
		
		float x = xList.get(arrayNumber)[offset];
		float y = yList.get(arrayNumber)[offset];
		float alt = altList.get(arrayNumber)[offset];
		Coords coords = new CoordsTXYA(index, x, y, alt);
		
		return coords;
	}
	
	public DeviceHistoryXYA() {
		super();
		coordsType = CoordsType.TXYA;
		
		blockSize = Config.getArrayBlockSize();

		xList = new ArrayList<float[]>();
		xList.add(new float[blockSize]);

		yList = new ArrayList<float[]>();
		yList.add(new float[blockSize]);

		altList = new ArrayList<float[]>();
		altList.add(new float[blockSize]);
		
		
		dataPointPresentList = new ArrayList<boolean[]>();
		dataPointPresentList.add(new boolean[blockSize]);
		
		listOfLists = new ArrayList<ArrayList<float[]>>();
		listOfLists.add(xList);
		listOfLists.add(yList);
		listOfLists.add(altList);
		
		newPoints = new LinkedList<Integer>();
		
		//TODO move a bunch of the above stuff to the abstract DeviceHistory class as initial values so theyre not in al the constructors.
		
		System.err.println("Just made listOfLists");//debug
		
	}

}
