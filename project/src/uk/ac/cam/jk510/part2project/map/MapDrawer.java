package uk.ac.cam.jk510.part2project.map;

import uk.ac.cam.jk510.part2project.settings.Config;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MapDrawer extends View {
	Paint line = new Paint();
	Paint vertices = new Paint();
	
	private void drawSmoothLines(Canvas canvas, float[] pts, Paint linePaint) {
		//TODO: create vertexPaint here, so it doesnt have to be passed in.
		canvas.drawLines(pts, linePaint);
		
		float radius = linePaint.getStrokeWidth()/2;
		Paint vertexPaint = new Paint();
		vertexPaint.setColor(linePaint.getColor());
		//strokeWidth is already 1 by default
		
		for(int i=0; i+1<pts.length; i+=2) {
			canvas.drawCircle(pts[i], pts[i+1], radius, vertexPaint);
		}
		
	}

	public MapDrawer(Context context) {
		super(context);
		line.setStrokeWidth(Config.getMapLineThickness());
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLACK);
		vertices.setStyle(Paint.Style.FILL);
		vertices.setColor(Color.BLACK);
	}

	@Override
	public void onDraw(Canvas canvas) {
//		canvas.drawLine(0, 0, 20, 20, line);
//		canvas.drawLine(20, 20, 20, 50, line);
//		canvas.drawCircle(20, 20, Config.getMapLineThickness()/2, vertices);
//		
//		canvas.drawLine(100, 100, 150, 130, line);
//		canvas.drawLine(150, 130, 50, 250, line);
//		canvas.drawCircle(150, 130, Config.getMapLineThickness()/2, vertices);
		
		float[] pts = {0,0,20,20,20,20,20,50,20,50,100,100,100,100,150,130,150,130,50,250,50,250,200,500};
		drawSmoothLines(canvas, pts, line);

	}
	
}
