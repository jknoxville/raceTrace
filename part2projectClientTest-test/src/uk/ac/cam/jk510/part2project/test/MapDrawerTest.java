package uk.ac.cam.jk510.part2project.test;

import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import uk.ac.cam.jk510.part2project.R;
import uk.ac.cam.jk510.part2project.gui.MapDisplayScreen;
import uk.ac.cam.jk510.part2project.gui.NewOldSession;
import uk.ac.cam.jk510.part2project.gui.NewSessionActivitySingleSession;
import uk.ac.cam.jk510.part2project.gui.NewSessionActivitySingleUser;
import uk.ac.cam.jk510.part2project.gui.SMBTserverorclient;
import uk.ac.cam.jk510.part2project.protocol.ProtocolManager;
import uk.ac.cam.jk510.part2project.session.Device;
import uk.ac.cam.jk510.part2project.session.Session;
import uk.ac.cam.jk510.part2project.session.SessionEnum;
import uk.ac.cam.jk510.part2project.session.SessionManagerPredefined;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleSession;
import uk.ac.cam.jk510.part2project.session.SessionManagerSingleUser;
import uk.ac.cam.jk510.part2project.settings.Config;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

public class MapDrawerTest extends InstrumentationTestCase {

	MapDrawer instance;
	NewOldSession menu;
	MapDisplayScreen mapDrawer;

	@Override
	public void setUp() {
		/*
		 * Create some session
		 * create mapDrawer object instance
		 */
		
		final Class newSessionActivity;
		SessionEnum sesh = Config.getSesh();
		switch(sesh) {
		case singleUser: newSessionActivity = NewSessionActivitySingleUser.class;
		break;
		case bluetooth: newSessionActivity = SMBTserverorclient.class;
		break;
		case singleSession: newSessionActivity = NewSessionActivitySingleSession.class;
		break;
		default: try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Do something if not an enum value?
				newSessionActivity = null;
				e.printStackTrace();
			}
		}
		//Intent intent = new Intent(getInstrumentation().getContext(), NewSessionActivitySingleUser.class);
		menu = launchActivity("uk.ac.cam.jk510.part2project", NewOldSession.class, null);
		//.startActivityForResult(intent, 1);
		//mapDrawer = launchActivity("uk.ac.cam.jk510.part2project", NewSessionActivitySingleUser.class, null);
	}
	
	private void exit() {
		MapDisplayScreen.instance.exitForSure();
	}
	
	@Override
	public void tearDown() {
//		MapDisplayScreen.instance.exitForSure();
		//the following shouldnt be needed
//		ProtocolManager.destroy();
//		Session.destroy();
	}
	
	public void testSingleDeviceRandom101() {
		try {
			menu.singleUserSetup(menu.findViewById(R.layout.activity_new_old_session));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Device device = Session.getDevice(0);
		int i=0;
		boolean[] indexUsed = new boolean[101];
		while(i<101) {
			int index = (int) (Math.random()*100);
			if(!indexUsed[index]) {
				ProtocolManager.testInputData(device, index);
				indexUsed[index] = true;
				i++;
			}
		}
		exit();
	}
	
	public void testSecondSession() {
		try {
			menu.singleUserSetup(menu.findViewById(R.layout.activity_new_old_session));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testInsertSingleDevice101Test();
		exit();
		try {
			menu.singleUserSetup(menu.findViewById(R.layout.activity_new_old_session));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testInsertSingleDevice101Test();
		exit();
	}
	
	public void testInsertSingleDevice101Test() {
		Device device = Session.getDevice(0);
		for(int i=0; i<101; i++) {
			ProtocolManager.testInputData(device, i);
		}
		exit();
	}
	
	public void testRandomInput() {
		setUp();
		ProtocolManager.testInputData();
		exit();
	}
}
