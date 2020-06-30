import java.util.ArrayList;
import java.util.List;

public class Node {

	protected Party party;
	protected int x;
	protected int y;
	protected List<Node> neighborHood;
	protected boolean isInDistrict;
	protected int netScore;
	protected Chain district;
	
	//Constructor for all nodes
	public Node (Party party, int x, int y) {
		this.x=x;
		this.y=y;
		this.party = party;
		isInDistrict = false;
		neighborHood = new ArrayList<Node>();
	}
	
	public void addNeighbor(Node toAdd) {
		neighborHood.add(toAdd);
	}
	
	public void removeNeighbor(Node toAdd) {
		neighborHood.remove(toAdd);
	}
	
	public List<Node> getNeighbors() {
		return this.neighborHood;
	}
	
	public void addToDistrict(Chain district) {
		this.isInDistrict = true;
		district.addVoter(this);
		this.district=district;
		for (Node toCheck : neighborHood) {
			toCheck.removeNeighbor(this);
		}
	}
	
	//Cost of adding this district + items in the neighborhood that help
	public void calculateNetScore(Party winningParty) {
		int toReturn = 0;
		
		if (this.party.equals(winningParty)) {
			toReturn++;
		} else {
			toReturn--;
		}
		
		for (Node toCheck : neighborHood) {
			if (toCheck.isInDistrict == false)
				continue;
			
			if (this.party.equals(toCheck.party)) {
				toReturn++;
			} else {
				toReturn--;
			}
		}
		netScore= toReturn;
	}
}

