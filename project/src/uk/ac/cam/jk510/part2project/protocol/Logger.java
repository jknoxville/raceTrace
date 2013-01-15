package uk.ac.cam.jk510.part2project.protocol;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.DataPointPresentException;

public class Logger {

	private static Logger instance;
	private static int blockSize = Config.getArrayBlockSize();

	private long networkDataUpload = 0;
	private long networkDataDownload = 0;
	private long[] timeOfLastReceipt;
	
	protected ArrayList<long[]>[] receiptTimes;	//TODO receipt or display time?
	protected ArrayList<long[]> genTimes;	//time each point was generated

	/*
	 * TODO:
	 * 
	 * --Amount of data sent / recieved
	 * Average, min and max frequency of latest position update over all devices
	 * Max time between updates for any device
	 * Average, Variance and Max time from recording GPS position, to updating screen of another device
	 * Difference between shape of map drawn and real shape
	 * 
	 * write to disc copies of each map, later compare them between devices
	 * 
	 * least squares etc.

	 * If using grouped protocol
	 * 
	 * How map differs from group to group


	 * Discrepancy between reported speed and other stats, if supported, between different devices
	 * Measure of resilience
	 * 
	 * (If network unavailable, can it still function at least in part?)
	 * May use bluetooth, wifi to communicate with near neighbours
	 * Measure by % decline in recieved data points per device when one method fails
	 * 
	 * In simulation, have a function to disable one network temporarily and log new/old receive stats


	 * Measure of Convergence
	 * 
	 * (Does data in all clients eventually become the same?)
	 * Yes / No answer?
	 * Run simulation to find out quantitative measure if not obvious

	 * Ordering of data sent
	 * 
	 * Is most important data prioritized?
	 * Define most important data
	 * 
	 * Race leading position update
	 * Positions of nearest neighbors
	 * Position of last player

	 * Heuristic function
	 * 
	 * Give received packets a data-prioritized score
	 * 
	 * function dependent on GPS-Draw time
	 * S(point, t) = Importance(point)/t
	 * 
	 * Importance(point) = (RaceLeader|Loser)?10:0 + IsNear(point)
	 * IsNear(point) could be median based, or -ve distance...




	 * misc
	 * 
	 * Write images of map samples, for visual comparison in dissertation



	 * 
	 */

	/*
	 * Strategy:
	 * 
	 * Have long[] for each device, when point x arrives set array[x] = time. then have time of arrival of every point. Can use this later with gen / sent arrays of other devices
	 * to work out times between.
	 * 
	 */

	public Logger(Session session) {
		int devices = session.numDevices();
		timeOfLastReceipt = new long[devices];
		receiptTimes = new ArrayList[devices];
		for(int i=0; i<devices; i++) {
			receiptTimes[i] = new ArrayList<long[]>();
			receiptTimes[i].add(new long[blockSize]);
		}
		genTimes = new ArrayList<long[]>();
		genTimes.add(new long[blockSize]);
		
		instance = this;
	}

	public static void download(int bytes) {
		instance.networkDataDownload += bytes;
	}

	public static void upload(int bytes) {
		instance.networkDataUpload += bytes;
	}

	public static void receivedPoint(int aboutDevice, int index) {
		long time = System.currentTimeMillis();
		instance.timeOfLastReceipt[aboutDevice] = System.currentTimeMillis();

		extendArrays(index);

		//Calculate which array and the offset within it.
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;

		//Set dataPointPresent value to true
		ArrayList<long[]> arra = instance.receiptTimes[Session.getDevice(aboutDevice).getDeviceID()];
		long[] array = arra.get(arrayNumber);
		array[offset] = time;

	}
	public static void generatedPoint(int index) {
		long time = System.currentTimeMillis();

		extendArrays(index);

		//Calculate which array and the offset within it.
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;

		//Set dataPointPresent value to true
		instance.genTimes.get(arrayNumber)[offset] = time;

	}
	
	private static void extendArrays(int index) {
		while(!(index<historyLength())) {
			instance.genTimes.add(new long[blockSize]);
			for(int i=0; i<instance.receiptTimes.length; i++) {
				instance.receiptTimes[i].add(new long[blockSize]);
			}
		}
	}

	protected static int historyLength() {
		return instance.genTimes.size()*blockSize;
	}

}