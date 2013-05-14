package grouping;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import uk.ac.cam.jk510.part2project.session.Processable;

public class Request<T extends Processable> {
	
	protected static LinkedBlockingQueue<Request> allRequests = new LinkedBlockingQueue<Request>();
	
	public long birthtime = System.currentTimeMillis();
	private Block block;
	private double x, y;
	private LinkedList<Request> party;
	private T value;
	
	public Request(double x, double y, T value) {
		long i=(long) x/Block.getRadius();
		long j=(long) y/Block.getRadius();
		party = new LinkedList<Request>();
		this.value = value;
		Block block = Block.getBlock(i, j);
		if(block == null) {
			block = new Block(i,j);
		}
		block.add(this);
		System.out.println("adding to block "+i+", "+j+". now contains: "+block.getSize());
		this.block = block;
		allRequests.add(this);
	}
	
	public Block getBlock() {
		return block;
	}
	
	public double distanceTo(Request b) {
		return (x-b.x)*(x-b.x)+(y-b.y)*(y-b.y);
	}
	
	public void addToParty(Request b) {
		party.add(b);
	}
	
	public T getValue() {
		return value;
	}
	
	public List<Request> getParty() {
		LinkedList<Request> party = new LinkedList<Request>();
		party.addAll(this.party);
		System.out.println("This party: "+this.party.size()+" and returned party: "+party.size());
		return party;
	}

}
