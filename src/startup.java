import java.awt.Color;

public class startup {

	public static void main(String[] args) {
		//Steps for building a census map
		
		//1. Load in the voters in random access 		O(n)
		//2. Insert spacers to represent non-voters		O(width*height-n)
		//3. Link all pointers							O(width*height)
		
		Party democrat = new Party("Democratic Party", Color.blue);
		Party republican = new Party("Republican Party", Color.red);
		
		CensusMap baseMap = new CensusMap(100,100);
		for (int x=0; x < 100; x++)
			for (int y=0; y < 100; y++) {
				
				int random_int = (int)(Math.random() * (2 - 1 + 1) + 2);
				
				if (random_int==2)
					baseMap.addVoter(x, y, democrat);
				else if (random_int==3)
					baseMap.addVoter(x, y, republican);
			}
		
		baseMap.saveImage("output.png", 10);
	}

}
