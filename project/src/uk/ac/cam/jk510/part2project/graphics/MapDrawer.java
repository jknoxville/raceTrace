package uk.ac.cam.jk510.part2project.graphics;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MapDrawer extends View implements PositionStoreSubscriber {
	//Paint line = new Paint();
	Paint[] lines;
	Paint vertices = new Paint();
	ArrayList<DevicePath> devicePathList = new ArrayList<DevicePath>();
	Path[] pathsToDraw;
	Session session = Session.getSession();
	ArrayList<Device> devices;
	boolean[] pathIsNew;
	boolean atLeastOnePointIsOnScreen = false;

	RectF bounds = new RectF();
	Matrix mat = new Matrix();

	//	@Deprecated
	//	public MapDrawer(Context context, Session session) throws IllegalAccessException, InstantiationException {
	//		super(context);
	//		line.setStrokeWidth(Config.getMapLineThickness());
	//		line.setStyle(Paint.Style.STROKE);
	//		line.setColor(Color.BLACK);
	//		vertices.setStyle(Paint.Style.FILL);
	//		vertices.setColor(Color.BLACK);
	//		
	//		this.session = session;
	//		devices = session.getDevices();
	//		pathsToDraw = new Path[devices.size()];
	//		pathIsNew = new boolean[devices.size()];
	//		devicePathList = new ArrayList<DevicePath>();
	//		for(Device d: devices) {
	//			devicePathList.add(d.getDeviceID(), new DevicePath());
	//		}
	//		
	//		PositionStore.subscribeToUpdates(this);
	//	}

	public MapDrawer(Context context, AttributeSet att) throws IllegalAccessException, InstantiationException {
		super(context);

		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
		this.setBackgroundColor(Config.getBackgroundColor());
		reset();
	}

	private void initPaint(int p) {
		Paint paint = lines[p];
		paint.setStrokeWidth(Config.getMapLineThickness());
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Config.getColor(p));
	}

	//Resets all state to new state GCing the old state, should be called whenever a new session starts.
	public void reset() {
		session = Session.getSession();
		devices = session.getDevices();
		pathsToDraw = new Path[devices.size()];
		pathIsNew = new boolean[devices.size()];
		lines = new Paint[devices.size()];
		for(int device=0; device<devices.size(); device++) {
			lines[device] = new Paint();
			initPaint(device);
		}
		for(Device d: devices) {
			devicePathList.add(d.getDeviceID(), new DevicePath());
		}
		PositionStore.subscribeToUpdates(this);
	}

	private void updateDeviceTrail(Device device) {
		DevicePath dp = devicePathList.get(device.getDeviceID());
		pathsToDraw[device.getDeviceID()] = dp.makePath();	//replace existing path
		pathIsNew[device.getDeviceID()] = true;
		atLeastOnePointIsOnScreen = true;
		invalidate();	//A View method that tells it the view is invalidated so should be drawn again.

	}

	@Override
	public void onDraw(Canvas canvas) {

		//		int cHeight = canvas.getHeight();
		//		int cWidth = canvas.getWidth();

		float pTop = Float.POSITIVE_INFINITY;
		float pBottom = 0;
		float pLeft = Float.POSITIVE_INFINITY;
		float pRight = 0;

		int cHeight = getHeight();
		int cWidth = getWidth();

		//iterate through all the paths to draw, getting the maximum top, bottom, left and right values
		for(Path path: pathsToDraw) {

			if(path == null || path.isEmpty()) {	//dont want empty paths to influence bounds
				System.err.println("In pathsToDraw this path is null");	//debug
				continue;
			}


			path.computeBounds(bounds, true);
			float t = bounds.top;
			float b = bounds.bottom;
			float l = bounds.left;
			float r = bounds.right;
			//pTop should be the smallest non zero value of t. If pTop is 0, then have just started, so initialise it.
			//pTop = (pTop==0 || (t!=0 && t<pTop)) ? t : pTop;	 was this before change from init 0 to init inf
			pTop = (t<pTop) ? t : pTop;
			pBottom = b>pBottom ? b : pBottom;
			pLeft = (l<pLeft) ? l : pLeft;
			pRight = r>pRight ? r : pRight;
			System.out.println("t: "+t+"b: "+b);	//debug
		}
		System.out.println("pTop: "+pTop+" pBottom: "+pBottom+" pLeft: "+pLeft+" pRight: "+pRight);	//debug
		//calculate the overall bounds of all the paths
		float pHeight = pBottom - pTop;
		float pWidth = pRight - pLeft;
		System.err.println("pHeight: "+pHeight+" pWidth: "+pWidth);	//debug
		System.err.println("cHeight: "+cHeight+" cWidth: "+cWidth+"but canvasHeight: "+canvas.getHeight());	//debug

		float yScale = cHeight/pHeight;
		float xScale = cWidth/pWidth;
		float scale = yScale<xScale ? yScale : xScale;	//scale = min(xScale, yScale)
		System.err.println("xScale: "+xScale+" yScale: "+yScale);	//debug
		mat.setScale(scale, scale);	//changed from scale*0.9
		int device = 0;
		for(Path path : pathsToDraw) {

			if(path == null) {	//some paths will be null at first before they have any points added.
				continue;
			}

			//if this path is new, scale it - NO. rescale all paths everytime, because if any path scale changes, they all do.
			//if(pathIsNew[device]) {
			path.offset(-pLeft, -pTop);	//changed from bounds.left and bounds.top
			path.transform(mat);
			//}
			//draw all paths regardless of new or not
			canvas.drawPath(path, lines[device]);

			//draw position Indicators
			DevicePath dp = devicePathList.get(device);
			canvas.drawCircle((dp.getEndX()-pLeft)*scale, (dp.getEndY()-pTop)*scale, Config.getPosIndicatorSize(), lines[device]);

			device++;
		}

		mat.reset();

	}

	//Called by PositionStore when new points are ready
	public void notifyOfUpdate(Device d, LinkedList<Integer> newPoints) {
		System.err.println("MapDrawer notified of update");	//debug
		//get new points from history
		DevicePath dp = devicePathList.get(d.getDeviceID());
		for(Integer index: newPoints) {
			System.err.println("Now adding index: "+index+" from newPoints");//debug
			Coords coords = PositionStore.getCoord(d, index);
			dp.add(index, coords.getCoord(0), coords.getCoord(1));
		}
		//take care of PathIsNew and pathsToDraw
		//updateDeviceTrail(d); // changed to the code below instead:

		//quick fix: always remake all path objects before scaling so they are all scaled correctly.
		//TODO make this better. its not efficient to redo all paths on every new update
		for(Device dev: devices) {
			updateDeviceTrail(dev);
		}

		System.err.println("MapDrawer has finished being notified of update");	//debug

	}

}
