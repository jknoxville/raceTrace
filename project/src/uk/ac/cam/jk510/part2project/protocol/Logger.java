package uk.ac.cam.jk510.part2project.protocol;

import java.util.concurrent.TimeoutException;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;

public class Logger {

	private static Logger instance;

	private long networkDataUpload = 0;
	private long networkDataDownload = 0;
	private long[] timeOfLastReceipt;

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

	public Logger(Session session) {
		timeOfLastReceipt = new long[session.numDevices()];
		instance = this;
	}

	public static void download(int bytes) {
		instance.networkDataDownload += bytes;
	}

	public static void upload(int bytes) {
		instance.networkDataUpload += bytes;
	}

	public static void newPoint(Device device) {
		instance.timeOfLastReceipt[device.getDeviceID()] = System.currentTimeMillis();
	}

}