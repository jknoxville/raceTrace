package uk.ac.cam.jk510.part2project.session;

import java.util.ArrayList;

public class Session {
	
	private static Session session;
	
	private ArrayList<Device> deviceList;
	private Keys keys;
	
	public String[] getDeviceNames() {
		String[] names = new String[deviceList.size()];
		for(Device d: deviceList) {
			names[d.getDeviceID()] = d.getName();
		}
		return names;
	}
	
}
