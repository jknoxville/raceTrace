package uk.ac.cam.jk510.part2project.graphics;

import android.graphics.Path;

public class CompleteSegment extends Segment {

	protected Path path;
	private int starty;
	private int startx;
	
	public CompleteSegment(int startx, int starty, Path path) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.path = path;
	}
	
	public int getStartx() {
		return startx;
	}
	public int getStarty() {
		return starty;
	}
}
