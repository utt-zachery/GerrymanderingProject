
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;


public class FairAgent {

    protected CensusMap census;
    private Party party;

    private int numberOfDistricts;

    private int majorityDistrictNum;
    private int oppositionDistrictNum;

    private PriorityQueue<Chain> majority;
    private PriorityQueue<Chain> minority;
    private static Comparator<Chain> singleCompPos = null;
    private static Comparator<Chain> singleCompNeg = null;

    public FairAgent(int numberOfDistricts, CensusMap census, Party party){

        //find the percent of the given party present in census  map
        double partyCount = 0;
        double percentParty;
        for (int i = 0; i < census.getMaxVoter(); i++){
            if(census.getVoter(i) != null && census.getVoter(i).party.equals(party)){
                partyCount++;
            }
        }
        percentParty = partyCount / census.getMaxVoter();

        this.numberOfDistricts = numberOfDistricts;
        this.census = census;
        this.party = party;
        double percentOpposition = 1 - percentParty;

        // if the given party is the majority, percent opposition to assign number of opposition districts
        if (percentParty >= .5){
            oppositionDistrictNum = (int) Math.ceil(percentOpposition* numberOfDistricts);
            majorityDistrictNum = numberOfDistricts - oppositionDistrictNum;
        }
        else { // else if perecent party is the minority, use percent party to assign number of opposition districts
            oppositionDistrictNum = (int) Math.ceil(percentParty * numberOfDistricts);
            majorityDistrictNum = numberOfDistricts - oppositionDistrictNum;
        }

        if (singleCompPos == null){
            singleCompPos = new ChainComparator();
            singleCompNeg = new ChainComparatorNegative();
        }
        majority = new PriorityQueue<Chain>(1,singleCompPos);
        minority = new PriorityQueue<Chain>(1, singleCompNeg);

    }
    public Chain[] grow(){
        PriorityQueue<Chain> bestMajorityDistricts = new PriorityQueue<Chain>(1, singleCompPos);
        PriorityQueue<Chain> bestOppositionDistricts = new PriorityQueue<Chain>(1,singleCompNeg);
        ArrayList<Chain> allDistricts = new ArrayList<Chain>();
        for (int i = 0; i < oppositionDistrictNum; i++){//add the best n-number of opposition districts
            allDistricts.add(minority.peek());
            bestOppositionDistricts.add(minority.poll());

        }
        for (int i = 0; i < majorityDistrictNum; i++){//add the best n-number of majority districts
            allDistricts.add(majority.peek());
            bestMajorityDistricts.add(majority.poll());
        }

        //delete the rest of the chains and free the nodes to be re-assigned
        while(!majority.isEmpty()){
            Iterator<Node> todelete = majority.poll().getChainIterator();
            while (todelete.hasNext()) {
                Node tofree= todelete.next();
                tofree.district = null;
                tofree.isInDistrict = false;
            }
        }
        while(!minority.isEmpty()){
            Iterator<Node> todelete = minority.poll().getChainIterator();
            while (todelete.hasNext()) {
                Node tofree= todelete.next();
                tofree.district = null;
                tofree.isInDistrict = false;
            }
        }

        int oldCount = 0;
        int quikCount = 0;
        //get current number of nodes assigned
        for (Chain chain : allDistricts){
            oldCount += chain.getSize();
        }
        quikCount = oldCount;
        while(quikCount < census.getMaxVoter()){
            oppositionGrowth(bestOppositionDistricts); // grow opposition districts
            majorityGrowth(bestMajorityDistricts); // grow majority districts
            quikCount = 0;
            for (Chain chain : allDistricts) {
                quikCount += chain.getSize();
            }
            //get new count of nodes that have been assigned
            if (quikCount == oldCount)// if there was no voter available with the desired conditions, simply add most neutral available voter
                growth(allDistricts);

            oldCount = quikCount;
        }
        //copy districts into array
        Chain[] toReturn  = new Chain[numberOfDistricts];
        for (int i = 0; i < numberOfDistricts; i++){
            toReturn[i] = allDistricts.get(i);
            System.out.println(allDistricts.get(i).getSize() + "  "  + allDistricts.get(i).getRatio());
        }
        return  toReturn;
    }


