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
