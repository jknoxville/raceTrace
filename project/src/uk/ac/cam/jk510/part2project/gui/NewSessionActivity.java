package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public abstract class NewSessionActivity extends Activity {
	
	/*
	 * This activity is always the predecessor of MapDisplayScreen.
	 */

	Exception exception;
	static NewSessionActivity instance;
	private static final int NEXT_SCREEN = 1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_session, menu);
		return true;
	}

	//This method spawns a new thread and sets it running setUpSession().
	//When finished setUpSession(), it calls onSetupComplete()
	final protected void setUpSessionThread() {
		instance = this;	//sets up static link so this activity can be closed by another
		new Thread(new Runnable() {

			public void run() {
				try {
					setUpSession();
					
				} catch (IllegalAccessException e) {
					recordException(e);
					e.printStackTrace();
				} catch (InstantiationException e) {
					recordException(e);
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void recordException(Exception e) {
		exception = e;
	}

	protected abstract void setUpSession() throws Exception;

	//called when session set-up is successful
	public void onSetupComplete(View view) throws Exception {
		ProtocolManager.initialiseProtocolManager(Session.getSession());
		Intent intent = new Intent(this, MapDisplayScreen.class);    	
		startActivityForResult(intent, NEXT_SCREEN);
	}
	
	public void onSetupComplete() throws Exception {
		onSetupComplete(null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == NEXT_SCREEN) {
			onBackPressed();
		}
	}
	
	//destroy all state as if this activity was never created.
	private void destroy() {
		System.out.println("DESTROYING SESSION");
		SessionManager.killThread();	//this will stop the session setup thread safely and eventually.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ProtocolManager.destroy();
		Session.destroy();
		instance = null;
	}
	
	public void onBackPressed() {
		destroy();
		this.finish();
		Session.destroy();
	}

}
