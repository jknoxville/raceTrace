package uk.ac.cam.jk510.part2project.map;

import uk.ac.cam.jk510.part2project.settings.Config;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

public class MapDrawerTest extends View {
	Paint line = new Paint();
	Paint vertices = new Paint();
	
	//Path used instead
	@Deprecated
	private void drawSmoothLines(Canvas canvas, float[] pts, Paint linePaint) {
		canvas.drawLines(pts, linePaint);
		
		float radius = linePaint.getStrokeWidth()/2;
		Paint vertexPaint = new Paint();
		vertexPaint.setColor(linePaint.getColor());
		//strokeWidth is already 1 by default
		
		for(int i=0; i+1<pts.length; i+=2) {
			canvas.drawCircle(pts[i], pts[i+1], radius, vertexPaint);
		}
	}
	
	private Path createPath(char[] x, char[] y) throws InvalidDataPointException {
		if(x.length!=y.length) {throw new InvalidDataPointException();}
		
		Path path = new Path();
		path.moveTo(x[0], y[0]);
		for(int i=1; i<x.length; i++) {
			path.lineTo(x[i], y[i]);
		}
		return path;
	}

	public MapDrawerTest(Context context) {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
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
		try {			System.err.println("here");
			Path path = createPath(x,y);
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
		} catch (InvalidDataPointException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
