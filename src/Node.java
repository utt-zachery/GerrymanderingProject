import java.util.ArrayList;
import java.util.List;

public class Node {

	protected Party party;
	protected boolean isInhabited;
	protected int x;
	protected int y;
	protected List<Node> neighborHood;
	protected boolean isInDistrict;
	
	//Constructor for all nodes
	public Node (Party party, int x, int y) {
		this.x=x;
		this.y=y;
		this.party = party;
		isInhabited = true;
		isInDistrict = false;
		neighborHood = new ArrayList<Node>();
	}
	
	public List<Node> globalNeighbors() {
		return new ArrayList<Node>();
	}
	
	//Cost of adding this district + items in the neighborhood that help
	public int calculateNetScore(Party winningParty) {
		int toReturn = 0;
		
		if (this.party.equals(winningParty)) {
			toReturn++;
		} else {
			toReturn--;
		}
		
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

