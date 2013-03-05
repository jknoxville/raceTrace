import java.util.List;


public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grid grid = new Grid();
		for(int i=0; i<100; i++) {
			Point p = new Point(Math.random()*10, Math.random()*10);
			grid.insert(p);
		}
		int count=0;
		List<Point> group = grid.nextGroup();
		if(group != null) count += group.size();
		while(group != null) {
			group = grid.nextGroup();
			if(group != null) count += group.size();
		}
		while(grid.advance()) {
			group = grid.nextGroup();
			if(group != null) count += group.size();
			while(group != null) {
				group = grid.nextGroup();
				if(group != null) {
					count += group.size();
				}
			}


		}
		System.out.println("count: "+count);
	}

}
