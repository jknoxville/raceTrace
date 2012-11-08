package uk.ac.cam.jk510.part2project.session;

public abstract class SessionManager {

	private static Session session;
	public abstract void newSession();

	public static Session getSession() {
		return session;
	}

	public static String[] getDeviceNames() {
		return session.getDeviceNames();
	}
}
