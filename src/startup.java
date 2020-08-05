import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class startup {
	public static void main(String args[]) {

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
		
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception er) {
			try {
	            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	        		UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		}
		
		
	
		
		int censusWidth = 200;
		int censusHeight = 200;
		
		Party democrat = new Party("Democratic Party", java.awt.Color.blue);
		Party republican = new Party("Republican Party", java.awt.Color.red);
	
		CensusMap baseMap = new CensusMap(censusWidth,censusHeight);
		JFrame gui = new GUI(baseMap,republican,democrat );
		gui.setVisible(true);
		
		
		/**
    **/
	}
}
