import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
		PriorityQueue<Chain> bestDistricts = new PriorityQueue<Chain>(1, singleCompPos);
		PriorityQueue<Chain> worstDistricts = new PriorityQueue<Chain>(1, singleCompNeg);
		List<Chain> allDistrict = new ArrayList<Chain>();
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			bestDistricts.add(chains.peek());
			allDistrict.add(chains.peek());
			worstDistricts.add(chains.poll());
		}
		
		
		while (chains.isEmpty() == false)
		{
			Iterator<Node> todelete = chains.poll().getChainIterator();
			while (todelete.hasNext()) {
				Node tofree= todelete.next();
				tofree.district = null;
				tofree.isInDistrict = false;
			}
		}
		
		
		int oldCount = 0;
		
		for (Chain c : allDistrict) {
			oldCount=oldCount+c.getSize();
		}
		int quikCount = oldCount;
		while (quikCount < this.census.getMaxVoter()) {
			doIteration(bestDistricts, worstDistricts);
			quikCount = 0;
			for (Chain c : allDistrict) {
				quikCount=quikCount+c.getSize();
			}
			if (quikCount == oldCount)
				doStaleIteration(allDistrict);
			
			oldCount = quikCount;
		}
		
		Chain[] toReturn = new Chain[this.numberOfDistricts];
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			toReturn[i]=bestDistricts.poll();
			System.err.println(toReturn[i].getNetScore() + " : " + toReturn[i].getPartyCount() + " : " + toReturn[i].getSize() + " = " + ((double)toReturn[i].getPartyCount() ) / (double) toReturn[i].getSize());
		}
		
		
		return toReturn;
	}

	private void doStaleIteration(List<Chain> allDistricts) {

		for (Chain c: allDistricts) {
		
		Node best = c.findBestVoter();
		if (best == null)
			continue;
		best.addToDistrict(c);
		for (Node better : best.neighborHood)
		{
			if (better.isInDistrict() == false) {
				better.addToDistrict(c);
			}
		}
		
		}
	}
	
	private void doIteration(PriorityQueue<Chain> bestDistricts, PriorityQueue<Chain> worstDistricts) {

		Chain bestChain = bestDistricts.poll();
		
		worstDistricts.remove(bestChain);
		
		
		Chain worstChain = worstDistricts.poll();
		bestDistricts.remove(worstChain);
		
		Node worst = bestChain.findWorstVoter();
		Node best = worstChain.findBestVoter();
		
		if (best != null && best.isInDistrict() == false && best.netScore>=0) {
		best.addToDistrict(worstChain);
		for (Node better : best.neighborHood)
		{
			if (better.isInDistrict() == false) {
				better.addToDistrict(worstChain);
			}
		}
		}
		
		if (worst != null && worst.isInDistrict() == false) {
		worst.addToDistrict(bestChain);
		for (Node worse : worst.neighborHood)
		{
			if (worse.isInDistrict() == false) {
				worse.addToDistrict(bestChain);
			}
		}
		}
		
		bestDistricts.add(bestChain);
		bestDistricts.add(worstChain);
		
		worstDistricts.add(bestChain);
		worstDistricts.add(worstChain);
	}
	
	public void buildChains() {
		
		int activeIndex = 0;
		
		while (activeIndex < census.getMaxVoter()) {
			Node head = census.getVoter(activeIndex);
			if (head != null && head.isInDistrict() == false && head.party == this.party) {
				
				Chain newChain = new Chain(party);
				buildChainHelper(newChain, head);
				chains.add(newChain);
			}
			activeIndex++;
		}
	}
	
	private void buildChainHelper(Chain activeChain, Node activeNode) {

		if (activeNode.isInDistrict() == false && activeNode.party == this.party && activeChain.getSize() < census.getMaxVoter() / (2*this.numberOfDistricts)) {
			activeNode.addToDistrict(activeChain);
		
			for (Node activeNeighborhood : activeNode.getNeighbors()) {
				buildChainHelper(activeChain, activeNeighborhood);
			}
		}
	}
}
