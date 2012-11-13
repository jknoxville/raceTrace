package uk.ac.cam.jk510.part2project.session;

public abstract class SessionManager {

	protected static Session session;
	public abstract void newSession() throws IllegalAccessException, InstantiationException;

	public static Session getSession() throws IllegalAccessException, InstantiationException {
		assert(session != null);
		return session;
	}

	public static String[] getDeviceNames() {
		return session.getDeviceNames();
	}
}
