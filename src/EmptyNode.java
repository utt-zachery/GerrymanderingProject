import java.util.ArrayList;
import java.util.List;

public class EmptyNode extends Node {

	public EmptyNode(int x, int y) {
		super(null, x, y);
		isInhabited = false;
	}
	
	@Override
	public List<Node> globalNeighbors() {
		ArrayList<Node> toReturn= new ArrayList<Node>();
		for (Node toVisit : super.neighborHood) {
			if (toVisit.isInDistrict == false ) {
				toReturn.add(toVisit);
				
				List<Node> reccu = toVisit.globalNeighbors();
				for (Node recurssive : reccu) {
					toReturn.add(recurssive);
				}
			}
		}
		return toReturn;
	}
	
	//Cost of adding this district + items in the neighborhood that help
		public int calculateNetScore(Party winningParty) {
			int toReturn = 0;
			
			for (Node toCheck : neighborHood) {
				if (this.party.equals(toCheck.party)) {
					toReturn++;
				} else {
					toReturn--;
				}
			}
			
			return toReturn;
		}
}
