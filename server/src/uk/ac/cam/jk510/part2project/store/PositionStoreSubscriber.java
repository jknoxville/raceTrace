package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;

public interface PositionStoreSubscriber {

	public void notifyOfUpdate(Device d, LinkedList<Integer> newPoints);
}