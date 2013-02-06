package uk.ac.cam.jk510.part2project.test;

import uk.ac.cam.jk510.part2project.graphics.MapDrawer;
import android.os.Bundle;
import android.test.InstrumentationTestCase;

public class MapDrawerTest extends InstrumentationTestCase {

	MapDrawer instance;

	@Override
	public void setUp() {
		/*
		 * Create some session
		 * create mapDrawer object instance
		 */
		instance = launchActivity("uk.ac.cam.jk510.part2project.graphics", MapDrawer.class, new Bundle());
	}
	
	@Override
	public void tearDown() {
		
	}
}
