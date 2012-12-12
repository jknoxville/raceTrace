package uk.ac.cam.jk510.part2project.graphics;

import java.util.Map.Entry;

import uk.ac.cam.jk510.part2project.openjdk.TreeMap;
import android.graphics.Path;

public class DevicePath {

	private TreeMap<Integer, Segment> pathCache = new TreeMap<Integer, Segment>();
	private float endX;
	private float endY;
	private int lastIndex = -1;

	protected void add(int index, float x, float y) {
		//Check it's not here already, which it shouldn't be, because the filtering is done by PositionStore
		assert(!pathCache.containsKey(index));
		if(pathCache.containsKey(index) && pathCache.get(index) instanceof CompleteSegment) {
			System.err.println("ERROR: duplicate key at DevicePath.add()");
		}

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
			Entry<Integer, Segment> prevEntry = pathCache.lowerEntry(index);
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
				if(pathCache.higherKey(index)==index+1) {
					//no gap required
				} else {
					//insert gap
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

	//constructor which creates a TreeMap with just one big gap.
	//This same gap object will be shared for all gaps.
	public DevicePath() {
		super();
		pathCache = new TreeMap<Integer, Segment>();
		pathCache.put(0, new GapSegment());
		System.err.println("just initialised DevicePath");

	}

	public Path makePath() {
		System.out.println("makePath()");//debug
		//Set<Entry<Integer, Segment>> entrySet = pathCache.entrySet();
		Path entirePath = new Path();

		System.out.println("makePath() 2");//debug
		//iterate through all Segments in order, adding them to the path
		//int position = -1;
		Entry<Integer, Segment> entry = null;
		System.out.println("makePath() before loop");//debug
		entry = pathCache.firstEntry();	// entry = first entry
		//while there are higher entries, store them in entry and execute loop:
		boolean firstCompleteSegment = true;	//this flag is set so the first complete segment can be identified and
		//that a line isnt drawn from the origin.
		while(entry!=null) {

			Segment segment = entry.getValue();

			//add the entries path to entirePath if its not a gap:
			if(segment instanceof CompleteSegment) {
				//TODO add optimization that doesn't draw path if its a signle element path
				if(!firstCompleteSegment) {
					entirePath.lineTo(((CompleteSegment)segment).getStartx(), ((CompleteSegment)segment).getStarty());
				} else {
					firstCompleteSegment = false;
					entirePath.moveTo(((CompleteSegment)segment).getStartx(), ((CompleteSegment)segment).getStarty());	//if drawing very first path, move to first point first
				}

				entirePath.addPath(((CompleteSegment)segment).path);
			} else {
				//if it is a gap, add a line from current point to start of next path, if there is a next path, otherwise dont
				Entry<Integer, Segment> nextEntry = pathCache.higherEntry(entry.getKey());
				if(nextEntry == null) {
					//no need to draw line since this is the last point
				} else {
					//CompleteSegment nextSegment = (CompleteSegment) nextEntry.getValue();
					//entirePath.lineTo(nextSegment.getStartx(), nextSegment.getStarty());
				}

			}
			entry = pathCache.higherEntry(entry.getKey());
		}

		System.out.println("makePath() after loop");//debug

		return entirePath;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}
}
