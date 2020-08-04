import java.util.List;

import javax.swing.JProgressBar;

public class GerrymanderAgent {
	
	protected CensusMap census;
	private JProgressBar executeProgress;
	
	public GerrymanderAgent(CensusMap census, JProgressBar executeProgress) {
		this.executeProgress=executeProgress;
		this.census = census;
	}
	
	public Chain[] growChains(PartyDistricts[] districtHolder) {
		System.out.println("Peek: " + districtHolder[0].chains.peek().getNetScore());
		
		districtHolder[0].trimBestChains();
		districtHolder[1].trimBestChains();
		
		int oldCount = districtHolder[0].getCurrentDistrictsSize() + districtHolder[1].getCurrentDistrictsSize();
		
		int quikCount = oldCount;
		while (quikCount < this.census.getMaxVoter()) {
			executeProgress.setValue((int) Math.round(100 * (double)quikCount/(double)this.census.getMaxVoter()));
			executeProgress.repaint();
			executeProgress.revalidate();
			doIteration(districtHolder[0], districtHolder[0].numDistricts+districtHolder[1].numDistricts);
			doIteration(districtHolder[1], districtHolder[0].numDistricts+districtHolder[1].numDistricts);
			quikCount = districtHolder[0].getCurrentDistrictsSize() + districtHolder[1].getCurrentDistrictsSize();
			
			if (quikCount == oldCount) {
				doStaleIteration(districtHolder[0].allDistrict);
				doStaleIteration(districtHolder[1].allDistrict);
			}
			
			oldCount = quikCount;
		}
		
		Chain[] toReturn = new Chain[districtHolder[0].numDistricts + districtHolder[1].numDistricts];
		for (int i=0; i < districtHolder[0].numDistricts; i++)
		{
			toReturn[i]=districtHolder[0].bestDistricts.poll();
			System.err.println(toReturn[i].getNetScore() + " : " + toReturn[i].getPartyCount() + " : " + toReturn[i].getSize() + " = " + ((double)toReturn[i].getPartyCount() ) / (double) toReturn[i].getSize());
		}
		for (int i=0; i < districtHolder[1].numDistricts; i++)
		{
			toReturn[i+districtHolder[0].numDistricts]=districtHolder[1].bestDistricts.poll();
			System.err.println(-1*toReturn[i+districtHolder[0].numDistricts].getNetScore() + " : " + toReturn[i+districtHolder[0].numDistricts].getPartyCount() + " : " + toReturn[i].getSize() + " = " + (1-((double)toReturn[i+districtHolder[0].numDistricts].getPartyCount() ) / (double) toReturn[i+districtHolder[0].numDistricts].getSize()));
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
	
	private void doIteration(PartyDistricts currentChains, int totalDistricts) {

		
		if (currentChains.numDistricts > 1) {
		Chain bestChain = currentChains.bestDistricts.poll();
		
		currentChains.worstDistricts.remove(bestChain);
		
		
		Chain worstChain = currentChains.worstDistricts.poll();
		currentChains.bestDistricts.remove(worstChain);
		
		Node worst = bestChain.findWorstVoter();
		Node best = worstChain.findBestVoter();
		
			if (best != null && best.isInDistrict() == false ) {
				best.addToDistrict(worstChain);
				addNeighborhoodToDistrict(worstChain,best);
			}
		
		
		if (worst != null && worst.isInDistrict() == false) {
			worst.addToDistrict(bestChain);
			addNeighborhoodToDistrict(bestChain,worst);
		}
		
		currentChains.bestDistricts.add(bestChain);
		currentChains.bestDistricts.add(worstChain);
		
		currentChains.worstDistricts.add(bestChain);
		currentChains.worstDistricts.add(worstChain);
		} else {
			Chain bestChain = currentChains.bestDistricts.peek();
			Node best = bestChain.findBestVoter();
			if (best != null && best.isInDistrict() == false && bestChain.getSize() < 1.5*(double)census.getMaxVoter() / (double)totalDistricts ) {
				best.addToDistrict(bestChain);
				addNeighborhoodToDistrict(bestChain,best);
			}
		}
	}
	
	public void buildChains(PartyDistricts[] districtHolder) {
		
		int activeIndex = 0;
		
		while (activeIndex < census.getMaxVoter()) {
			Node head = census.getVoter(activeIndex);
			if (head != null && head.isInDistrict() == false) {
				
				if (head.party.equals(districtHolder[0].districtParty)) {
					Chain newChain = new Chain(districtHolder[0].districtParty);
					buildChainHelper(newChain, head, districtHolder[0].districtParty, districtHolder[0].numDistricts + districtHolder[1].numDistricts);
					districtHolder[0].chains.add(newChain);
				} else {
					Chain newChain = new Chain(districtHolder[1].districtParty);
					buildChainHelper(newChain, head, districtHolder[1].districtParty,  districtHolder[0].numDistricts + districtHolder[1].numDistricts);
					districtHolder[1].chains.add(newChain);
				}
			}
			
			activeIndex++;
		}
	}
	
	private void buildChainHelper(Chain activeChain, Node activeNode, Party activeParty, int totalDistricts) {

		if (activeNode.isInDistrict() == false && activeNode.party.equals(activeParty) && activeChain.getSize() < (double)census.getMaxVoter() / (2.0 * (double)totalDistricts)) {
			activeNode.addToDistrict(activeChain);
		
			for (Node activeNeighborhood : activeNode.getNeighbors()) {
				buildChainHelper(activeChain, activeNeighborhood, activeParty, totalDistricts);
			}
		}
	}
	
}
