package uk.ac.cam.jk510.part2project.store;

public class CoordsTXYA extends Coords {
	
	
	public CoordsTXYA(int lTime, float x, float y, float alt) {
		this.lClock = lTime;
		coords = new float[3];
		coords[0] = x;
		coords[1] = y;
		coords[2] = alt;
		coordsType = CoordsType.TXYA;
	}

}
