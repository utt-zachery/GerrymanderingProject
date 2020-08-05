import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MapPane extends JPanel{
	private static final long serialVersionUID = 1L;
	private int zoomFactor;
	private CensusMap map;
	private Map<Integer, Party> paneData;
	private int otherParty = 0;
	private int partyCap = 0;
	private Party minorityParty;
	private Party majorityParty;
	private JLabel remainingVoters;
	
	public MapPane(final CensusMap map, Party minorityParty, Party majorityParty, JLabel remainingVoters, JPanel holder) {
		this.zoomFactor = 5;
		this.map=map;
		this.setPreferredSize(new Dimension(zoomFactor*map.getWidth(), zoomFactor*map.getHeight()));
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		this.paneData = new HashMap<Integer, Party>();
		this.minorityParty=minorityParty;
		this.majorityParty=majorityParty;
		reset(majorityParty);
		MouseProcesser mp = new MouseProcesser(this,holder);
		this.partyCap = (int)Math.round(map.getMaxVoter() * 0.5);
		this.addMouseListener(mp);
		this.addMouseMotionListener(mp);
		this.remainingVoters=remainingVoters;
	}
	
	public void clickVoter() {
		if (this.otherParty < partyCap)
		{
			this.otherParty++;
			remainingVoters.setText(this.getRemainingVoters() + " voters remaining");
			remainingVoters.repaint();
			remainingVoters.revalidate();
		}
	}

	public void importMap(Party minorityParty, Party majorityParty, Map<Integer, Party> paneData, int partCap) {
		this.majorityParty = majorityParty;
		this.minorityParty = minorityParty;
		this.paneData=paneData;
		this.partyCap=partCap;
	}
	
	public void export(String fileName) {
		try
        {
               FileOutputStream fos = new FileOutputStream(fileName);
               ObjectOutputStream oos = new ObjectOutputStream(fos);
               oos.writeObject(this.paneData);
               oos.close();
               fos.close();
               System.out.printf("Serialized HashMap data is saved in " + fileName);
        }catch(IOException ioe)
         {
               ioe.printStackTrace();
         }
	}
	
	public int getCurrentMinorityPartyTotal() {
		return this.otherParty;
	}
	
	public int getRemainingVoters() {
		return this.partyCap-this.otherParty;
	}
	
	public void setRemainingVoters(int remainingVoters) {
		this.otherParty = this.partyCap-remainingVoters;
	}
	
	public Map<Integer, Party> getData() {
		return this.paneData;
	}
	
	public Party getMinorityParty() {
		return minorityParty;
	}
	
	public Party getMajoirtyParty() {
		return majorityParty;
	}
	
	public int getZoomFactor() {
		return this.zoomFactor;
	}
	
	public Map<Integer, Party> getPaneData() {
		return this.paneData;
	}
	
	public CensusMap getCensusMap() {
		return this.map;
	}
	
	public void zoomOut() {
		this.zoomFactor = Math.max(1, zoomFactor - 1);
	}
	
	public void zoomIn() {
		this.zoomFactor = ( zoomFactor + 1);
	}

	public void setMinorityParty(Party minorityParty) {
		this.minorityParty=minorityParty;
	}
	
	public void setMinorityPartyVotes(int votes) {
		this.partyCap=votes;
	}
	
	public void reset(Party majoirtyParty) {
		this.otherParty = 0;
		paneData.clear();
		for (int x=0; x<map.getWidth(); x++) {
			for (int y=0; y<map.getHeight(); y++) {
				paneData.put(x+y*map.getWidth(), majoirtyParty);
			}
		}
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		this.setPreferredSize(new Dimension(zoomFactor*map.getWidth(), zoomFactor*map.getHeight()));
		for (Map.Entry<Integer,Party> entry : paneData.entrySet()) {
			g.setColor(entry.getValue().partyColor);
			g.fillRect(zoomFactor*(entry.getKey() / map.getWidth()),zoomFactor* (entry.getKey() % map.getWidth()), zoomFactor, zoomFactor);
		}	
	}
}
