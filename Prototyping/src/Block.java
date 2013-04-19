import java.util.concurrent.LinkedBlockingQueue;


public class Block {
	
	int x;
	int y;
	LinkedBlockingQueue<Point> points;
	
	Block(int x, int y) {
		this.x = x;
		this.y = y;
		points = new LinkedBlockingQueue<Point>();
	}
	
	void add(Point p) {
		points.add(p);
	}
	
	void remove(Point p) {
		points.remove(p);
	}
	
	void removeAll(Point p) {
		points.removeAll(p.pointSet);
	}
	
	Point getOldest() {
		return points.peek();
	}

}
