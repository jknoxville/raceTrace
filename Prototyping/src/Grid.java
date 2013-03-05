
import java.util.ArrayList;
import java.util.List;

public class Grid {

	static int width = 10;
	static int height = 10;
	
	static double radius = 4;

	static int xProgress = 0;
	static int yProgress = 0;

	//static ArrayList<ArrayList<Block>> blocks;
	static Block[][] blocks;

	public Grid() {
		//blocks = new ArrayList<ArrayList<Block>>();
		blocks = new Block[100][100];
	}

	static Block getBlock(double xd, double yd) {
		int x = (int) (xd/radius);
		int y = (int) (yd/radius);
		Block[] column;
		if(blocks[x] != null) {
			column = blocks[x];
		} else {
			column = new Block[100];

			blocks[x] = column;
		}


		Block block;
		if(column[y] != null) {
			block = column[y];
		} else {
			block = new Block(x,y);
			column[y] = block;
		}

		return block;
	}

	void insert(final Point p) {
		Block block = getBlock(p.x, p.y);
		block.add(p);
	}

	List<Point> nextGroup() {
		List points = pointsIn4Block(xProgress, yProgress);

		//System.out.println("points in 4block: "+points.size());
		//if this 4block contains any points, process the block, otherwise skip it
		if(points.size()>0) {
			distanceFilter(points);
			Point oldest = getOldest(points);
			Block blockOfOldestPoint = oldest.getBlock();

			//If oldest point is in the top left corner of the 4block, stop the group expanding and return it.
			if((blockOfOldestPoint.x == xProgress && blockOfOldestPoint.y == yProgress)) {
				blockOfOldestPoint.remove(oldest);
				oldest.pointSet.add(oldest);
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
					if(blocks[i][j] == null) {
						continue;
					} else {
						size += blocks[i][j].points.size();
						list.addAll(blocks[i][j].points);
					}
				}

			}
		}
		//System.out.println("points in this 4block("+x+","+y+"): "+list.size()+" should be "+size);
		return list;
	}

	@Deprecated
	Point oldestIn4Block(int x, int y) {
		Point oldest = null;
		for(int i=x; i<x+2; i++) {
			for(int j=y; j<y+2; j++) {
				Point point = blocks[x][y].getOldest();
				if(point.olderThan(oldest) || oldest == null) {
					oldest = point;
				}
			}
		}
		return oldest;
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
}
