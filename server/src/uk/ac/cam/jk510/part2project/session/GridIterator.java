package uk.ac.cam.jk510.part2project.session;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class GridIterator<T> implements Iterator<List<Point<T>>> {
	
	Grid grid;
	
	GridIterator(Grid<T> g) {
		grid = g;
	}

	@Override
	public boolean hasNext() {
		return grid.hasNext();
	}

	@Override
	public List<Point<T>> next() {
		List<Point<T>> next = grid.nextGroup();
		if(next == null) {
			throw new NoSuchElementException();
		} else {
			return next;
		}
	}

	@Override
	public void remove() {
		//do nothing - already removed
	}

	public void reset() {
		grid.resetProgress();
	}

}
