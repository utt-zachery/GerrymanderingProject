import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class startup {
	public static void main(String args[]) {

		//Entry point for the application
		
		
		//Establish Swing look and feel
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
		
		// Initalize
		int censusWidth = 320;
		int censusHeight = 320;
		
		Party democrat = new Party("Democratic Party", java.awt.Color.blue);
		Party republican = new Party("Republican Party", java.awt.Color.red);
	
		CensusMap baseMap = new CensusMap(censusWidth,censusHeight);
		JFrame gui = new GUI(baseMap,republican,democrat );
		gui.setVisible(true);
		
		
		/**
    **/
	}
}
