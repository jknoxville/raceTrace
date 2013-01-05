package uk.ac.cam.jk510.part2project.session;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import uk.ac.cam.jk510.part2project.gui.NewSessionActivitySingleSession;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class SessionManagerSingleSession extends SessionManager {

	@Override
	public void newSession(Activity activity) throws IllegalAccessException,
	InstantiationException {
		// TODO Auto-generated method stub

	}

	public static void spawnSetupThread(final View view) {
		new Thread(
				new Runnable() {
					public void run() {
						try {
							//Open TCP socket to server.
							Socket sock = new Socket(Config.getServerIP(), 60000);	//TODO hardcoded port

							//Send name, address and port
							ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
							oos.writeObject(Config.getName());

							//recieve session object back
							ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
							SessionPackage pack = (SessionPackage) ois.readObject();
							Session.reconstructSession(pack);

							//TODO post new intent thing to ui thread to go to mapdispalyscreen.
							view.post(new Runnable() {

								public void run() {

									try {

										//TODO go to mapdisplayscreen
										Intent intent = new Intent(context, newSessionActivity);
				    					NewSessionActivitySingleServer.startActivityForResult(intent, 1);

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

							});

						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				).start();

	}

}
