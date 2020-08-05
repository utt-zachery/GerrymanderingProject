import java.util.List;

public abstract class AbstractNode {

	private Party party;
	private int x;
	private int y;
	private boolean isInDistrict;
	private Chain district;
	private boolean isEdge;
	
	//Constructor for all nodes
	public AbstractNode (Party party, int x, int y) {
		this.x=x;
		this.y=y;
		this.party = party;
		isInDistrict = false;
	}
	
	public Party getParty() {
		return party;
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	public Chain getDistrict() {
		return this.district;
	}
	
	public void divorceDistrict() {
		this.district = null;
		this.isInDistrict = false;
	}
	
	public boolean isInDistrict() {
		return isInDistrict;
	}
	
	public abstract void addNeighbor(AbstractNode toAdd);
	public abstract List<AbstractNode> getNeighbors();
	
	public boolean isDistrictEdge() {
		return this.isEdge;
	}
	
	public void detectEdge() {
		this.isEdge = false;
		for (AbstractNode n : this.getNeighbors()) {
			if (!n.district.equals(this.district))
				this.isEdge = true;
		}
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
		
		for (AbstractNode toCheck : this.getNeighbors()) {
			if (toCheck.isInDistrict() == true)
				continue;
			
			if (winningParty.equals(toCheck.party)) {
				toReturn++;
			} else {
				toReturn--;
			}
		}
		return toReturn;
	}	
}
