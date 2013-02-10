package uk.ac.cam.jk510.part2project.gui;

import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionManager;
import uk.ac.cam.jk510.part2project.session.StopThreadException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public abstract class NewSessionActivity extends Activity {
	
	/*
	 * This activity is always the predecessor of MapDisplayScreen.
	 */

	//Session session;
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
				//onSetupComplete();
				//commented out because currently called by UI TODO which is why it has View view in arguments.
				catch (Exception e) {
					// TODO Auto-generated catch block
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
		
		//TODO new thread?:
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
		SessionManager.killThread();	//this will stop the session setup thread eventually.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ProtocolManager.destroy();
		Session.destroy();		//TODO added on 14th jan (may break something)
		instance = null;
	}
	
	public void onBackPressed() {
		destroy();
		this.finish();
		Session.destroy();
	}

}
