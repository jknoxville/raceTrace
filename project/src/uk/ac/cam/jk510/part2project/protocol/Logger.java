package uk.ac.cam.jk510.part2project.protocol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import au.com.bytecode.opencsv.CSVWriter;

import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.gui.MapDisplayScreen;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.settings.Config;
import uk.ac.cam.jk510.part2project.store.DataPointPresentException;

public class Logger {

	private static Logger instance;
	private static int blockSize = Config.getArrayBlockSize();

	private static long startingTime;
	private long networkDataUpload = 0;
	private long networkDataDownload = 0;
	private int noRequestsSent = 0;
	private int noRequestsRecieved = 0;
	private int noRequestsRespondedTo = 0;
	private long[] timeOfLastReceipt;
	private int devices;
	private int thisDevice;

	protected ArrayList<long[]>[] receiptTimes;	//TODO receipt or display time?
	protected ArrayList<long[]> genTimes;	//time each point was generated

	protected LinkedList<Long>[] latestPointTimes;	//time of each arrival of the new end of the path for each device

	/*
	 * TODO:
	 * Key:
	 * -- : Data is collected, just needs computation
	 * 
	 * --Amount of data sent / recieved
	 * --Average, min and max frequency of latest position update over all devices
	 * --Max time between updates for any device
	 * --Average, Variance and Max time from recording GPS position, to updating screen of another device
	 * 		^^Assuming time between receipt and drawing is 0
	 * Difference between shape of map drawn and real shape
	 * --PNG file of screen is saved every n seconds.
	 * --^write to disc copies of each map, later compare them between devices
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
		devices = session.numDevices();
		thisDevice = Session.getThisDevice().getDeviceID();
		timeOfLastReceipt = new long[devices];
		receiptTimes = new ArrayList[devices];
		for(int i=0; i<devices; i++) {
			receiptTimes[i] = new ArrayList<long[]>();
			receiptTimes[i].add(new long[blockSize]);
		}

		latestPointTimes = new LinkedList[devices];
		for(int i=0; i<devices; i++) {
			latestPointTimes[i] = new LinkedList<Long>();
		}

		genTimes = new ArrayList<long[]>();
		genTimes.add(new long[blockSize]);

		spawnScreenCaptureThread();

		instance = this;
		startingTime = System.currentTimeMillis();
	}

	public static void download(int bytes) {
		instance.networkDataDownload += bytes;
	}

	public static void upload(int bytes) {
		instance.networkDataUpload += bytes;
	}
	
	public static void sendingRequest() {
		instance.noRequestsSent++;
	}
	
	public static void receivedRequest() {
		instance.noRequestsRecieved++;
	}
	
	public static void respondedToRequest() {
		instance.noRequestsRespondedTo++;
	}

	public static void receivedPoint(int aboutDevice, int index) {
		long time = System.currentTimeMillis() - startingTime;
		instance.timeOfLastReceipt[aboutDevice] = time;

		extendArrays(index);

		//Calculate which array and the offset within it.
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;

		ArrayList<long[]> arra = instance.receiptTimes[Session.getDevice(aboutDevice).getDeviceID()];
		long[] array = arra.get(arrayNumber);
		array[offset] = time;

	}
	public static void generatedPoint(int index) {
		long time = System.currentTimeMillis() - startingTime;

		extendArrays(index);

		//Calculate which array and the offset within it.
		int arrayNumber = index / blockSize;
		int offset = index % blockSize;

		instance.genTimes.get(arrayNumber)[offset] = time;

	}
	public static void newLatestPoint(int device) {
		long time = System.currentTimeMillis() - startingTime;

		instance.latestPointTimes[device].add(time);
	}
	private void spawnScreenCaptureThread() {
		new Thread(new Runnable() {public void run() {

			File dir = null;

			while(ProtocolManager.isAlive()) {

				if(MapDrawer.initialised()) {
					Bitmap bmp = MapDrawer.getScreenShot();
					if(dir == null) {

						if(externalStorageWriteable()) {
							//MapDisplayScreen.instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

							dir = new File(Environment.getExternalStorageDirectory(), "raceTrace");
							if(!dir.exists()) {
								dir.mkdir();
							}


						} else {
							System.err.println("Can't write to external storage.");
						}
					}
					FileOutputStream out;
					try {
						File newFile = new File(dir, "raceTrace"+System.currentTimeMillis()+".png");
						out = new FileOutputStream(newFile);
						bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				try {
					Thread.sleep(Config.getScreenShotTimer());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}}).start();
	}

	private boolean externalStorageWriteable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable;
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
	public static void spawnLogFlush() {
		new Thread(new Runnable() {public void run() {

			instance.writeLogToDisk();

		}}).start();
	}
	private void writeLogToDisk() {
		File dir = null;
		if(externalStorageWriteable()) {

			dir = new File(Environment.getExternalStorageDirectory(), "raceTrace");
			if(!dir.exists()) {
				dir.mkdir();
			}

			try {
				File newFile = new File(dir, "log"+System.currentTimeMillis()+".csv");
				FileWriter writer = new FileWriter(newFile);

				writeStats(writer);

				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.err.println("Can't write log to external storage.");
		}
	}

	private void writeStats(Writer writer) throws IOException {
		CSVWriter csvWriter = new CSVWriter(writer);
		String[] topology = {"Topology", Config.getProtocol().name()};
		String[] protocol = {"Transport", Config.transportProtocol().name()};
		String[] respDec = {"Responce heuristic", Config.responseDecider().name()};
		String[] dropping = {"Packet Dropping", Boolean.toString(Config.droppingEnabled)};
		String[] markov = {null, "Markov dropping", Boolean.toString(Config.markovPacketDroppingSimulation)};
		String[] loseRate = {null, "Connection lose rate", Double.toString(Config.loseConnectionRate)};
		String[] reconnectRate = {null, "Reconnect rate", Double.toString(Config.reconnectRate)};
		String[] dropRate = {null, "Packet drop rate (if not markov)", Double.toString(Config.dropRate)};
		LinkedList<String[]> settings = new LinkedList<String[]>();
		settings.add(topology);
		settings.add(protocol);
		settings.add(respDec);
		settings.add(dropping);
		settings.add(markov);
		settings.add(loseRate);
		settings.add(reconnectRate);
		settings.add(dropRate);
		csvWriter.writeAll(settings);
		String[] numDevices = {"Devices", Integer.toString(devices)};
		String[] thisDeviceA = {"This device:", Integer.toString(thisDevice)};
		String[] sent = {"Sent", Long.toString(networkDataUpload)};
		String[] received = {"Received", Long.toString(networkDataDownload)};
		String[] reqSent = {"Requests sent", Integer.toString(noRequestsSent)};
		String[] reqReceived = {"Requests received", Integer.toString(noRequestsRecieved)};
		String[] reqRespondedTo = {"Requests responded to", Integer.toString(noRequestsRespondedTo)};
		
		csvWriter.writeNext(numDevices);
		csvWriter.writeNext(thisDeviceA);
		csvWriter.writeNext(sent);
		csvWriter.writeNext(received);
		csvWriter.writeNext(reqSent);
		csvWriter.writeNext(reqReceived);
		csvWriter.writeNext(reqRespondedTo);
		String[] largeArray = new String[historyLength()];
		for(int index=0; index<historyLength(); index++) {
			largeArray[index] = Long.toString(genTimes.get(index/blockSize)[index % blockSize]);
		}
		String[] genTimes = {"Generation time"};
		csvWriter.writeNext(genTimes);
		csvWriter.writeNext(largeArray);
		for(int device=0; device<devices; device++) {
			String[] deviceLable = {"Device "+device};
			csvWriter.writeNext(deviceLable);
			for(int index=0; index<historyLength(); index++) {
				largeArray[index] = Long.toString(receiptTimes[device].get(index/blockSize)[index % blockSize]);
			}
			csvWriter.writeNext(largeArray);
		}
		csvWriter.flush();
		csvWriter.close();
	}

}