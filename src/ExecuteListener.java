import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

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
		
		for (int i=0; i < districts.length; i++) {
			first=map.drawDistrict(mainview.getZoomFactor(), districts[i],first,i, districtHolder[0].numDistricts + districtHolder[1].numDistricts <= 5);
			second=map.drawDistrict(mainview.getZoomFactor(), districts[i],second,i, districtHolder[0].numDistricts + districtHolder[1].numDistricts <= 5);
		}
		
		System.out.println("Phase 2 Completed: All chains are built");
		
		Toolkit.getDefaultToolkit().beep();
		
		districtsImage.setIcon(new ImageIcon(first));
		districtsImage.repaint();
		districtsImage.revalidate();
		
		JPanel toAdd = printResult(districts,  districts[0],  map);
		this.electionResults.add(toAdd);
		
		this.electionResults.repaint();
		this.electionResults.revalidate();
		
		maintabs.setEnabledAt(1, true);
		maintabs.setEnabledAt(2, true);
		maintabs.setEnabledAt(3, true);
		
		
		
		this.first=first;
		this.second = second;
		districtOveralyView.setIcon(new ImageIcon(first));
		innerPane.remove(executeProgress);
		innerPane.add(executeButton, BorderLayout.SOUTH);
		executeButton.repaint();
		executeButton.revalidate();
		innerPane.repaint();
		innerPane.revalidate();
	}
	
	public JPanel printResult(Chain[] districts, Chain district, CensusMap map) {
		JPanel toReturn = new JPanel();
		JLabel imageControl = new JLabel();
		
		BufferedImage allResult = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
		map.drawResult(allResult, 1, districts, district);
		imageControl.setIcon(new ImageIcon(allResult));
		toReturn.add(imageControl);
		
		return toReturn;
	}
}
