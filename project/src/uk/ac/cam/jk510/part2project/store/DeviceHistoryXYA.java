package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.settings.Config;

public class DeviceHistoryXYA extends DeviceHistory {

	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	//private ArrayList<ArrayList<int[]>> listOfIntLists;

	private ArrayList<int[]> xList;
	private ArrayList<int[]> yList;
	private ArrayList<int[]> altList;
	
	protected Coords getCoord(int index) {
		int arrayNumber = arrayNumber(index);
		int offset = offset(index);
		assert(dataPointPresentList.get(arrayNumber)[offset]);
		
		int x = xList.get(arrayNumber)[offset];
		int y = yList.get(arrayNumber)[offset];
		int alt = altList.get(arrayNumber)[offset];
		Coords coords = new CoordsTXYA(index, x, y, alt);
		
		return coords;
	}
	
	public DeviceHistoryXYA() {
		super();
		coordsType = CoordsType.TXYA;
		
		blockSize = Config.getArrayBlockSize();

		xList = new ArrayList<int[]>();
		xList.add(new int[blockSize]);

		yList = new ArrayList<int[]>();
		yList.add(new int[blockSize]);

		altList = new ArrayList<int[]>();
		altList.add(new int[blockSize]);
		
		
		dataPointPresentList = new ArrayList<boolean[]>();
		dataPointPresentList.add(new boolean[blockSize]);
		
		listOfIntLists = new ArrayList<ArrayList<int[]>>();
		listOfIntLists.add(xList);
		listOfIntLists.add(yList);
		listOfIntLists.add(altList);
		
		newPoints = new LinkedList<Integer>();
		
		//TODO move a bunch of the above stuff to the abstract DeviceHistory class as initial values so theyre not in al the constructors.
		
		System.err.println("Just made listOfIntLists");//debug
		
	}

}
