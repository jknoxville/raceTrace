import java.util.List;


public class Driver {


	public static void main(String[] args) {
		Grid grid = new Grid();
		for(int iteration=0; iteration<2; iteration++) {
			grid.resetPosition();
			for(int i=0; i<1000; i++) {
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

}
