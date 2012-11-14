package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;

public abstract class Coords {
	
	protected int[] coords;
	int lClock;	//the logical clock value the coordinate represents. (ie distance along path)
	public CoordsType coordsType;
	
	public int getSize() {
		return coords.length;
	}
	public int getCoord(int dimension) {
		return coords[dimension];
	}
	
}
