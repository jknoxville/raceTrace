package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

public interface PositionStoreSubscriber {

	public void notifyOfUpdate(LinkedList<Integer> newPoints);
}