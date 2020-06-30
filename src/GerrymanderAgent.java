import java.util.Comparator;
import java.util.PriorityQueue;

public class GerrymanderAgent {
	
	protected CensusMap census;
	protected Party party;
	int numberOfDistricts;
	
	protected PriorityQueue<Chain> chains;
	private static Comparator<Chain> singleCompPos = null;
	private static Comparator<Chain> singleCompNeg = null;
	
	public GerrymanderAgent(CensusMap census, Party party, int numberOfDistricts) {
		
		if (singleCompPos == null) {
			singleCompPos= new ChainComparator();
			singleCompNeg = new ChainComparatorNegative();
		}
		
		this.numberOfDistricts=numberOfDistricts;
		this.census = census;
		this.party = party;
		chains = new PriorityQueue<Chain>(1, singleCompPos);
	}
	
	public Chain[] growChains() {
		System.out.println("Peek: " + chains.peek().getNetScore());
		int totalDistricted = 0;
		PriorityQueue<Chain> bestDistricts = new PriorityQueue<Chain>(1, singleCompPos);
		PriorityQueue<Chain> worstDistricts = new PriorityQueue<Chain>(1, singleCompNeg);
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			totalDistricted=totalDistricted+chains.peek().getSize();
			bestDistricts.add(chains.peek());
			worstDistricts.add(chains.poll());
		}
		
		while (totalDistricted < this.census.getMaxVoter()) {
			totalDistricted=totalDistricted+doIteration(bestDistricts, worstDistricts);
		}
		
		Chain[] toReturn = new Chain[this.numberOfDistricts];
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			toReturn[i]=bestDistricts.poll();
			System.err.println(toReturn[i].getNetScore() + " : " + toReturn[i].getSize());
		}
		return toReturn;
	}
	
	private int doIteration(PriorityQueue<Chain> bestDistricts, PriorityQueue<Chain> worstDistricts) {
		int toReturn = 0;
		Chain bestChain = bestDistricts.poll();
		Chain worstChain = worstDistricts.poll();
		worstDistricts.remove(bestChain);
		bestDistricts.remove(worstChain);
		
		Node worst = bestChain.findWorstVoter();
		Node best = worstChain.findBestVoter();
		
		best.addToDistrict(worstChain);
		for (Node better : best.neighborHood)
		{
			if (better.isInDistrict == false) {
				better.addToDistrict(worstChain);
				toReturn++;
			}
		}
		
		worst.addToDistrict(bestChain);
		for (Node worse : worst.neighborHood)
		{
			if (worse.isInDistrict == false) {
				worse.addToDistrict(bestChain);
				toReturn++;
			}
		}
		
		bestDistricts.add(bestChain);
		bestDistricts.add(worstChain);
		
		worstDistricts.add(bestChain);
		worstDistricts.add(worstChain);
		return toReturn +2;
	}
	
	public void buildChains() {
		
		int activeIndex = 0;
		
		while (activeIndex < census.getMaxVoter()) {
			Node head = census.getVoter(activeIndex);
			if (head != null && head.isInDistrict == false && head.party == this.party) {
				
				Chain newChain = new Chain(party);
				buildChainHelper(newChain, head);
				chains.add(newChain);
			}
			activeIndex++;
		}
	}
	
	private void buildChainHelper(Chain activeChain, Node activeNode) {
		
		if (activeNode.isInDistrict == false && activeNode.party == this.party && activeChain.getSize() < census.getMaxVoter() / (2*this.numberOfDistricts)) {
			activeNode.addToDistrict(activeChain);
		
			
			for (Node activeNeighborhood : activeNode.getNeighbors()) {
				buildChainHelper(activeChain, activeNeighborhood);
			}
		}
	}
}
