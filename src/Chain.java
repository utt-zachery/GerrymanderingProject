import java.util.Iterator;
import java.util.LinkedList;

public class Chain {

	private LinkedList<Node> chain;
	private int netScore;
	private Party party;
	
	public Chain(Party party) {
		this.party=party;
		netScore = 0;
		this.chain = new LinkedList<Node>();
	}
	
	public int getSize() {
		return this.chain.size();
	}
	
	public Node findBestVoter() {
		
		int bestNetIndex = -1;
		Node bestNode = null;
		for (Node next : this.chain)
		{
			if (next.calculateNetScore(party) > bestNetIndex) {
				bestNetIndex=next.calculateNetScore(party) ;
				bestNode= next;
			}
		}
		return bestNode;
	}
	
	public Node findWorstVoter() {
		
		int worstNetIndex = Integer.MAX_VALUE;
		Node bestNode = null;
		for (Node next : this.chain)
		{
			if (next.calculateNetScore(party) < worstNetIndex) {
				worstNetIndex=next.calculateNetScore(party) ;
				bestNode= next;
			}
		}
		return bestNode;
	}

	public Iterator<Node> getChainIterator() {
		return chain.iterator();
	}
	
	public void addVoter(Node toAdd) {
		if (toAdd.party.equals(this.party))
			this.netScore++;
		else
			this.netScore--;
		
		this.chain.add(toAdd);
	}
	
	public int getNetScore() {
		return this.netScore;
	}
}
