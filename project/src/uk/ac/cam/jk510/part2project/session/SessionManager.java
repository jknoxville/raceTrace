package uk.ac.cam.jk510.part2project.session;

import android.app.Activity;

public abstract class SessionManager {
	
	private static boolean alive = true;

	public abstract void newSession(Activity activity) throws IllegalAccessException, InstantiationException;
	
	public static void checkIfAlive() throws StopThreadException {
		if(!alive) {
			throw new StopThreadException();
		}
	}

	public static void setAlive() {
		alive = true;
	}
	
	public static void killThread() {
		alive = false;
	}
	
}
