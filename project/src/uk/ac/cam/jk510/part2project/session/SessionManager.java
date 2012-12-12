package uk.ac.cam.jk510.part2project.session;

import android.app.Activity;

public abstract class SessionManager {

	public abstract void newSession(Activity activity) throws IllegalAccessException, InstantiationException;
}
