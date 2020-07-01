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
	public int hashCode;
	
	//Constructor for all nodes
	public Node (Party party, int x, int y, int hashCode) {
		this.x=x;
		this.y=y;
		this.party = party;
		this.hashCode=hashCode;
		isInDistrict = false;
		neighborHood = new ArrayList<Node>();
	}
	
	public boolean isInDistrict() {
		return isInDistrict;
	}
	public void addNeighbor(Node toAdd) {
		neighborHood.add(toAdd);
	}
	
	public List<Node> getNeighbors() {
		return this.neighborHood;
	}
	
	public void addToDistrict(Chain district) {
		this.isInDistrict = true;
		district.addVoter(this);
		this.district=district;
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
			if (toCheck.isInDistrict() == true)
				continue;
			
			if (winningParty.equals(toCheck.party)) {
				toReturn++;
			} else {
				toReturn--;
			}
		}
		netScore= toReturn;
		return toReturn;
	}
}

