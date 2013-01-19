package uk.ac.cam.jk510.part2project.store;

import java.util.LinkedList;

public class Response {
	
	public LinkedList<Coords> matches;
	public LinkedList<Integer> remainingPoints;
	
	public Response() {
		matches = new LinkedList<Coords>();
		remainingPoints = new LinkedList<Integer>();
	}
	
	public static LinkedList<Coords> getCoordsList(Response[] responses) {
		LinkedList<Coords> coordsList = new LinkedList<Coords>();
		for(Response response: responses) {
			coordsList.addAll(response.matches);
		}
		return coordsList;
	}

}
