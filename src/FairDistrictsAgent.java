




// notes
//refer to line 117 for refresher on current plan
// if netScore = 0 add
// if that is unavaible, add the voter with the netscore closest to 0
//otherwise, do not change what was already written
//additionaly I don't currently think i need the comparators I made, but perhaps for finding the voter with the closest netscore to 0, highly doubtful however

// or perhaps, check current chains ratio, if higher than 50 add a neighbor with <=0 net score, if lower than 50 add a neighborhood with >= nextscore


// use add most neutral voter to create a bunch of chains, then use comparator to sort based on which have the best ratios, add more

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;

public class FairDistrictsAgent {

    protected CensusMap census;
    Party party;
    int numberOfDistricts;

    protected PriorityQueue<Chain> chains;
    private static ChainComparatorRatio Ratio = null;




    public FairDistrictsAgent(CensusMap census, int numDistricts, Party party){

        if (Ratio == null){
            Ratio = new ChainComparatorRatio();

        }

        this.numberOfDistricts = numDistricts;
        this.census = census;
        this.party = party;
        chains =  new PriorityQueue<Chain>(1,Ratio);



    }
    public Chain[] growChains(){

        PriorityQueue<Chain> bestDistricts = new PriorityQueue<Chain>(1,Ratio);



      for (int i=0; i < this.numberOfDistricts; i++)
       {
            bestDistricts.add(chains.poll());// contains all districts in a heap, but max heap so that the best districts are here
       }
        // makes voters not in the top 5 chains available to re-add into the chains
        while (chains.isEmpty() == false)// makes all voters available
        {
            Iterator<Node> todelete = chains.poll().getChainIterator();
            while (todelete.hasNext()) {
                Node tofree= todelete.next();
                tofree.district = null;
                tofree.isInDistrict = false;
            }
        }

        int oldCount = 0;

        for (Chain c : bestDistricts) {
            oldCount=oldCount+c.getSize();
        }// how many voters are already in a district?


        int quikCount = oldCount;// how many voters are currently  in a district
        while (quikCount < this.census.getMaxVoter()) {//while there are still voters not in a district
            addNeutralVoters(bestDistricts);
            quikCount = 0;
            for (Chain c : bestDistricts) {
                quikCount=quikCount+c.getSize();
            }
            if (quikCount == oldCount)// if there was no voter available with the desired conditions, simply add best available voter
                addBestAvailable(bestDistricts);
            System.out.println(quikCount);
            oldCount = quikCount;
        }





        Chain[] toReturn = new Chain[this.numberOfDistricts];
        for (int i=0; i < this.numberOfDistricts; i++)
        {
            toReturn[i]=bestDistricts.poll();
            System.err.println(toReturn[i].getNetScore() + " : " + toReturn[i].getPartyCount() + " : " + toReturn[i].getSize() + " = " + ((double)toReturn[i].getPartyCount() ) / (double) toReturn[i].getSize());
        }


        return  toReturn;

    }

    public void addNeutralVoters(PriorityQueue<Chain> bestDistricts){
        for (Chain chain : bestDistricts){
            Node add = chain.findNeutral();
            if (add != null  && !add.isInDistrict && chain.getSize() < (2 * ((double)census.getMaxVoter() / numberOfDistricts))){
                add.addToDistrict(chain);
                for (Node neighbors : add.neighborHood){
                    if ( neighbors != null && !neighbors.isInDistrict){
                        neighbors.addToDistrict(chain);
                    }
                }
            }
        }
    }

    public void addBestAvailable(PriorityQueue<Chain> bestDistricts){
        for (Chain chain : bestDistricts){
            Node add = chain.findMostNeutralVoter();
            if (add != null  && !add.isInDistrict && chain.getSize() < (2 * ((double)census.getMaxVoter() / numberOfDistricts))){
                add.addToDistrict(chain);
                for (Node neighbors : add.neighborHood){
                    if ( neighbors != null && !neighbors.isInDistrict ){
                        neighbors.addToDistrict(chain);
                    }
                }
            }
        }
    }
    public void buildChains(){
        int voter = 0;

        while (voter < census.getMaxVoter()){
            Node add = census.getVoter(voter);
            if (!add.isInDistrict && add != null){
                Chain newChain = new Chain(party);
                add.addToDistrict(newChain);
                builder(newChain);
                chains.add(newChain);


            }

            voter++;
        }
    }

    public void builder(Chain chain){

        addAvailable(chain, chain.getSize());

    }

    public void addAvailable(Chain chain,int oldSize){

        Node add = chain.findMostNeutralVoter();

        if (add != null && !add.isInDistrict){

            add.addToDistrict(chain);
        }
        int currentSize = chain.getSize();

        if (currentSize != oldSize &&  currentSize < census.getMaxVoter() / (2*numberOfDistricts)){
            addAvailable(chain, currentSize);
        }

    }


}
