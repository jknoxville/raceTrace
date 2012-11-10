package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.settings.Config;

public class DeviceHistoryXYA extends DeviceHistory {

	private int blockSize;
	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	private ArrayList<ArrayList<int[]>> listOfIntLists;

	private ArrayList<int[]> xList;
	private ArrayList<int[]> yList;
	private ArrayList<int[]> altList;
	
	private ArrayList<boolean[]> dataPointPresentList;
	
	public DeviceHistoryXYA() {
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
	}

	@Override
	protected int historyLength() {
		return xList.size();
	}

}
