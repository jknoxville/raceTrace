package uk.ac.cam.jk510.part2project.settings;

import java.util.LinkedList;
import java.util.Random;

import uk.ac.cam.jk510.part2project.network.Transport;
import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.session.SessionEnum;
import uk.ac.cam.jk510.part2project.store.Coords;
import uk.ac.cam.jk510.part2project.store.CoordsType;
import uk.ac.cam.jk510.part2project.store.HistoryType;
import android.graphics.Color;

public class Config {

	private static final int MinUpdateRedrawSize = 1;	//number of new points to recieve before redrawing
	private static int MapLineThickness = 2;	//thickness of paths drawn in MapDisplayScreen
	private static int arrayBlockSize = 600;		// At 1 per second, thats 10 mins per chunk
	private static int arrayIndexFrequency = 10;	// Finest granularity of points. In samples per 10 sec.
	//private static String name = "John";	//Name of local player TODO lookup from OS?
	private static String name = String.valueOf(Math.random());	//TODO temp fix: random name for each device
	private static final int keepAlivePeriod = 10000;
	private static final int minCoordsPerPacket = 5;
	
	//Missing Data / Requests
	private static final int missingDataThreshold = 1;	//number of missing points before request is sent to other devices.
	private static final int missingDataTimeThreshold = 10*1000;	//millisec max time between checks for missing data.
	private static final int missingDataCheckThreshold = 10;	//number of network points received that triggers missing check.
	
	//Colors
	private static int bgColor = Color.WHITE;	
	private static int[] colors = {Color.BLUE, Color.GREEN, Color.RED};	//TODO add more colours
	private static float posIndicatorSize = 10;	//Radius of circle
	
	//Protocol
	private static Proto protocol = Proto.clientServer;
	private static SessionEnum sesh = SessionEnum.singleSession;
	private static CoordsType coordsType = CoordsType.TXYA;
	private static HistoryType historyType = HistoryType.XYA;
	private static final boolean localOnly = (protocol == Proto.p2p);	//Only operate over local network. (e.g wifi) - so dont use external IP addresses.
	private static final Transport transportProtocol = Transport.UDP;
	
	//GPS Updates
	private static final int gpsUpdateTime = 0;	//minTime between GPS position updates
	private static final int gpsUpdateDistance = 0;	//min distance between GPS position updates
	
	//Hard-coded app data
	private static final String UUIDString = "fa87c0d0-afac-11de-8a39-0800200c9a66"; //Randomly created string for use with this app

	private static final String localServerIP = "192.168.137.1";
	private static final String globalServerIP = "jknoxville.no-ip.org";
	private static final int serverPort = 60000;
	private static final int defaultClientPort = 60001;
	
	
	//Datagram Format
	private static final int nameSize = 4;
	
	//Debug
	private static final boolean debugModeOn = true;
	/*
	 * Send all p2p packets to server as well
	 */
	
	//Getter methods
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
	public static float getPosIndicatorSize() {
		return posIndicatorSize;
	}
	public static String getServerIP() {
		return (localOnly?localServerIP:globalServerIP);
	}
	public static int getServerPort() {
		return serverPort;
	}
	public static int getDefaultClientPort() {
		return defaultClientPort;
	}
	public static int getDatagramMetadataSize() {
		return nameSize;	//TODO keep up to date with other metadata fields other than coordinates.
	}
	public static int getNameSize() {
		return nameSize;
	}
	public static void setName(String name2) {
		name = name2;
		
	}
	public static long getKeepAlivePeriod() {
		return keepAlivePeriod;
	}
	public static boolean debugMode() {
		return debugModeOn;
	}
	public static int getMinCoordsPerPacket() {
		return minCoordsPerPacket;
	}
	public static Transport transportProtocol() {
		return transportProtocol;
	}
	public static int missingDataThreshold() {
		return missingDataThreshold;
	}
	public static long missingCheckTimer() {
		return missingDataTimeThreshold;
	}
	public static long missingDataCheckThreshold() {
		return missingDataCheckThreshold;
	}
}
