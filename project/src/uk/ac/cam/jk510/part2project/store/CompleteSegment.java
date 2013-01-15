package uk.ac.cam.jk510.part2project.store;

import uk.ac.cam.jk510.part2project.graphics.Segment;
import android.graphics.Path;

public class CompleteSegment extends Segment {

	protected Path path;
	private float starty;
	private float startx;
	
	public CompleteSegment(float x, float y, Path path) {
		super();
		this.startx = x;
		this.starty = y;
		this.path = path;
	}
	
	public float getStartx() {
		return startx;
	}
	public float getStarty() {
		return starty;
	}
}
