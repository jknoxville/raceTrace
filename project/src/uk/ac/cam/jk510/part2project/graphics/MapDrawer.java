package uk.ac.cam.jk510.part2project.graphics;

import java.util.ArrayList;

import uk.ac.cam.jk510.part2project.session.Device;
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
	ArrayList<Path> devicePaths;

	public MapDrawer(Context context) {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
	}
	
	private void updateDeviceTrail(Device device) {
		DevicePath dp = devicePathList.get(device.getDeviceID());
		devicePaths.add(device.getDeviceID(), dp.makePath());
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		
//		float[] pts = {0,0,20,20,20,20,20,50,20,50,100,100,100,100,150,130,150,130,50,250,50,250,200,500};
//		drawSmoothLines(canvas, pts, line);
		
//		Path path = new Path();
//		path.lineTo(20,20);
//		path.lineTo(20,50);
//		path.lineTo(100,100);
//		path.lineTo(150,130);
//		path.lineTo(50,250);
//		path.lineTo(200,500);
//		canvas.drawPath(path, line);
		
		char[] x = {20,20,100,150,50,78,56,200};
		char[] y = {20,50,100,130,250,33,67,5};
		RectF bounds = new RectF();
		System.err.println("here");
		Path path = new Path();
		
		
		
		System.err.println("here here");
		path.computeBounds(bounds, true);
		System.err.println("here3");
		int cHeight = canvas.getHeight();
		int cWidth = canvas.getWidth();
		float pHeight = bounds.height();
		float pWidth = bounds.width();
		Matrix mat = new Matrix();
		float yScale = cHeight/pHeight;
		float xScale = cWidth/pWidth;
		System.err.println("xScale: "+xScale+" yScale: "+yScale);
		mat.setScale(xScale*0.9f, yScale*0.9f);
		path.offset(-bounds.left, -bounds.top);
		path.transform(mat);
		canvas.drawPath(path, line);

	}
	
}
