
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Grid<T> {

	int width = 10;
	int height = 10;
	
	double radius = 4;

	int xProgress = 0;
	int yProgress = 0;

	LinkedList<LinkedList<Block>> blocks;

	public Grid() {
		blocks = new LinkedList<LinkedList<Block>>();
	}
	
	public void insert(double x, double y, T value) {
		insert(new Point<T>(this, x, y, value));
	}

	Block getBlock(double xd, double yd) {
		int x = (int) (xd/radius);
		int y = (int) (yd/radius);
		LinkedList<Block> column;
		if(blocks.get(x) != null) {
			column = blocks.get(x);
		} else {
			column = new LinkedList<Block>();
			blocks.add(x, column);
		}


		Block block;
		if(column.get(y) != null) {
			block = column.get(y);
		} else {
			block = new Block(x,y);
			column.add(y, block);
		}

		return block;
	}

	void insert(final Point p) {
		Block block = getBlock(p.x, p.y);
		block.add(p);
	}

	List<Point> nextGroup() {
		List points = pointsIn4Block(xProgress, yProgress);

		//if this 4block contains any points, process the block, otherwise skip it
		if(points.size()>0) {
			distanceFilter(points);
			Point oldest = getOldest(points);
			Block blockOfOldestPoint = oldest.getBlock();

			//If oldest point is in the top left corner of the 4block, stop the group from expanding and return it.
			if((blockOfOldestPoint.x == xProgress && blockOfOldestPoint.y == yProgress)) {
				oldest.pointSet.add(oldest);
				blockOfOldestPoint.removeAll(oldest);
				return oldest.pointSet;
			}
		}
		return null;

	}

	//returns true if there are more blocks to traverse.
	boolean advance() {
		if(yProgress < height) {
			yProgress++;
		} else {
			if(xProgress < width) {
				yProgress = 0;
				xProgress ++;
			} else {
				return false;
			}
		}
		return true;
	}

	ArrayList<Point> pointsIn4Block(int x, int y) {
		ArrayList<Point> list = new ArrayList<Point>();
		int size=0;
		for(int i=x; i<x+2; i++) {
			for(int j=y; j<y+2; j++) {
				if(i<width&&j<height) {
					if(blocks.get(i) == null || blocks.get(i).get(j) == null) {
						continue;
					} else {
						size += blocks.get(i).get(j).points.size();
						list.addAll(blocks.get(i).get(j).points);
					}
				}

			}
		}
		//System.out.println("points in this 4block("+x+","+y+"): "+list.size()+" should be "+size);
		return list;
	}

	Point getOldest(List<Point> points) {
		Point oldest = null;
		for(Point point: points) {
			if(oldest == null || point.olderThan(oldest)) {
				oldest = point;
			}
		}
		return oldest;
	}

	void distanceFilter(List<Point> points) {

		Point oldest = getOldest(points);

		for(Point otherPoint: points) {
			if(otherPoint == oldest) continue;
			if(Point.distanceBetween(oldest, otherPoint) < 10) {
				oldest.add(otherPoint);
				otherPoint.getBlock().remove(otherPoint);
			}
		}
	}
	
	void resetPosition() {
		xProgress = 0;
		yProgress = 0;
	}
}
