package uk.ac.cam.jk510.part2project.store;

import java.util.ArrayList;


public class CoordsTXYA extends Coords {
	
	
	public CoordsTXYA(int time, int x, int y, int alt) {
		size = 4;
		coords = new int[size];
		coords[0] = time;
		coords[1] = x;
		coords[2] = y;
		coords[3] = alt;
	}

}
