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
