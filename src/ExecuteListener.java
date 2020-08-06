import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

//GUI class that handles executing the algorithm in another thread
public class ExecuteListener implements Runnable {

	private List<JSlider> currentList;
	private JLabel districtsImage;
	private CensusMap map;
	private MapPane mainview;
	private Party[] partyList;
	private List<Chain> activeDistricts;
	private JTabbedPane maintabs;
	private JProgressBar executeProgress;
	private JPanel innerPane;
	private JButton executeButton;
	private JLabel districtOveralyView;
	private List<JSpinner> districtSelection;
	private JComboBox<String> graphOptions;
	private JPanel electionResults;
	
	BufferedImage first;
	BufferedImage second;
	BufferedImage third;
	
	public ExecuteListener(List<JSlider> currentList, JLabel districtsImage, CensusMap map, MapPane mainview,
			Party[] partyList, List<Chain> activeDistricts, JTabbedPane maintabs, JProgressBar executeProgress,
			JPanel innerPane, JButton executeButton, JLabel districtOveralyView, List<JSpinner> districtSelection, JComboBox<String> graphOptions, JPanel electionResults) {
		
		super();
		this.graphOptions=graphOptions;
		this.districtOveralyView=districtOveralyView;
		this.currentList = currentList;
		this.districtsImage = districtsImage;
		this.map = map;
		this.electionResults=electionResults;
		this.mainview = mainview;
		this.partyList = partyList;
		this.activeDistricts = activeDistricts;
		this.maintabs = maintabs;
		this.executeProgress = executeProgress;
		this.innerPane=innerPane;
		this.executeButton=executeButton;
		this.districtSelection = districtSelection;
	}

	@Override
	public void run() {
		
		int max = 101;
		int iterCode = 0;
		int q =0;
		for (JSlider iter: currentList) {
			if (iter.getValue() < max) {
				iterCode=q;
				max=iter.getValue();
			}
			q++;
		}
		
		map.buildMap(mainview, graphOptions.getSelectedIndex() == 0);
		
		int otherIndex = (iterCode ==0) ? 1:0;
		
		map.getNeighbors();

		PartyDistricts[] districtHolder = new PartyDistricts[2];
		districtHolder[0] = new PartyDistricts(partyList[iterCode], (int)districtSelection.get(iterCode).getValue());
		districtHolder[1] = new PartyDistricts(partyList[otherIndex],  (int)districtSelection.get(otherIndex).getValue());
		
		
		System.out.println("\nBeginning Phase 2: Chain building");
		GerrymanderAgent gerry = new GerrymanderAgent(map,this.executeProgress);
		gerry.buildChains(districtHolder);
		
		Chain[] districts = gerry.growChains(districtHolder);
		
		this.activeDistricts.clear();
		for (Chain c : districts)
			activeDistricts.add(c);
		
		BufferedImage first = map.drawVoters(mainview.getZoomFactor());
		BufferedImage second = map.drawVoters(mainview.getZoomFactor());
		BufferedImage third = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for (int i=0; i < districts.length; i++) {
			first=map.drawDistrict(mainview.getZoomFactor(), districts[i],first,i, districtHolder[0].numDistricts + districtHolder[1].numDistricts <= 5);
			second=map.drawDistrict(mainview.getZoomFactor(), districts[i],second,i, districtHolder[0].numDistricts + districtHolder[1].numDistricts <= 5);
			map.drawOutline(third,1,  districts[i]);
		}
		
		System.out.println("Phase 2 Completed: All chains are built");
		
		Toolkit.getDefaultToolkit().beep();
		
		
		
		districtsImage.setIcon(new ImageIcon(first));
		districtsImage.repaint();
		districtsImage.revalidate();
		
		
		JPanel allResults = new JPanel();
		JScrollPane godPane = new JScrollPane(allResults);
		BoxLayout container = new BoxLayout(allResults, BoxLayout.PAGE_AXIS);
		allResults.setLayout(container);
		
		
		this.first=first;
		this.second = second;
		this.third = third;
		
		
		
		for (int index = 0; index < activeDistricts.size(); index++ ) {
			Party expectedWin = partyList[0];
			Party expectedLoss = partyList[1];
			if (districts[index].getChainIterator().next().getParty().equals(partyList[1])){
				expectedWin = partyList[1];
				expectedLoss =  partyList[0];
			}
			allResults.add(resultsFactory(districts[index],map,index,expectedWin,expectedLoss));
			allResults.add(Box.createRigidArea(new Dimension(10,40)));
			electionResults.removeAll();
			electionResults.add(godPane);
			
		}
	
		
		
		electionResults.repaint();
		electionResults.revalidate();
		
		maintabs.setEnabledAt(1, true);
		maintabs.setEnabledAt(2, true);
		maintabs.setEnabledAt(3, true);
		
		
		
		districtOveralyView.setIcon(new ImageIcon(first));
		innerPane.remove(executeProgress);
		innerPane.add(executeButton, BorderLayout.SOUTH);
		executeButton.repaint();
		executeButton.revalidate();
		innerPane.repaint();
		innerPane.revalidate();
	}

	//Draws results
	JPanel resultsFactory(Chain district, CensusMap map, int districtIndex, Party expectedWin, Party expectedLose) {
		Party win;
		Party loss;
		
		int PC = district.getPartyCount();
		
		if ((double)PC / (double)district.getSize() >= 0.5) {
			win = expectedWin;
			loss = expectedLose;
		}
		else {
			win = expectedLose;
			loss = expectedWin;
		}
		
		JPanel toReturn = new JPanel(new BorderLayout());
		JLabel districtImage = new JLabel();
		JPanel innerPane = new JPanel();
		BoxLayout bl = new BoxLayout(innerPane, BoxLayout.X_AXIS);
		innerPane.setLayout(bl);
		
		BufferedImage distictIdentifier = CensusMap.copyImage(this.third);
		
		BufferedImage toDraw = map.drawResults(distictIdentifier, 1,district, win.partyColor);
		districtImage.setIcon(new ImageIcon(toDraw));
		innerPane.add(districtImage);
		
		
		String votePercentage = String.format("%.2f", (100*((float)PC / (float)district.getSize())));
		
		String htmlString = "<html> <font size='5'><b>District "+ (districtIndex +1) + "</b><br/> <i> Total Votes: " + district.getSize() + "</i> <p>" + win.partyName + ": " + PC + "</p><p>" + loss.partyName + ": " + (district.getSize() - PC) + "</p> <br> <p>" + win.partyName +" wins with " + votePercentage + "% of the vote! </p></font> </html>";
		
		JLabel text = new JLabel(htmlString);
		innerPane.add(Box.createRigidArea(new Dimension(20, 0)));
		innerPane.add(text);
		toReturn.add(innerPane, BorderLayout.NORTH);
		return toReturn;
	}
}
