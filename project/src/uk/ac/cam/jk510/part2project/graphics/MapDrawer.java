package uk.ac.cam.jk510.part2project.graphics;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class MapDrawer extends View {
	Paint line = new Paint();
	Paint vertices = new Paint();
	ArrayList<DevicePath> devicePathList;
	//TODO initialise this list for all the devices.
	ArrayList<Path> pathsToDraw;
	Session session;
	ArrayList<Device> devices;
	boolean[] pathIsNew;

	public MapDrawer(Context context) {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
		
		session = SessionManager.getSession();
		devices = session.getDevices();
		pathsToDraw = new ArrayList<Path>();
		pathIsNew = new boolean[devices.size()];
	}
	
	private void updateDeviceTrail(Device device) {
		DevicePath dp = devicePathList.get(device.getDeviceID());
		pathsToDraw.add(device.getDeviceID(), dp.makePath());
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		
		RectF bounds = new RectF();
		int cHeight = canvas.getHeight();
		int cWidth = canvas.getWidth();
		float pTop = 0;
		float pBottom = 0;
		float pLeft = 0;
		float pRight = 0;
		
		//iterate through all the paths to draw, getting the maximum top, bottom, left and right values
		for(Path path: pathsToDraw) {
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
		
		//Construct scaling matrix
		Matrix mat = new Matrix();
		float yScale = cHeight/pHeight;
		float xScale = cWidth/pWidth;
		System.err.println("xScale: "+xScale+" yScale: "+yScale);
		mat.setScale(xScale*0.9f, yScale*0.9f);
		
		for(Path path : pathsToDraw) {
			//if this path is new, scale it
			if(pathIsNew[pathsToDraw.indexOf(path)]) {
			path.offset(-bounds.left, -bounds.top);
			path.transform(mat);
			}
			canvas.drawPath(path, line);
		}
		
		//TODO make sure the whole scaling thing is done in the right order (probably before drawing to canvas)
		//make protocol manager to drive the whole thing. A real simple single user one, where the sessionManager just doensnt do anything.

	}
	
}
