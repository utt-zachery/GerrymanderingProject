import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
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
	BufferedImage first;
	
	public ExecuteListener(List<JSlider> currentList, JLabel districtsImage, CensusMap map, MapPane mainview,
			Party[] partyList, List<Chain> activeDistricts, JTabbedPane maintabs, JProgressBar executeProgress,
			JPanel innerPane, JButton executeButton, JLabel districtOveralyView) {
		
		super();
		this.districtOveralyView=districtOveralyView;
		this.currentList = currentList;
		this.districtsImage = districtsImage;
		this.map = map;
		this.mainview = mainview;
		this.partyList = partyList;
		this.activeDistricts = activeDistricts;
		this.maintabs = maintabs;
		this.executeProgress = executeProgress;
		this.innerPane=innerPane;
		this.executeButton=executeButton;
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
		
		map.buildMap(mainview);
		int numberDistricts = 5;
		map.getNeighbors();

		System.out.println("\nBeginning Phase 2: Chain building");
		GerrymanderAgent gerry = new GerrymanderAgent(map,partyList[iterCode], numberDistricts, this.executeProgress);
		gerry.buildChains();
		
		Chain[] districts = gerry.growChains();
		
		this.activeDistricts.clear();
		for (Chain c : districts)
			activeDistricts.add(c);
		
		BufferedImage first = map.drawVoters(mainview.getZoomFactor());
		
		for (int i=0; i < districts.length; i++)
			first=map.drawDistrict(mainview.getZoomFactor(), districts[i],first,i);
		
		System.out.println("Phase 2 Completed: All chains are built");
		
		Toolkit.getDefaultToolkit().beep();
		
		districtsImage.setIcon(new ImageIcon(first));
		districtsImage.repaint();
		districtsImage.revalidate();
		maintabs.setEnabledAt(1, true);
		maintabs.setEnabledAt(2, true);
		this.first=first;
		districtOveralyView.setIcon(new ImageIcon(first));
		innerPane.remove(executeProgress);
		innerPane.add(executeButton, BorderLayout.SOUTH);
		executeButton.repaint();
		executeButton.revalidate();
		innerPane.repaint();
		innerPane.revalidate();
	}
}
