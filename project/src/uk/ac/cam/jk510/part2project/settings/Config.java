package uk.ac.cam.jk510.part2project.settings;

import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.protocol.SessionEnum;
import uk.ac.cam.jk510.part2project.store.CoordsType;
import uk.ac.cam.jk510.part2project.store.HistoryType;
import android.graphics.Color;

public class Config {

	private static int MapLineThickness = 5;
	private static int arrayBlockSize = 600;		// At 1 per second, thats 10 mins per chunk
	private static int arrayIndexFrequency = 10;	// Finest granularity of points. In samples per 10 sec.
	private static String name = "John";
	private static int bgColor = Color.WHITE;
	private static Proto protocol = Proto.singleUser;
	private static SessionEnum sesh = SessionEnum.singleUser;
	private static CoordsType coordsType = CoordsType.TXYA;
	private static HistoryType historyType = HistoryType.XYA;

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

}
