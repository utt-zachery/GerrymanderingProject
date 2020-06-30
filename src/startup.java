import java.awt.Color;

public class startup {

	public static void main(String[] args) {
		
		
		//Steps for building a census map
		
		//1. Load in the voters in random access 		O(n)
		//2. Scan the entire map for voters of the selected party that are next to each other and build chains
		//3. Build a maxheap of all chains by length
		//4. Select the N districts you want by the N largest chains
		//5. Build a maxheap and a minheap of the chains, arranged in net index value
		
		//6. ------------Iterations--------------
		//6a. Chain with the highest net index should add any node its touching with the lowest net index score
		//6b. Chain with the lowest net index should add any node in its graph with the highest net index score
		
		//7. Voter exchanges to balance each district
		
		
		
		Party democrat = new Party("Democratic Party", Color.blue);
		Party republican = new Party("Republican Party", Color.red);
		
		CensusMap baseMap = new CensusMap(100,100);
		int numberDistricts = 3;
		
		int demCount = 0;
		int repCount = 0;
		for (int x=0; x < baseMap.getWidth(); x++)
			for (int y=0; y < baseMap.getHeight(); y++) {
				
				int random_int = (int)(Math.random() * (2 - 1 + 1) + 2);
				
				if (random_int==2) {
					baseMap.addVoter(x, y, democrat);
					demCount++;
				} else if (random_int==3) {
					baseMap.addVoter(x, y, republican);
					repCount++;
				}
			}
				
		baseMap.getNeighbors();
	
		System.out.println("Phase 1 Completed: The Census Map is created");
		System.out.println("Democrats: " + demCount);
		System.out.println("Republicans: " + repCount);
		baseMap.saveImage("output.png", 10);
		
		System.out.println("\nBeginning Phase 2: Chain building");
		GerrymanderAgent gerry = new GerrymanderAgent(baseMap,democrat, numberDistricts);
		gerry.buildChains();
		//gerry.growChains();
		System.out.println("Phase 2 Completed: All chains are built");
	}

}
