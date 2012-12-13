package uk.ac.cam.jk510.part2project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionPackage;
import uk.ac.cam.jk510.part2project.settings.Config;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Receive serialized session from some device.
		 * Set up data storage structures
		 * Set up socket to each device
		 * Start periodic sending thread
		 * Start listening thread for each device
		 * 
		 */

		//Receive serialized Session from some device
		/*
		 * Involves starting thread to listen for a connection
		 */


		new Thread(new Runnable() {
			@Override
			public void run() {
				
				SessionPackage pack = NetworkInterface.getSessionPackage();
				Session session = Session.reconstructSession(pack);

				//spawn device listener threads:
				for(final Device device: session.getDevices()) {
					
					//new thread:
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
							ServerSocket serv = new ServerSocket(Config.getServerPort());
							Socket sock = serv.accept();
							System.out.println("connected to "+device.getName());	//debug
							
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}).start();

				}
			}

		}).start();
	}

}
