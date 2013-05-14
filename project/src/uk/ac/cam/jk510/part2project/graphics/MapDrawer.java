package uk.ac.cam.jk510.part2project.graphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.DevicePath;
import uk.ac.cam.jk510.part2project.store.PositionStore;
import uk.ac.cam.jk510.part2project.store.PositionStoreSubscriber;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MapDrawer extends View implements PositionStoreSubscriber {
	private static MapDrawer instance;
	
	Paint[] lines;
	Paint[] labels;
	ArrayList<DevicePath> devicePathList;
	Path[] pathsToDraw;
	boolean[] pathIsNew;
	float oldScale;
	float oldpTop;
	float oldpLeft;
	//String[] names;
	Session session = Session.getSession();
	Paint vertices = new Paint();
	boolean atLeastOnePointIsOnScreen = false;
	boolean needToRedraw = true;
	RectF bounds = new RectF();
	Matrix mat = new Matrix();

	public MapDrawer(Context context, AttributeSet att) throws IllegalAccessException, InstantiationException {
		super(context);
		instance = this;
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
		this.setBackgroundColor(Config.getBackgroundColor());
		devicePathList = new ArrayList<DevicePath>();
		reset();
		System.out.println("DEVICEPATH SIZE: "+devicePathList.get(0).getPathCache().size());
	}

	private void initPaint(int p) {
		Paint paint = lines[p];
		paint.setStrokeWidth(Config.getMapLineThickness());
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Config.getColor(p));
		Paint label = labels[p];
		label.setTextSize(30);
		label.setStyle(Paint.Style.FILL);
		label.setColor(Config.getColor(p));
	}

	//Resets all state to new state GCing the old state, should be called whenever a new session starts.
	public synchronized void reset() {
		session = Session.getSession();
		Collection<Device> devices = session.getDevices();
		pathsToDraw = new Path[devices.size()];
		pathIsNew = new boolean[devices.size()];
		lines = new Paint[devices.size()];
		labels = new Paint[devices.size()];
		//names = session.getDeviceNames();
		for(int device=0; device<devices.size(); device++) {
			lines[device] = new Paint();
			labels[device] = new Paint();
			initPaint(device);
		}
		devicePathList.clear();
		if(devicePathList.size() != 0) System.out.println("ERROR: devicepathlist not empty");	//debug
		for(Device d: devices) {
			DevicePath dp = new DevicePath();
			d.setDevicePath(dp);
			devicePathList.add(d.getDeviceID(), dp);
		}
		System.out.println("1st devicePath: "+devicePathList.get(0));
		PositionStore.subscribeToUpdates(this);
	}

	private synchronized void updateDeviceTrail(Device device) {
		System.out.println(device.getDeviceID());
		DevicePath dp = devicePathList.get(device.getDeviceID());
		pathsToDraw[device.getDeviceID()] = dp.makePath();	//replace existing path
		pathsToDraw[device.getDeviceID()].computeBounds(bounds, true);	//debug
		System.out.println("updatingDeviceTrail, path.top = "+bounds.top);	//debug
		pathIsNew[device.getDeviceID()] = true;
		atLeastOnePointIsOnScreen = true;
		needToRedraw = true;
	}

	@Override
	public synchronized void onDraw(Canvas canvas) {

		float pTop = Float.POSITIVE_INFINITY;
		float pBottom = 0;
		float pLeft = Float.POSITIVE_INFINITY;
		float pRight = 0;

		int cHeight = getHeight();
		int cWidth = getWidth();

		//iterate through all the paths to draw, getting the maximum top, bottom, left and right values
		int pathNumber = 0;
		for(Path path: pathsToDraw) {

			if(path == null || path.isEmpty()) {	//dont want empty paths to influence bounds
				System.err.println("In pathsToDraw this path is null");	//debug
				continue;
			}

			/*
			 * pTop : top of path, pLeft : left side of path etc.
			 * Since no points are deleted pTop should only ever decrease (get higher). and similar for pLeft, pRight...
			 */
			path.computeBounds(bounds, true);
			float t = bounds.top;
			float b = bounds.bottom;
			float l = bounds.left;
			float r = bounds.right;
			//pTop should be the smallest non zero value of t. If pTop is 0, then have just started, so initialise it.
			pTop = (t<pTop) ? t : pTop;
			pBottom = b>pBottom ? b : pBottom;
			pLeft = (l<pLeft) ? l : pLeft;
			pRight = r>pRight ? r : pRight;
			System.out.println("PathsToDraw["+pathNumber+"] t: "+t+"b: "+b);	//debug
			pathNumber++;
		}

		//calculate the overall bounds of all the paths
		float pHeight = pBottom - pTop;
		float pWidth = pRight - pLeft;

		float yScale = cHeight/pHeight;
		float xScale = cWidth/pWidth;
		float scale = yScale<xScale ? yScale : xScale;	//scale = min(xScale, yScale)
		mat.setScale(scale, scale);
		int device = 0;
		for(Path path : pathsToDraw) {

			if(path == null) {	//some paths will be null at first before they have any points added.
				continue;
			}

			//rescale all paths everytime, because if any path scale changes, they all do.
			path.offset(-pLeft, -pTop);	//changed from bounds.left and bounds.top
			path.transform(mat);

			//draw all paths regardless of new or not
			canvas.drawPath(path, lines[device]);

			//draw position Indicators
			DevicePath dp = devicePathList.get(device);
			if(needToRedraw) {
				oldScale = scale;
				oldpTop = pTop;
				oldpLeft = pLeft;
				drawPositionIndicator(canvas, dp, pLeft, pTop, scale, device);
				needToRedraw = false;
			} else {
				drawPositionIndicator(canvas, dp, pLeft, pTop, scale, device);
				/*^^^^^^^^^^^
				 * onDraw sometimes gets called when it doesnt need to be.
				 * This means the paths are already scaled and right for the screen to be drawn,
				 * so onDraw calculates scale to be ~1
				 * It then plots the lines perfectly, but for the circle, since it uses data from devicePath
				 * not path, it isnt already scaled, so must use the old scaling values.
				 */
			}
			device++;
		}
		mat.reset();
	}

	private void drawPositionIndicator(Canvas canvas, DevicePath dp, float pLeft, float pTop, float scale, int device) {
		canvas.drawCircle((dp.getPositionX()-oldpLeft)*oldScale, (dp.getPositionY()-oldpTop)*oldScale, Config.getPosIndicatorSize(), labels[device]);
		//canvas.drawText(names[device], 0, Config.charsOfNameToShow(), (dp.getPositionX()-pLeft)*scale+(Config.getPosIndicatorSize()*2), (dp.getPositionY()-pTop)*scale, labels[device]);
	}

	//Called by PositionStore when new points are ready
	public synchronized void notifyOfUpdate(Device d, LinkedList<Integer> newPoints) {
		System.err.println("MapDrawer notified of update");	//debug
		//get new points from history
		DevicePath dp = devicePathList.get(d.getDeviceID());
		for(Integer index: newPoints) {
			System.err.println("Now adding index: "+index+" from newPoints on device "+d.getDeviceID());//debug
			Coords coords = PositionStore.getCoord(d, index);
			dp.add(index, coords.getCoord(0), coords.getCoord(1));
		}
		//take care of PathIsNew and pathsToDraw

		//quick fix: always remake all path objects before scaling so they are all scaled correctly.
		//TODO make this better. its not efficient to redo all paths on every new update
		for(Device dev: Session.getSession().getDevices()) {
			System.out.println(dev.getDeviceID());
			updateDeviceTrail(dev);
		}
		this.post(new Runnable() {	//do invalidate() in UI thread
			public void run() {
				invalidate();
			}
		});
	}
	
	public static void destroy() {
		PositionStore.unsubscribe(instance);
		for(Device d: instance.session.getDevices()) {
			d.nullifyDevicePath();
		}
		instance = null;
	}

	public static Bitmap getScreenShot() {
		if(!instance.isDrawingCacheEnabled()) {
			instance.setDrawingCacheEnabled(true);
		}
		Bitmap bm = instance.getDrawingCache();
		return bm;
	}

	public static boolean initialised() {
		return instance != null;
	}

}
