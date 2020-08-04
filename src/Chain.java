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
	//returns percentage of voter within district that are the districts party
	public double getRatio(){
		return (double) this.getPartyCount() / this.getSize();
	}
	//returns voter who's net score is closest to 0 and is avaiable to add 
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
	//returns voter who's net score is 0 and 
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
	//returns a voter with the highest net score available
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
	//returns a voter with the lowest net score available
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
	//adds voter to chain
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
	
	
	//returns number of voters with the same party as the district
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
