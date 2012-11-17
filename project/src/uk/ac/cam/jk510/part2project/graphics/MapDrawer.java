package uk.ac.cam.jk510.part2project.graphics;

import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
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
	Paint line = new Paint();
	Paint vertices = new Paint();
	ArrayList<DevicePath> devicePathList = new ArrayList<DevicePath>();
	//TODO initialise this list for all the devices.
	Path[] pathsToDraw;
	Session session = SessionManager.getSession();
	ArrayList<Device> devices;
	boolean[] pathIsNew;
	RectF bounds = new RectF();
	Matrix mat = new Matrix();

	@Deprecated
	public MapDrawer(Context context, Session session) throws IllegalAccessException, InstantiationException {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
		
		this.session = session;
		devices = session.getDevices();
		pathsToDraw = new Path[devices.size()];
		pathIsNew = new boolean[devices.size()];
		devicePathList = new ArrayList<DevicePath>();
		for(Device d: devices) {
			devicePathList.add(d.getDeviceID(), new DevicePath());
		}
		
		PositionStore.subscribeToUpdates(this);
	}
	
	public MapDrawer(Context context, AttributeSet att) throws IllegalAccessException, InstantiationException {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
		this.setBackgroundColor(Config.getBackgroundColor());
		
		session = Session.getSession();
		devices = session.getDevices();
		pathsToDraw = new Path[devices.size()];
		pathIsNew = new boolean[devices.size()];
		for(Device d: devices) {
			devicePathList.add(d.getDeviceID(), new DevicePath());
		}
		PositionStore.subscribeToUpdates(this);
	}
	
	private void updateDeviceTrail(Device device) {
		DevicePath dp = devicePathList.get(device.getDeviceID());
		pathsToDraw[device.getDeviceID()] = dp.makePath();	//replace existing path
		pathIsNew[device.getDeviceID()] = true;
		invalidate();

	}

	@Override
	public void onDraw(Canvas canvas) {

		int cHeight = canvas.getHeight();
		int cWidth = canvas.getWidth();
		float pTop = 0;
		float pBottom = 0;
		float pLeft = 0;
		float pRight = 0;
		
		
		
		//iterate through all the paths to draw, getting the maximum top, bottom, left and right values
		for(Path path: pathsToDraw) {
			
			if(path == null) {	//some paths will be null at first before they have any points added.
				System.err.println("In pathsToDraw this path is null");	//debug
				continue;
			}
			
			path.computeBounds(bounds, true);
			float t = bounds.top;
			float b = bounds.bottom;
			float l = bounds.left;
			float r = bounds.right;
			pTop = t>pTop ? t : pTop;
			pBottom = b>pBottom ? b : pBottom;
			pLeft = l>pLeft ? l : pLeft;
			pRight = r>pRight ? r : pRight;
		}
		//calculate the overall bounds of the paths together
		float pHeight = pBottom - pTop;
		float pWidth = pRight - pLeft;
		System.err.println("pHeight: "+pHeight+" pWidth: "+pWidth);	//debug
		System.err.println("cHeight: "+cHeight+" cWidth: "+cWidth);	//debug
		
		float yScale = cHeight/pHeight;
		float xScale = cWidth/pWidth;
		float scale = yScale<xScale ? yScale : xScale;	//scale = min(xScale, yScale)
		System.err.println("xScale: "+xScale+" yScale: "+yScale);	//debug
		mat.setScale(scale*0.9f, scale*0.9f);	//TODO bad hardcoding here - shouldnt even need to be done???
		int device = 0;
		for(Path path : pathsToDraw) {
			
			if(path == null) {	//some paths will be null at first before they have any points added.
				continue;
			}
			
			//if this path is new, scale it
			if(pathIsNew[device]) {
			path.offset(-bounds.left, -bounds.top);
			path.transform(mat);
			}
			//draw all paths regardless of new or not
			canvas.drawPath(path, line);
			
			device++;
		}
		
		mat.reset();
		
		//TODO make sure the whole scaling thing is done in the right order (probably before drawing to canvas)
		//make protocol manager to drive the whole thing. A real simple single user one, where the sessionManager just doensnt do anything.

	}

	//Called by PositionStore when new points are ready
	public void notifyOfUpdate(Device d, LinkedList<Integer> newPoints) {
		System.err.println("MapDrawer notified of update");	//debug
		//get new points from history,
		LinkedList<Coords> points = new LinkedList<Coords>();
		DevicePath dp = devicePathList.get(d.getDeviceID());
		for(Integer index: newPoints) {
			System.err.println("Now adding index: "+index+" from newPoints");//debug
			Coords coords = PositionStore.getCoord(d, index);
			dp.add(index, coords.getCoord(1), coords.getCoord(2));
		}
		//itake care of PathIsNew and pathsToDraw
		updateDeviceTrail(d);
		
		System.err.println("MapDrawer has finished being notified of update");	//debug
		
	}
	
}
