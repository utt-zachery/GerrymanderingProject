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

	public double getRatio(){
		return (double) this.getPartyCount() / this.getSize();
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

	
	public int getSize() {
		return this.chain.size();
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
	
	public Node findBestVoterInside(){
		int bestNetIndex = Integer.MIN_VALUE;
		Node bestNode = null;
		for (Node node : this.chain){
			if (node.calculateNetScore(party) > bestNetIndex){
				bestNetIndex = node.calculateNetScore(party);
				bestNode = node;
			}
		}
		return bestNode;
	}
	
	public Node findWorstVoterInside(){
		int worstNetIndex = Integer.MAX_VALUE;
		Node worstNode = null;
		for (Node node : this.chain){
			if (node.calculateNetScore(party) < worstNetIndex){
				worstNetIndex = node.calculateNetScore(party);
				worstNode = node;
			}
		}
		return worstNode;
	}

	public Iterator<Node> getChainIterator() {
		return chain.iterator();
	}
	
	public void addVoter(Node toAdd) {
		
		String callerName = Thread.currentThread().getStackTrace()[2].getClassName();

		try {
		    Class<?> caller = Class.forName(callerName);
		   if (!caller.equals(toAdd.getClass())) {
			   throw new RuntimeException("addVoter must be invoked from the Node class!");
		   }
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
		
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
			if (n.party.equals(this.party))
				toreturn++;
		return toreturn;
	}

	public int getNetScore() {
		return this.netScore;
	}
}
