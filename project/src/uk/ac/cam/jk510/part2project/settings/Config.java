package uk.ac.cam.jk510.part2project.settings;

import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.protocol.SessionEnum;
import uk.ac.cam.jk510.part2project.store.CoordsType;
import uk.ac.cam.jk510.part2project.store.HistoryType;
import android.graphics.Color;

public class Config {

	private static final int MinUpdateRedrawSize = 1;
	private static int MapLineThickness = 2;
	private static int arrayBlockSize = 600;		// At 1 per second, thats 10 mins per chunk
	private static int arrayIndexFrequency = 10;	// Finest granularity of points. In samples per 10 sec.
	private static String name = "John";
	private static int bgColor = Color.WHITE;
	private static int[] colors = {Color.BLUE, Color.GREEN, Color.RED};	//TODO add more colours
	
	private static Proto protocol = Proto.singleUser;
	private static SessionEnum sesh = SessionEnum.bluetooth;
	private static CoordsType coordsType = CoordsType.TXYA;
	private static HistoryType historyType = HistoryType.XYA;
	
	private static final int gpsUpdateTime = 0;
	private static final int gpsUpdateDistance = 0;
	
	private static final String UUIDString = "fa87c0d0-afac-11de-8a39-0800200c9a66";

	public static int getMapLineThickness() {
		return MapLineThickness;
	}
	public static int getArrayBlockSize() {
		return arrayBlockSize;
	}
	public static int getArrayIndexFreq() {
		return arrayIndexFrequency;
	}
	public static String getName() {
		return name;
	}
	public static int getBackgroundColor() {
		return bgColor;
	}
	public static Proto getProtocol() {
		return protocol;
	}
	public static SessionEnum getSesh() {
		return sesh;
	}
	public static CoordsType getCoordsType() {
		return coordsType;
	}
	public static HistoryType getHistoryType() {
		return historyType;
	}
	public static int getMinUpdateRedrawSize() {
		return MinUpdateRedrawSize;
	}
	public static int getGPSUpdateTime() {
		return gpsUpdateTime;
	}
	public static int getGPSUpdateDistance() {
		return gpsUpdateDistance;
	}
	public static String getUUIDString() {
		return UUIDString;
	}
	public static int getColor(int p) {
		return colors[p];
	}

}
