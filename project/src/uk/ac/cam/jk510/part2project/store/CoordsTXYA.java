package uk.ac.cam.jk510.part2project.store;

public class CoordsTXYA extends Coords {
	
	
	public CoordsTXYA(int lTime, int x, int y, int alt) {
		this.lClock = lTime;
		coords = new int[3];
		coords[0] = x;
		coords[1] = y;
		coords[2] = alt;
	}

}
