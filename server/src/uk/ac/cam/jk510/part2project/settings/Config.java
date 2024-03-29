package uk.ac.cam.jk510.part2project.settings;

import uk.ac.cam.jk510.part2project.network.Transport;
import uk.ac.cam.jk510.part2project.protocol.Proto;
import uk.ac.cam.jk510.part2project.protocol.SessionEnum;
import uk.ac.cam.jk510.part2project.store.CoordsType;
import uk.ac.cam.jk510.part2project.store.HistoryType;

public class Config {

	private static final int MinUpdateRedrawSize = 1;	//number of new points to recieve before redrawing
	private static int MapLineThickness = 2;	//thickness of paths drawn in MapDisplayScreen
	private static int arrayBlockSize = 600;		// At 1 per second, thats 10 mins per chunk
	private static int arrayIndexFrequency = 10;	// Finest granularity of points. In samples per 10 sec.
	private static String name = "John";	//Name of local player TODO lookup from OS?
	private static final int keepAlivePeriod = 10000;	//TODO actually use this!!!

	//Colors - not needed for server
	/*
	private static int bgColor = Color.WHITE;	
	private static int[] colors = {Color.BLUE, Color.GREEN, Color.RED};	//TODO add more colours
	private static float posIndicatorSize = 10;	//Radius of circle
	 */

	//Protocol
	private static Proto protocol = Proto.clientServer;
	private static SessionEnum sesh = SessionEnum.bluetooth;
	private static CoordsType coordsType = CoordsType.TXYA;
	private static HistoryType historyType = HistoryType.XYA;
	private static boolean dontSendPointsToOwner = true;
	private static final Transport transportProtocol = Transport.UDP;
	
	//GPS Updates
	private static final int gpsUpdateTime = 0;	//minTime between GPS position updates
	private static final int gpsUpdateDistance = 0;	//min distance between GPS position updates

	//Hard-coded app data
	private static final String UUIDString = "fa87c0d0-afac-11de-8a39-0800200c9a66"; //Randomly created string for use with this app
	private static final String serverIP = "78.150.172.9";
	private static final int serverPort = 60000;
	private static final int defaultClientPort = 60001;

	//Datagram Format
	private static final int nameSize = 1;
	
	//Server Specific
	private static final long serverResendPeriodSecs = 1; //if this
	private static final int serverNewPointsThreshold = 100; //or this is exceeded it will send
	private static final boolean singleSession = true;
	
	//Simulation
	private static final boolean droppingEnabled = false;
	private static final boolean markovPacketDroppingSimulation = true;
	private static boolean currentlyDropping = false;
	private static final double loseConnectionRate = 0.1;	//chance you lose connection in a given second
	private static final double reconnectRate = 0.3;
	private static final double dropRate = 0.2;
	
	//Testing
	private static final boolean serverDuplicationTest = false;
	private static final int duplicationFactor = 125;
	
	//debug
	private static final boolean listenOnly = false;

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
	/*
	 * public static int getBackgroundColor() {
		return bgColor;
	}
	 */
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
	/*
	public static int getColor(int p) {
		return colors[p];
	}
	public static float getPosIndicatorSize() {
		return posIndicatorSize;
	}
	 */
	public static String getServerIP() {
		return serverIP;
	}
	public static int getServerPort() {
		return serverPort;
	}
	public static int getDatagramMetadataSize() {
		return nameSize;	//TODO keep up to date with other metadata fields other than coordinates.
	}
	public static int getNameSize() {
		return nameSize;
	}
	public static long getServerResendPeriodMillis() {
		return serverResendPeriodSecs*1000;
	}
	public static int getServerNewPointsThreshold() {
		return serverNewPointsThreshold;
	}
	public static boolean serverDuplicationTest() {
		return serverDuplicationTest;
	}
	public static int getDuplicationFactor() {
		return duplicationFactor;
	}
	public static boolean singleSession() {
		return singleSession;
	}
	public static int getDefaultClientPort() {
		return defaultClientPort;
	}
	public static boolean listenOnly() {
		return listenOnly;
	}
	public static boolean dontSendPointsToOwner() {
		return dontSendPointsToOwner;
	}
	public static Transport transportProtocol() {
		return transportProtocol;
	}
	public static boolean droppingPackets() {
		if(droppingEnabled) {
			if(markovPacketDroppingSimulation) {

				if(Math.random() <= (currentlyDropping?reconnectRate:loseConnectionRate)) {
					currentlyDropping = !currentlyDropping;
				}
				return currentlyDropping;
			} else {
				if(Math.random() <= dropRate) {
					return true;
				}
			}
		}
		return false;
	}
	public static long getKeepAlivePeriod() {
		return keepAlivePeriod;
	}
}
