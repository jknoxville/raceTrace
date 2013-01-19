package uk.ac.cam.jk510.part2project.test;

import uk.ac.cam.jk510.part2project.network.DataConnectionManager;
import android.test.AndroidTestCase;

public class MessageTest extends AndroidTestCase {
	public MessageTest() {
		super();
	}
	
	@Override
	public void setUp() {
		/*
		 * open netwrok connection with self
		 */
		DataConnectionManager.initDataSocket();
	}
	
	@Override
	public void tearDown() {
		
	}

}
