package uk.ac.cam.jk510.part2project.graphics;

import java.util.Map.Entry;

import uk.ac.cam.jk510.part2project.openjdk.TreeMap;
import android.graphics.Path;

public class DevicePath {

	TreeMap<Integer, Segment> pathCache = new TreeMap<Integer, Segment>();
	private float endX;
	private float endY;
	private float lastXinMadePath;
	private float lastYinMadePath;
	private int lastIndex = -1;

	//add new point to this DevicePath
	protected synchronized void add(int index, float x, float y) {
		//Check it's not here already, which it shouldn't be, because the filtering is done by PositionStore
		assert(!pathCache.containsKey(index));
		if(pathCache.containsKey(index) && pathCache.get(index) instanceof CompleteSegment) {
			System.err.println("ERROR: duplicate key at DevicePath.add()");
		}

		//get target GapSegment
		Entry<Integer, Segment> gapEntry = pathCache.floorEntry(index);
		GapSegment gap = (GapSegment) gapEntry.getValue();

		//if new point is at start of a gap, add it the end of the previous segment and increment the position of this one.
		if(index == gapEntry.getKey()) {
			//move start of gap one space forward
			pathCache.remove(index);

			//if its not at the end of its gap, add a new gap after it
			if(!pathCache.containsKey(index+1)) {
				pathCache.put(index+1, gap);
			}

			//get previous segment
			Entry<Integer, Segment> prevEntry = pathCache.lowerEntry(index);
			
			//if there is a previous segment, add this point to it.
			if(prevEntry != null) {
				CompleteSegment prev = (CompleteSegment) prevEntry.getValue();
				Path path = prev.path;
				path.lineTo(x, y);
			} else {
				//otherwise: this is the very first data point, give it it's own path, starting from x, y.
				assert(index == 0);
				Path path = new Path();
				path.moveTo(x, y);
				CompleteSegment comp = new CompleteSegment(x, y, path);
				pathCache.put(index, comp);
			}
		}

		//else split the gap into a first half gap, single point segment, and last half gap.
		else {
			//first half gap
			pathCache.put(gapEntry.getKey(), gap);

			//make single item path containing this point
			Path newPath = new Path();
			newPath.moveTo(x, y);
			CompleteSegment seg = new CompleteSegment(x, y, newPath);
			pathCache.put(index, seg);

			//check for null, if so, then this is the highest segment yet.
			if(pathCache.higherKey(index)==null) {
				pathCache.put(index+1, gap);
			} else {

				//if theres space after it, add a gap
				if(pathCache.higherKey(index)!=index+1) {
					pathCache.put(index+1, gap);
				}
			}
		}
		if(index>lastIndex) {
			lastIndex = index;
			endX = x;
			endY = y;
		}
	}

	//constructor which creates a TreeMap consisting of just one big gap.
	//This same gap object will be shared for all gaps.
	public DevicePath() {
		super();
		pathCache = new TreeMap<Integer, Segment>();
		pathCache.put(0, new GapSegment());
	}

	public synchronized Path makePath() {
		Path entirePath = new Path();

		lastXinMadePath = endX;
		lastYinMadePath = endY;
		
		//iterate through all Segments in order, adding them to the path
		Entry<Integer, Segment> entry = null;
		entry = pathCache.firstEntry();
		//while there are higher entries, store them in entry and execute loop:
		boolean firstCompleteSegment = true;	
		/*
		 * this flag is set so the first complete segment can be identified
		 *  and a line isnt drawn from the origin.
		 */
		while(entry!=null) {

			Segment segment = entry.getValue();

			//add the entries path to entirePath if its not a gap:
			if(segment instanceof CompleteSegment) {

				if(!firstCompleteSegment) {
					entirePath.lineTo(((CompleteSegment)segment).getStartx(), ((CompleteSegment)segment).getStarty());
				} else {
					firstCompleteSegment = false;
					entirePath.moveTo(((CompleteSegment)segment).getStartx(), ((CompleteSegment)segment).getStarty());	//if drawing very first path, move to first point first
				}
				entirePath.addPath(((CompleteSegment)segment).path);
				
			}
			entry = pathCache.higherEntry(entry.getKey());
		}

		return entirePath;
	}

	public synchronized float getPositionX() {
		return lastXinMadePath;
	}

	public synchronized float getPositionY() {
		return lastYinMadePath;
	}
}
