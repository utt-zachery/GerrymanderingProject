import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JProgressBar;

public class GerrymanderAgent {
	
	protected CensusMap census;
	protected Party party;
	int numberOfDistricts;
	
	protected PriorityQueue<Chain> chains;
	private static Comparator<Chain> singleCompPos = null;
	private static Comparator<Chain> singleCompNeg = null;
	private JProgressBar executeProgress;
	
	public GerrymanderAgent(CensusMap census, Party party, int numberOfDistricts, JProgressBar executeProgress) {
		
		if (singleCompPos == null) {
			singleCompPos= new ChainComparator();
			singleCompNeg = new ChainComparatorNegative();
		}
		
		this.executeProgress=executeProgress;
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
			executeProgress.setValue((int) Math.round(100 * (double)quikCount/(double)this.census.getMaxVoter()));
			executeProgress.repaint();
			executeProgress.revalidate();
			doIteration(bestDistricts, worstDistricts, allDistrict);
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
	
	private void addNeighborhoodToDistrict(Chain district, Node toAdd) {
		
		for (Node c : toAdd.getNeighbors()) {
			if (c.isInDistrict() == false) {
				c.addToDistrict(district);
			}
		}
		
	}
	
	private void doIteration(PriorityQueue<Chain> bestDistricts, PriorityQueue<Chain> worstDistricts, List<Chain> allDistrict) {

		Chain bestChain = bestDistricts.poll();
		
		worstDistricts.remove(bestChain);
		
		
		Chain worstChain = worstDistricts.poll();
		bestDistricts.remove(worstChain);
		
		Node worst = bestChain.findWorstVoter();
		Node best = worstChain.findBestVoter();
		
		if (bestChain.getNetScore() - worstChain.getNetScore() < 50 ) {
			if (best != null && best.isInDistrict() == false && best.netScore>=0) {
				best.addToDistrict(worstChain);
				addNeighborhoodToDistrict(worstChain,best);
			}
		} else {
			Node worserest = worstChain.findWorstVoter();
			if (worserest != null && worserest.isInDistrict() == false && worserest.netScore < 0) {
				worserest.addToDistrict(worstChain);
				addNeighborhoodToDistrict(worstChain,worserest);
			}
		}
		
		if (worst != null && worst.isInDistrict() == false) {
			worst.addToDistrict(bestChain);
			addNeighborhoodToDistrict(bestChain,worst);
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
			if (head != null && head.isInDistrict() == false && head.party.equals(this.party)) {
				Chain newChain = new Chain(party);
				buildChainHelper(newChain, head);
				chains.add(newChain);
			}
			
			activeIndex++;
		}
	}
	
	private void buildChainHelper(Chain activeChain, Node activeNode) {

		if (activeNode.isInDistrict() == false && activeNode.party.equals(this.party) && activeChain.getSize() < census.getMaxVoter() / (2*this.numberOfDistricts)) {
			activeNode.addToDistrict(activeChain);
		
			for (Node activeNeighborhood : activeNode.getNeighbors()) {
				buildChainHelper(activeChain, activeNeighborhood);
			}
		}
	}
	
}
