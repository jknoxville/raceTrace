package uk.ac.cam.jk510.part2project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;

import uk.ac.cam.jk510.part2project.network.Message;
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
		 * Set up datagram socket.
		 * Spawn one listening thread.
		 * Start periodic sending thread.
		 * 
		 */

		//Receive serialized Session from some device
		/*
		 * Involves starting thread to listen for a connection
		 */

		SessionPackage pack = NetworkInterface.getSessionPackage();
		Session session = Session.reconstructSession(pack);

		NetworkInterface net = NetworkInterface.getInstance();
		ServerState.init();
		
		//TODO spawn periodic sending thread
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(Config.getServerResendPeriodMillis());
						ServerState.sendIfReady();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		System.out.println("Now listening for data");	//debug
		
		//listen for incoming data and process it:
		while(true) {
			Message.processDatagram(net.receiveDatagram());
		}
	}

}
