import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		
		double votePercentage = 0.5;
		
		Party democrat = new Party("Democratic Party", Color.blue);
		Party republican = new Party("Republican Party", Color.red);
		
		CensusMap baseMap = new CensusMap(200,200);
		int numberDistricts = 5;
		
		int demCount = 0;
		int repCount = 0;
		for (int x=0; x < baseMap.getWidth(); x++)
			for (int y=0; y < baseMap.getHeight(); y++) {
				
				double rand = Math.random();
				
				if (rand<= votePercentage) {
					baseMap.addVoter(x, y, democrat);
					demCount++;
				} else   {
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
		Chain[] districts = gerry.growChains();
		BufferedImage first = baseMap.drawDistrict(10);
		for (int i=0; i < districts.length; i++)
			first=baseMap.drawDistrict(10, districts[i],first);
		
		System.out.println("Phase 2 Completed: All chains are built");
		
		File outputfile = new File("districts.png");
	 	
	    try {
			ImageIO.write(first, "png", outputfile);
			System.out.println(outputfile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
