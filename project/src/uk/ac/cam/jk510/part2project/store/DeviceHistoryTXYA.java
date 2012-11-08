package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.settings.Config;

public class DeviceHistoryTXYA extends DeviceHistory {

	private int[] newDataPoints;

	private int highestIndex;
	private int blockSize;
	//listOfIntLists contains all of the int Lists used, so that they can be iterated through to update all lists.
	private ArrayList<ArrayList<int[]>> listOfIntLists;

	private ArrayList<int[]> timesList;
	private ArrayList<int[]> xList;
	private ArrayList<int[]> yList;
	private ArrayList<int[]> altList;
	
	private ArrayList<boolean[]> dataPointPresentList;
	
	public DeviceHistoryTXYA() {
		blockSize = Config.getArrayBlockSize();
		newDataPoints = null;

		timesList = new ArrayList<int[]>();
		timesList.add(new int[blockSize]);

		xList = new ArrayList<int[]>();
		xList.add(new int[blockSize]);

		yList = new ArrayList<int[]>();
		yList.add(new int[blockSize]);

		altList = new ArrayList<int[]>();
		altList.add(new int[blockSize]);
		
		
		dataPointPresentList = new ArrayList<boolean[]>();
		dataPointPresentList.add(new boolean[blockSize]);
		
		highestIndex = 0;
		
		listOfIntLists = new ArrayList<ArrayList<int[]>>();
		listOfIntLists.add(timesList);
		listOfIntLists.add(xList);
		listOfIntLists.add(yList);
		listOfIntLists.add(altList);
	}

	@Override
	protected boolean checkClass(Coords coords) {
		return coords.getClass().getName()=="CoordsTXYA";
	}

	@Override
	protected int listSize() {
		return xList.size();
	}

}
