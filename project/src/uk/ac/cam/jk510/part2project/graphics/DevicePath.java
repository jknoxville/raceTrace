package uk.ac.cam.jk510.part2project.graphics;

import java.util.Map.Entry;
import java.util.Set;

import uk.ac.cam.jk510.part2project.openjdk.TreeMap;
import android.graphics.Path;

public class DevicePath {

	private TreeMap<Integer, Segment> pathCache = new TreeMap<Integer, Segment>();

	protected void add(int index, int x, int y) {
		//Check it's not here already, which it shouldn't be, because teh filtering is done by PositionStore
		assert(!pathCache.containsKey(index));
		assert(index>0);

		System.err.println("Now inserting index: "+index+" size: "+pathCache.size()+" firstkey: "+pathCache.firstKey());	//debug
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
			Entry prevEntry = pathCache.lowerEntry(index);
//			CompleteSegment prev = (CompleteSegment) pathCache.lowerEntry(index).getValue();
			//if there is a previous segment, add this point to it.
			if(!(prevEntry == null)) {
				CompleteSegment prev = (CompleteSegment) prevEntry.getValue();
				Path path = prev.path;
				path.lineTo(x, y);
			} else {//if this is the very first data point, give it it's own path, starting from x, y.
				assert(index == 0);
				Path path = new Path();
				path.moveTo(x, y);
				CompleteSegment comp = new CompleteSegment(path);
				pathCache.put(index, comp);
			}
		}

		//else split the gap into a first half gap, single point segment, and last half gap.
		else {
			//first half gap
			pathCache.put(gapEntry.getKey(), gap);
			
			//make single item path containing this point
			Path newPath = new Path();
			newPath.lineTo(x, y);
			CompleteSegment seg = new CompleteSegment(newPath);
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
		System.err.println("just put 0 in, first key is "+pathCache.firstKey());
		
	}

	public Path makePath() {
		System.out.println("makePath()");//debug
		Set<Entry<Integer, Segment>> entrySet = pathCache.entrySet();
		Path entirePath = new Path();
		
		System.out.println("makePath() 2");//debug
		//iterate through all Segments in order, adding them to the path
		int position = -1;
		Entry<Integer, Segment> entry = null;
		System.out.println("makePath() before loop");//debug
		entry = pathCache.firstEntry();	// entry = first entry
		//while there are higher entries, store them in entry and execute loop:
		while(entry!=null) {
			
			Segment segment = entry.getValue();
			
			//add the entries path to entirePath if its not a gap:
			if(segment instanceof CompleteSegment) {
				entirePath.addPath(((CompleteSegment)segment).path);
			}
			entry = pathCache.higherEntry(entry.getKey());
		}
		
		System.out.println("makePath() after loop");//debug
		
		return entirePath;
	}

}
