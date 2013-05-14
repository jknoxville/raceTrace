package grouping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Block {

	private static long width = 100000;
	private static long radius = 300;
	//todo better initial cap:
	private static HashMap<Long, Block> blocks = new HashMap<Long, Block>();

	private static boolean alive=true;
	private static long timeout = 10000;

	private long x;
	private long y;
	private LinkedBlockingQueue<Request> requests;

	static synchronized Block getBlock(long x, long y) {
		long index = y*width + x;
		return blocks.get(index);
	}

	public static void processRequests() {
		while(alive) {
			final Request a;
			try {
				//blocks and waits for next one if empty
				a = Request.allRequests.take();

				try {
					Thread.sleep(timeout-(System.currentTimeMillis()-a.birthtime));
				} catch (InterruptedException e) {/*continue*/}
				for(Block b: a.getBlock().getNeighbours()) {
					for(Request r: b.requests) {
						if(a.distanceTo(r)<=getRadius()) {
							a.addToParty(r);
							Request.allRequests.remove(r);
							b.remove(r);
						}
					}
				}
				a.getBlock().remove(a);
				//spawn a new thread to take care of this session from now on
				new Thread(new Runnable() {
					public void run() {
						a.getValue().process(a.getParty());
					}
				}).start();
			} catch (InterruptedException e1) {
				processRequests();
			}

		}
	}

	private synchronized void remove(Request r) {
		requests.remove(r);
		//if block now empty, remove it from hashmap
		if(requests.size()==0) {
			blocks.remove(y*width + x);
		}
	}

	public Block(long x, long y) {
		this.x=x;
		this.y=y;
		requests = new LinkedBlockingQueue<Request>();
		synchronized(this) {
			blocks.put(y*width + x, this);
		}
	}

	public synchronized void add(Request r) {
		requests.add(r);
	}

	public synchronized List<Block> getNeighbours() {
		LinkedList<Block> neighbours = new LinkedList<Block>();
		for(long i=x-1; i<x+2; i++) {
			for(long j=y-1; j<y+2; j++) {
				Block b;
				if((b = blocks.get(j*width+i)) != null) {
					neighbours.add(b);
				}
			}
		}
		return neighbours;
	}

	public static long getRadius() {
		return radius;
	}


}
