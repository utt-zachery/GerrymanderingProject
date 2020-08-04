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
	//gerrymander agenet constructor
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
		//get the best n-number districts for the selected party that were built using buildChains() and buildChainsHelper()
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			bestDistricts.add(chains.peek());
			allDistrict.add(chains.peek());
			worstDistricts.add(chains.poll());
		}
		
		//delete the rest of the chains and free up their nodes for reassignment 
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
		//acquire the current count for how many nodes have been assigned a district
		for (Chain c : allDistrict) {
			oldCount=oldCount+c.getSize();
		}
		int quikCount = oldCount;
		//iterate until all nodes have been assigned to a district
		while (quikCount < this.census.getMaxVoter()) {
			executeProgress.setValue((int) Math.round(100 * (double)quikCount/(double)this.census.getMaxVoter()));
			executeProgress.repaint();
			executeProgress.revalidate();
			doIteration(bestDistricts, worstDistricts, allDistrict); //assigns nodes
			quikCount = 0;
			//get the new count for nodes that have been assigned
			for (Chain c : allDistrict) {
				quikCount=quikCount+c.getSize();
			}
			//if no new nodes have been added, assign nodes following a different  criteria
			if (quikCount == oldCount)
				doStaleIteration(allDistrict);
			//when assigning nodes for the iteration has finished, assign the old count of assigned nodes to the current count
			oldCount = quikCount;
		}
		//copy the districts into an array and print out demographics, return the array
		Chain[] toReturn = new Chain[this.numberOfDistricts];
		for (int i=0; i < this.numberOfDistricts; i++)
		{
			toReturn[i]=bestDistricts.poll();
			System.err.println(toReturn[i].getNetScore() + " : " + toReturn[i].getPartyCount() + " : " + toReturn[i].getSize() + " = " + ((double)toReturn[i].getPartyCount() ) / (double) toReturn[i].getSize());
		}
		
		
		return toReturn;
	}

	private void doStaleIteration(List<Chain> allDistricts) {
	//add the best available voter
	//this is as defined as the voter with the highest net-score, or the number of the given party minus the number of the opposition within the neighborhood
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
		//add all available neighbor nodes into the district
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
	//create first set of districts
	public void buildChains() {
		
		int activeIndex = 0;
		//iterate through all nodes
		while (activeIndex < census.getMaxVoter()) {
			Node head = census.getVoter(activeIndex);
			//if that node is available, create a new district and add that node
			if (head != null && head.isInDistrict() == false && head.party.equals(this.party)) {
				Chain newChain = new Chain(party);
				buildChainHelper(newChain, head);
				chains.add(newChain);
			}
			
			activeIndex++;
		}
	}
	
	private void buildChainHelper(Chain activeChain, Node activeNode) {
		//if the chain is under a certain size, and the Node "activeNode" is available to add, add the node and all of its available neighbors, recursivley adding 
		//neighbors in this manner
		if (activeNode.isInDistrict() == false && activeNode.party.equals(this.party) && activeChain.getSize() < census.getMaxVoter() / (2*this.numberOfDistricts)) {
			activeNode.addToDistrict(activeChain);
		
			for (Node activeNeighborhood : activeNode.getNeighbors()) {
				buildChainHelper(activeChain, activeNeighborhood);
			}
		}
	}
	
}
