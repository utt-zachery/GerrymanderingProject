import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class PartyDistricts {

	public Party districtParty;
	protected PriorityQueue<Chain> chains;
	int numDistricts;
	static Comparator<Chain> singleCompPos = null;
	static Comparator<Chain> singleCompNeg = null;
	
	PriorityQueue<Chain> bestDistricts;
	PriorityQueue<Chain> worstDistricts;
	List<Chain> allDistrict;
	
	public PartyDistricts(Party districtParty, int numDistricts) {
		super();
		
		if (singleCompPos == null) {
			singleCompPos= new ChainComparator();
			singleCompNeg = new ChainComparatorNegative();
		}
		allDistrict = new ArrayList<Chain>();
		worstDistricts = new PriorityQueue<Chain>(1, PartyDistricts.singleCompNeg);
		bestDistricts = new PriorityQueue<Chain>(1, PartyDistricts.singleCompPos);
		this.districtParty = districtParty;
		this.numDistricts = numDistricts;
		this.chains = new PriorityQueue<Chain>(singleCompPos);
	}
	
	public void trimBestChains() {
		for (int i=0; i < this.numDistricts; i++)
		{
			bestDistricts.add(chains.peek());
			allDistrict.add(chains.peek());
			worstDistricts.add(chains.poll());
		}
		
		while (chains.isEmpty() == false)
		{
			Iterator<AbstractNode> todelete = chains.poll().getChainIterator();
			while (todelete.hasNext()) {
				AbstractNode tofree= todelete.next();
				tofree.divorceDistrict();
				
			}
		}
	}
	
	public int getCurrentDistrictsSize() {
		int oldCount = 0;
		
		for (Chain c : allDistrict) {
			oldCount=oldCount+c.getSize();
		}
		
		return oldCount;
	}
}
