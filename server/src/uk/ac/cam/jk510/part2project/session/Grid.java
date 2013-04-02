package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grid<T> implements Iterable {

	int width = 10;
	int height = 10;
	
	double radius = 4;

	int xProgress = 0;
	int yProgress = 0;

	HashMap<Integer, HashMap<Integer, Block>> blocks;

	public Grid() {
		blocks = new HashMap<Integer, HashMap<Integer, Block>>();
	}
	
	public void insert(double x, double y, T value) {
		insert(new Point<T>(this, x, y, value));
	}

	Block getBlock(double xd, double yd) {
		int x = (int) (xd/radius);
		int y = (int) (yd/radius);
		HashMap<Integer, Block> column;
		if(blocks.get(x) != null) {
			column = blocks.get(x);
		} else {
			column = new HashMap<Integer, Block>();
			blocks.put(x, column);
		}


		Block block;
		if(column.get(y) != null) {
			block = column.get(y);
		} else {
			block = new Block(x,y);
			column.put(y, block);
		}

		return block;
	}

	synchronized void insert(final Point p) {
		Block block = getBlock(p.x, p.y);
		block.add(p);
	}
	
	public GridIterator<T> iterator() {
		return new GridIterator<T>(this);
	}

	synchronized List<Point<T>> nextGroupFromCurrentBlock() {
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

	//returns true if there are more blocks to traverse
	//and moves the cursors to the next block if so.
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
	
	boolean hasNext() {
		List<Point<T>> next = nextGroupFromCurrentBlock();
		while(next == null && advance() == true) {
			next = nextGroupFromCurrentBlock();
		}
		return (next != null);
	}
	
	List<Point<T>> nextGroup() {
		if(hasNext()) {
			return nextGroupFromCurrentBlock();
		} else {
			return null;
		}
	}

	ArrayList<Point> pointsIn4Block(int x, int y) {
		ArrayList<Point> list = new ArrayList<Point>();
		int size=0;
		for(int i=x; i<x+2; i++) {
			for(int j=y; j<y+2; j++) {
				if(i<width&&j<height) {
					if(!blocks.containsKey(i) || !blocks.get(i).containsKey(j)) {
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

	public void resetProgress() {
		xProgress = 0;
		yProgress = 0;
	}
}
