package uk.ac.cam.jk510.part2project.store;


public abstract class Coords {
	
	protected int aboutDevice;
	protected float[] coords;
	protected int lClock;	//the logical clock value the coordinate represents. (ie distance along path)
	public CoordsType coordsType;
	
	public int getLClock() {
		return lClock;
	}
	
	public int getSize() {
		return coords.length;
	}
	public float getCoord(int dimension) {
		return coords[dimension];
	}
	public int getDevice() {
		return aboutDevice;
	}
	
}
