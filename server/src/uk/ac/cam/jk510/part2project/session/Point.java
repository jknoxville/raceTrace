package uk.ac.cam.jk510.part2project.session;
import java.util.LinkedList;
import java.util.List;

/*
 * A (Key, Value) store where the keys are (x,y) coordinates
 */
public class Point<T> {
	double x;
	double y;
	long time;
	List<Point> pointSet;
	Block block;
	private T value;
	
	Point(Grid grid, double x, double y, T object) {
		this.x = x;
		this.y = y;
		this.time = System.currentTimeMillis();
		this.pointSet = new LinkedList<Point>();
		this.block = grid.getBlock(x,y);
		this.value = object;
	}
	
	boolean olderThan(Point p) {
		return time<p.time;
	}
	void add(Point p) {
		pointSet.add(p);
		if(!p.pointSet.isEmpty()) {
			pointSet.addAll(p.pointSet);
		}
	}
	
	Block getBlock() {
		return block;
	}
	
	static double distanceBetween(Point a, Point b) {
		double dx = a.x-b.x;
		double dy = a.y-b.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public T getObject() {
		return value;
	}
}