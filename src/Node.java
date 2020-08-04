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
	private boolean isEdge;
	
	//Constructor for all nodes
	public Node (Party party, int x, int y, int hashCode) {
		this.x=x;
		this.y=y;
		this.party = party;
		this.hashCode=hashCode;
		isInDistrict = false;
		neighborHood = new ArrayList<Node>();
	}
	
	public Chain getDistrict() {
		return this.district;
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
	
	public boolean isDistrictEdge() {
		return this.isEdge;
	}
	
	public void detectEdge() {
		this.isEdge = false;
		for (Node n : this.neighborHood) {
			if (!n.district.equals(this.district))
				this.isEdge = true;
		}
	}
	
	public void addToDistrict(Chain district) {
		this.isInDistrict = true;
		district.addVoter(this);
		this.district=district;
	}
	public int availableNeighborCount(){ // return the number of nodes in the given node's neighbor that can be added to the district 
		//or are already in the node's district
		int numAvailable = 0;
		for (Node neighbor : this.neighborHood){
			if (neighbor != null && (!neighbor.isInDistrict || neighbor.district == this.district)){
				numAvailable++;
			}
		}
		return numAvailable;
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

