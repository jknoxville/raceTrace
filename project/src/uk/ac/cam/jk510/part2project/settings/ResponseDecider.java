package uk.ac.cam.jk510.part2project.settings;

public enum ResponseDecider {
	 always,						//always respond
	 probability,					//respond with some probability
	 responseRatioThresh,			//respond if you have n% of the requested data	-	NOTE could result in starvation if request concerns > 1 device
	 responseRatioProbability,		//respond with prob p where you have p of the data.
	 youAreRequestee,				//respond if the request contains data you generated
	 youAreLargestRequestee,		//respond if of the data requested, you are the largest device requested.	NOTE being largest requested may mean the mutual connection is bad.
	 /*
	  * TODO:
	  * respond if you are the peer with strongest signal / most of the data
	  */
	 never;							//never respond
}
