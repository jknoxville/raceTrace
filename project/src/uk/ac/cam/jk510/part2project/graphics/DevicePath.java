package uk.ac.cam.jk510.part2project.graphics;

import java.util.Map.Entry;
import java.util.Set;

import uk.ac.cam.jk510.part2project.openjdk.TreeMap;
import android.graphics.Path;

public abstract class DevicePath {

	private TreeMap<Integer, Segment> pathCache;

	protected void add(int index, int x, int y) throws Exception {
		//Check it's not here already, which it shouldn't be.
		assert(!pathCache.containsKey(index));

		Entry<Integer, Segment> gapEntry = pathCache.floorEntry(index);
		GapSegment gap = (GapSegment) gapEntry.getValue();

		//if point is at start of a gap, make it the end of the previous segment and move the start of this one.
		if(index == gapEntry.getKey()) {
			//move start of gap one space forward
			pathCache.remove(index);

			//is this point at the end of the gap, making it a single space gap?
			if(pathCache.containsKey(index+1)) {
				//do nothing - no gap needs to be made after this point.
			} else {
				pathCache.put(index+1, gap);
			}

			//add point to previous segment
			CompleteSegment prev = (CompleteSegment) pathCache.lowerEntry(index);
			//if there is a previous segment, add this point to it.
			if(!(prev == null)) {
				Path path = prev.path;
				path.lineTo(x, y);
			} else {//if this is the very first data point, give it it's own path, starting from x, y.
				Path path = new Path();
				path.moveTo(x, y);
			}
		}

		//else split the gap into a first half gap, single point segment, and last half gap.
		else {
			//first half gap
			pathCache.put(gapEntry.getKey(), gap);
			
			//make single item path containing this point
			CompleteSegment seg = new CompleteSegment();
			Path newPath = new Path();
			newPath.lineTo(x, y);
			seg.path = newPath;
			pathCache.put(index, seg);
			
			//if theres space after it, add a gap
			if(pathCache.higherKey(index)==index+1) {
				//no gap required
			} else {
				//insert gap
				pathCache.put(index+1, gap);
			}
		}
	}

	//constructor which creates a TreeMap with just one big gap.
	//This same gap object will be shared for all gaps.
	public DevicePath() {
		super();
		pathCache = new TreeMap<Integer, Segment>();
		pathCache.put(0, new GapSegment());
	}

	public Path makePath() {
		Set<Entry<Integer, Segment>> entrySet = pathCache.entrySet();
		Path entirePath = new Path();
		
		//iterate through all Segments in order, adding them to the path
		int position = -1;
		Entry<Integer, Segment> entry = null;
		//while there are higher entries, store them in entry and execute loop:
		while((entry = pathCache.higherEntry(position))!=null) {
			//add the entries path to entirePath if its not a gap:
			if(entry instanceof CompleteSegment) {
				entirePath.addPath(((CompleteSegment) entry.getValue()).path);
			}
		}
		
		return entirePath;
	}

}
