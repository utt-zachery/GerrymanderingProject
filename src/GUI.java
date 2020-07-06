import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JPanel innerPane;
	CensusMap map;
	private List<Chain> activeDistricts;
	private int mapViewer;
	
	public GUI(final CensusMap map,Party majorityParty, Party minorityParty) {
		this.map = map;
		this.activeDistricts = new ArrayList<Chain>();
		JPanel mainpane = new JPanel();
		BoxLayout mainLayout = new BoxLayout(mainpane, BoxLayout.PAGE_AXIS);
		mainpane.setLayout(mainLayout);
		
		JPanel headPane = new JPanel(new FlowLayout());
		
		
		JLabel header1 = new JLabel("Party Information", SwingConstants.CENTER);
		headPane.add(header1);
		mainpane.add(headPane);
		
		
		final List<JSlider> currentList = new ArrayList<JSlider>();
		final List<JLabel> currentLabels = new ArrayList<JLabel>();
		
		mainpane.add(partyFactory(majorityParty.partyColor, majorityParty.partyName, currentList,currentLabels));
		mainpane.add(partyFactory(minorityParty.partyColor, minorityParty.partyName, currentList,currentLabels));
		
		
		
		innerPane = new JPanel(new BorderLayout());
		innerPane.add(mainpane, BorderLayout.NORTH);
		
		
		
		final JTabbedPane maintabs = new JTabbedPane();
		final JPanel holder = new JPanel(new BorderLayout());
		
		final JPanel buttonPane = new JPanel(new FlowLayout());
		final JLabel remaining = new JLabel((int)Math.round(map.getMaxVoter() * 0.5) + " voters remaining");
		JButton zout = new JButton("Zoom Out");
		JButton zin = new JButton("Zoom In");
		JButton reset = new JButton("Reset");
		JButton exportButton = new JButton("Export");
		JButton importButton = new JButton("Import");
		buttonPane.add(remaining);
		
		
		buttonPane.add(zout);
		buttonPane.add(zin);
		buttonPane.add(reset);
		
		buttonPane.add(exportButton);
		buttonPane.add(importButton);
		
		holder.add(buttonPane, BorderLayout.NORTH);
		
		
		final MapPane mainview = new MapPane(map, majorityParty, minorityParty, remaining,holder);
		
		
		final Party[] partyList = new Party[2];
		partyList[0] = majorityParty;
		partyList[1] = minorityParty;
		
		for (JSlider s1 : currentList) {
			s1.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					int caughtValue = ((JSlider)e.getSource()).getValue();
					int complement = 100 - caughtValue;
					int majorityPartyPercent = Math.max(caughtValue, complement);
					int index = 0;
					int i =0;
					int majoirtyPartyIndex = 0;
					for (JSlider q1 : currentList) {
						
						if (q1.getValue() == majorityPartyPercent) {
							majoirtyPartyIndex = i;
						}
						
						if (!q1.equals(((JSlider)e.getSource())))
							q1.setValue(complement);
						else
							index = i;
						
						i++;
					}
					
					Party newMajoirty = null;
					Party newMinority = null;
					if (majoirtyPartyIndex==0) {
						newMajoirty=partyList[0];
						newMinority= partyList[1];
					} else {
						newMajoirty=partyList[1];
						newMinority= partyList[0];
					}
						
						
					int minorityPartyPercent = Math.min(caughtValue, complement);
					
					double ratio = ((double)minorityPartyPercent)/100.0;
					int voters = (int)Math.round(ratio * map.getMaxVoter());
					mainview.setMinorityPartyVotes(voters);
					remaining.setText(voters + " voters remaining"); 
					mainview.setMinorityParty(newMinority);
					mainview.reset(newMajoirty);
					mainview.repaint();
					mainview.revalidate();
					buttonPane.repaint();
					buttonPane.revalidate();
					holder.repaint();
					holder.revalidate();
					
					int s=0;
					for (JLabel q1 : currentLabels) {
						if (s==index)
							q1.setText(caughtValue + "%");
						else
							q1.setText(complement + "%");
						s++;
					}
				}
			});
			
		}
		
		zout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mainview.zoomOut();
				mainview.repaint();
				mainview.revalidate();
				holder.repaint();
				holder.revalidate();
			}
		});
		
		zin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mainview.zoomIn();
				mainview.repaint();
				mainview.revalidate();
				holder.repaint();
				holder.revalidate();
			}
		});
		
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				int max = -1;
				int iterCode = 0;
				int i =0;
				for (JSlider iter: currentList) {
					if (iter.getValue() >= max) {
						iterCode=i;
						max=iter.getValue();
					}
					i++;
				}
				
				double ratio = ((double)(100- max))/100.0;
				int voters = (int)Math.round(ratio * map.getMaxVoter());
				mainview.setMinorityPartyVotes(voters);
				remaining.setText(voters + " voters remaining"); 
				
				mainview.reset(partyList[iterCode]);
				mainview.repaint();
				mainview.revalidate();
				buttonPane.repaint();
				buttonPane.revalidate();
				holder.repaint();
				holder.revalidate();
			}
			
		});
		
		exportButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int max = -1;
				int min = 101;
				int iterCode = 0;
				int iterMin =0;
				int i =0;
				for (JSlider iter: currentList) {
					if (iter.getValue() >= max) {
						iterCode=i;
						max=iter.getValue();
					}
					if (iter.getValue() < min) {
						iterMin=i;
						min=iter.getValue();
					}
					i++;
				}
				
				ExportableMomento toSave = new ExportableMomento(partyList[iterMin], partyList[iterCode], mainview.getData(), currentList.get(0).getValue(), currentList.get(1).getValue());
				toSave.export("activeMap.map");
			}
			
		});
		
		importButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				ExportableMomento toLoad = ExportableMomento.importMap("activeMap.map");
				currentList.get(0).setValue(toLoad.getPercentageParty1());
				currentList.get(1).setValue(toLoad.getPercentageParty2());
				
				int partyCap = (int)Math.round(map.getMaxVoter() * (double)Integer.min(toLoad.getPercentageParty1(), toLoad.getPercentageParty2()) / 100.0);
				mainview.importMap(toLoad.getMinorityParty(), toLoad.getMajorityParty(), toLoad.getPaneData(), partyCap);
				
			}
		});
		JScrollPane mainviewport = new JScrollPane(mainview);
		holder.add(mainviewport, BorderLayout.CENTER);
		
		JPanel districtMap = new JPanel(new BorderLayout());
		final JPanel buttonPane2 = new JPanel(new FlowLayout());
		JButton zout2 = new JButton("Zoom Out");
		final JLabel districtsImage = new JLabel();
		
		
		JButton zin2 = new JButton("Zoom In");
		zout2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mapViewer = Math.max(mapViewer-1, 1);
				BufferedImage first = map.drawDistrict(mapViewer);
				for (int i=0; i < activeDistricts.size(); i++)
					first=map.drawDistrict(mapViewer, activeDistricts.get(i),first,i);
				
				districtsImage.setIcon(new ImageIcon(first));
				districtsImage.repaint();
				districtsImage.revalidate();
			}
			
		});
		
		zin2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mapViewer++;
				BufferedImage first = map.drawDistrict(mapViewer);
				for (int i=0; i < activeDistricts.size(); i++)
					first=map.drawDistrict(mapViewer, activeDistricts.get(i),first,i);
				
				districtsImage.setIcon(new ImageIcon(first));
				districtsImage.repaint();
				districtsImage.revalidate();
			}
			
		});
		
		JButton exportButton2 = new JButton("Export");
		buttonPane2.add(zout2);
		buttonPane2.add(zin2);
		buttonPane2.add(exportButton2);
		districtMap.add(buttonPane2, BorderLayout.NORTH);
		
		JPanel paneMe = new JPanel(new BorderLayout());
		paneMe.add(districtsImage, BorderLayout.NORTH);
		JScrollPane districtPan = new JScrollPane(paneMe);
	
		districtMap.add(districtPan, BorderLayout.CENTER);
		
		maintabs.addTab("Census Map", holder);
		maintabs.addTab("Districts Map", districtMap);
		innerPane.add(maintabs, BorderLayout.CENTER);
		
		JButton execute = new JButton("Execute");
		innerPane.add(execute, BorderLayout.SOUTH);
		
		execute.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				maintabs.setEnabledAt(1, false);
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
				GerrymanderAgent gerry = new GerrymanderAgent(map,partyList[iterCode], numberDistricts);
				gerry.buildChains();
				
				Chain[] districts = gerry.growChains();
				for (Chain c : districts)
					activeDistricts.add(c);
				
				BufferedImage first = map.drawDistrict(mainview.getZoomFactor());
				for (int i=0; i < districts.length; i++)
					first=map.drawDistrict(mainview.getZoomFactor(), districts[i],first,i);
				
				System.out.println("Phase 2 Completed: All chains are built");
				
				districtsImage.setIcon(new ImageIcon(first));
				districtsImage.repaint();
				districtsImage.revalidate();
				maintabs.setEnabledAt(1, true);
			}
		});
		
		this.add(innerPane);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mapViewer = mainview.getZoomFactor();
	}
	
	public JPanel partyFactory(Color innerColor, String partyName, List<JSlider> currentList,List<JLabel> currentLabels) {
		JPanel party1Holder = new JPanel();
		BoxLayout party1 = new BoxLayout(party1Holder, BoxLayout.X_AXIS);
		party1Holder.setLayout(party1);
		JTextField partyname = new JTextField(25);
		partyname.setText(partyName);
		
		party1Holder.add(partyname);
		JButton colorChooser = new JButton("     ");
		colorChooser.setFocusPainted(false);
		colorChooser.setBackground(innerColor);
		colorChooser.setBorder(BorderFactory.createLineBorder(Color.black,2));
		party1Holder.add((Box.createRigidArea(new Dimension(5, 0))));
		party1Holder.add(colorChooser);
		JSlider p1 = new JSlider();
		
		p1.setMaximum(100);
		p1.setMajorTickSpacing(10);
		p1.setMinorTickSpacing(5);
		p1.setPaintTicks(true);
		party1Holder.add(p1);
		
		currentList.add(p1);
		
		JPanel totalPane = new JPanel(new FlowLayout());
		
		
		JLabel totalProportion = new JLabel("50%",SwingConstants.CENTER);
		totalPane.add(totalProportion);
		
		currentLabels.add(totalProportion);
		party1Holder.add(totalPane);
		return party1Holder;
	}
}