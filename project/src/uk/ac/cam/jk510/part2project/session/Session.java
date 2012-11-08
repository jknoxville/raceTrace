package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class Session {
	private static Session session;
	
	private ArrayList<Device> deviceList;
	private Keys keys;
	
	public static void newSession() {
		//Set up initial session
	}
	public static Session getSession() {
		return session;
	}
	
}
