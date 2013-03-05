import java.util.LinkedList;
import java.util.List;


public class Point {
	double x;
	double y;
	long time;
	List<Point> pointSet;
	Block block;
	
	Point(double x, double y) {
		this.x = x;
		this.y = y;
		this.time = System.currentTimeMillis();
		this.pointSet = new LinkedList<Point>();
		this.block = Grid.getBlock(x,y);
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
}