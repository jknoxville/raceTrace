package uk.ac.cam.jk510.part2project.session;

public abstract class SessionManager {
	
	private static boolean alive = true;

	public abstract void newSession() throws IllegalAccessException, InstantiationException;
	
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