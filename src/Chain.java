import java.util.Iterator;
import java.util.LinkedList;

public class Chain {

	private LinkedList<Node> chain;
	private int netScore;
	private Party party;
	public double getRatio(){
		return (double) this.getPartyCount() / (double) this.getSize();
	}
	public Chain(Party party) {
		this.party=party;
		netScore = 0;
		this.chain = new LinkedList<Node>();
	}

	public int getSize() {
		return this.chain.size();
	}

	public Node findMostNeutralVoter(){
		int max  = Integer.MAX_VALUE;
		Node neutral = null;

		for (Node inChain : this.chain){
			for (Node outChain : inChain.neighborHood){
				if (!outChain.isInDistrict && Math.abs(outChain.calculateNetScore(party)) < max){
					max = Math.abs(outChain.calculateNetScore(party));
					neutral = outChain;
				}
			}
		}
		return neutral;
	}
	public Node findNeutral(){

		Node neutral = null;

		for (Node inChain : this.chain){
			for (Node outChain : inChain.neighborHood){
				if (!outChain.isInDistrict && outChain.calculateNetScore(party) == 0){

					neutral = outChain;
				}
			}
		}
		return neutral;
	}
	public Node findBestVoter() {

		int bestNetIndex = Integer.MIN_VALUE;
		Node bestNode = null;
		for (Node pre : this.chain)
		{
			for (Node next : pre.neighborHood)
			{
				if (next.isInDistrict() == false && next.calculateNetScore(party) > bestNetIndex) {
					bestNetIndex=next.calculateNetScore(party) ;
					bestNode= next;
				}
			}
		}
		return bestNode;
	}

	public Node findWorstVoter() {

		int worstNetIndex = Integer.MAX_VALUE;
		Node bestNode = null;
		for (Node pre : this.chain)
		{
			for (Node next : pre.neighborHood)
			{
				if (next.isInDistrict() == false && next.calculateNetScore(party) < worstNetIndex) {
					worstNetIndex=next.calculateNetScore(party) ;
					bestNode= next;
				}
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
	public void removeVoter(Node toRemove){

		if(toRemove.party.equals(this.party))
			this.netScore--;
		else
			this.netScore++;
		toRemove.district = null;
		toRemove.isInDistrict = false;
		this.chain.remove(toRemove);
	}

	public int getPartyCount() {
		int toreturn=0;
		for (Node n: this.chain)
			if (n.party == this.party)
				toreturn++;
		return toreturn;
	}
	public int getNetScore() {
		return this.netScore;
	}
}
