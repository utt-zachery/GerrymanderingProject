import java.util.Iterator;
import java.util.LinkedList;

//The Chain class represents a district
public class Chain {

	private LinkedList<AbstractNode> chain;
	private int netScore;
	private Party party;
	
	public Chain(Party party) {
		this.party=party;
		netScore = 0;
		this.chain = new LinkedList<AbstractNode>();
	}
	
	public int getSize() {
		return this.chain.size();
	}
	
	
	//Returns the voter in the immediate vacinity of the district that, if added, would benefit the winning party the most
	public AbstractNode findBestVoter() {
		
		int bestNetIndex = Integer.MIN_VALUE;
		AbstractNode bestNode = null;
		for (AbstractNode pre : this.chain)
		{
			for (AbstractNode next : pre.getNeighbors())
			{
				if (next.isInDistrict() == false && next.calculateNetScore(party) > bestNetIndex) {
					bestNetIndex=next.calculateNetScore(party) ;
					bestNode= next;
				}
			}
		}
		return bestNode;
	}
	
	//Returns the voter in the immediate vacinity of the district that, if added, would hurt the current party the most
	public AbstractNode findWorstVoter() {
		
		int worstNetIndex = Integer.MAX_VALUE;
		AbstractNode bestNode = null;
		for (AbstractNode pre : this.chain)
		{
			for (AbstractNode next : pre.getNeighbors())
			{
				if (next.isInDistrict() == false && next.calculateNetScore(party) < worstNetIndex) {
					worstNetIndex=next.calculateNetScore(party) ;
					bestNode= next;
				}
			}
		}
		return bestNode;
	}

	public Iterator<AbstractNode> getChainIterator() {
		return chain.iterator();
	}
	
	// IMPORTANT: Should be invoked by the NODE class ONLY through DOUBLE DISPATCH
	// There are no checks for this to allow for inheritance of the Node class
	public void addVoter(AbstractNode toAdd) {
		
		if (toAdd.getParty().equals(this.party))
			this.netScore++;
		else
			this.netScore--;
		
		this.chain.add(toAdd);
		
	}
	
	//Returns the number of voters in the district who affiliate with a particular party
	public int getPartyCount() {
		int toreturn=0;
		for (AbstractNode n: this.chain)
			if (n.getParty().equals(this.party))
				toreturn++;
		return toreturn;
	}
	
	public int getNetScore() {
		return this.netScore;
	}
}