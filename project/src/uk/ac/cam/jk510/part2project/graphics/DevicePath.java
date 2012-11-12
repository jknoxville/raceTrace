package uk.ac.cam.jk510.part2project.graphics;

import java.util.Map.Entry;
import uk.ac.cam.jk510.part2project.openjdk.TreeMap;

public abstract class DevicePath {

	private TreeMap<Integer, Segment> pathCache;
	
	protected void add(int index, int x, int y) throws Exception {
		//Check it's not here already, which it shouldn't be.
		assert(!pathCache.containsKey(index));
		
		pathCache.floorKey(index);
	}
	
}
