package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;

public abstract class Coords {
	
	protected int[] coords;
	int size;
	
	public int getSize() {
		return size;
	}
	public int getCoord(int dimension) {
		return coords[dimension];
	}
	
}