    private void growth(ArrayList<Chain> allDistricts){
        for (Chain chain : allDistricts){
            //find most net-neutral voter possible, add it and its neighbors if possible
            Node mostNeutral = chain.findMostNeutralVoter();
            if (mostNeutral != null  && !mostNeutral.isInDistrict){
                mostNeutral.addToDistrict(chain);
                for (Node neighbor : mostNeutral.neighborHood){
                    if (neighbor != null && !neighbor.isInDistrict){
                        neighbor.addToDistrict(chain);
                    }
                }
            }
        }
    }

    private void oppositionGrowth(PriorityQueue<Chain> opposition){
        for (Chain chain : opposition){
            Node voter = chain.findWorstVoter(); //find the worst voter from the perspective of the party given
            //add it and its neighbors if it is available and, at least 6 of its neighbors are available
            //this is to try an avoid districts from creating skinny off-shoots and make them more contiguous
            if (voter != null && !voter.isInDistrict ){
                if(voter.availableNeighborCount() >= 6){
                    voter.addToDistrict(chain);
                    for(Node neighbor : voter.neighborHood){
                        if (neighbor != null && !neighbor.isInDistrict){
                            neighbor.addToDistrict(chain);
                        }
                    }
                }
            }
        }
    }

    private void majorityGrowth(PriorityQueue<Chain> majority){
        for (Chain chain : majority){
            Node voter = chain.findBestVoter();//find the best voter from the perspective of the party given
            //add it and its neighbors if it is available and, at least 6 of its neighbors are available
            //this is to try an avoid districts from creating skinny off-shoots and make them more contiguous
            if (voter != null && !voter.isInDistrict){
                if(voter.availableNeighborCount() >= 6) {
                    voter.addToDistrict(chain);
                    for (Node neighbor : voter.neighborHood) {
                        if (neighbor != null && !neighbor.isInDistrict) {
                            neighbor.addToDistrict(chain);
                        }
                    }
                }
            }


        }
    }




    public void build(){
        int index = 0;
        int partySwitch = 0;
        //iterate through all of the voters
        while (index < census.getMaxVoter()){
            Node voter = census.getVoter(index);
            //if that voter is un-assigned create a new chain starting with that voter
            if (voter != null && !voter.isInDistrict){
                Chain newChain = new Chain(party);
                voter.addToDistrict(newChain);
                //switch between making the newly created chain a opposition chain and majority chain

                if (partySwitch == 0){// add best
                    builder(newChain, partySwitch);
                    majority.add(newChain);
                    partySwitch = 1;
                }
                else{//add worst
                    builder(newChain, partySwitch);
                    minority.add(newChain);
                    partySwitch = 0;

                }
            }

            index++;
        }
    }
    private void builder(Chain chain, int partySwitch){
        int oldSize = chain.getSize();
        int currentSize;
        Node voter = null;
        //if it is an opposition chain, add the worst voter
        //if it is a majority chain, add the best voter
        //-- and their neighbors, if available
        if (partySwitch == 0){
            voter = chain.findBestVoter();
        }
        else{
            voter = chain.findWorstVoter();
        }
        if (voter != null && !voter.isInDistrict){
            voter.addToDistrict(chain);
            for (Node neighbor : voter.neighborHood){
                if (neighbor != null && !neighbor.isInDistrict){
                    neighbor.addToDistrict(chain);
                }
            }
        }
        currentSize = chain.getSize();
        //continue to grow this chain as long as there are voters available to add and it is less then a set portion of the overall population
        if (currentSize != oldSize && currentSize < (census.getMaxVoter()/ (4*numberOfDistricts))){
            builder(chain, partySwitch);
        }
    }

}
