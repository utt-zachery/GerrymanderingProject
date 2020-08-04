import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

public class CensusMap {

	private static final Color COLORS[] = {new Color(239, 71, 111), new Color(255, 209, 102), new Color(6, 214, 160), new Color(17, 138, 178), new Color(7, 59, 76)};
	private static final Color COLORS2[] = {new Color(0, 0, 128), new Color(0, 128, 128), new Color(0, 130, 200), new Color(128, 0, 0), new Color(128, 128, 0), new Color(128, 128, 128), new Color(145, 30, 180), new Color(170, 110, 40), new Color(170, 255, 195), new Color(210, 245, 60), new Color(220, 190, 255), new Color(230, 25, 75), new Color(240, 50, 230), new Color(245, 130, 48), new Color(250, 190, 212), new Color(255, 215, 180), new Color(255, 225, 25), new Color(255, 250, 200), new Color(255, 255, 255), new Color(60, 180, 75), new Color(70, 240, 240)};
	private Map<Integer, Node> censusData;
	private int width;
	private int height;
	
	public CensusMap(int width, int height) {
		this.width=width;
		this.height=height;
		this.censusData= new HashMap<Integer, Node>();
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void buildMap(MapPane input) {
		
		this.censusData.clear();
		
		for (Map.Entry<Integer,Party> entry : input.getData().entrySet()) {
			this.addVoter(entry.getKey() / input.getCensusMap().getWidth(), entry.getKey() % input.getCensusMap().getWidth(), entry.getValue());
		}
		
	}
	
	public void getNeighbors() {
		for (int x=0; x < width; x++) {
			for (int y=0; y < height; y++) {
				
				int mainAddressHash = x + width*y;
				
				if (x-1 >= 0) {
					this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash-1));
			
					if (y-1 >=0 ) {
						this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash-width-1));
					}
					
					if (y + 1 < height ) {
						this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash+width-1));
					}
				}
				
				if (x+1<width) {
					this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash+1));
					
					if (y-1 >=0 ) {
						this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash-width+1));
					}
					
					if (y + 1 < height ) {
						this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash+width+1));
					}
				}
				
				if (y-1 >=0 ) {
					this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash-width));
				}
				
				if (y + 1 < height ) {
					this.censusData.get(mainAddressHash).addNeighbor(this.censusData.get(mainAddressHash+width));
				}
			}
		}
	}
	
	public void saveImage(String fileName, int pixelScale) {
		BufferedImage toSave = new BufferedImage(this.width * pixelScale, this.height * pixelScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D painter = toSave.createGraphics();
			for (Map.Entry<Integer,Node> entry : censusData.entrySet()) {
					painter.setPaint(entry.getValue().party.partyColor);
					painter.fillRect(entry.getValue().x*pixelScale, entry.getValue().y*pixelScale, pixelScale, pixelScale);
			}
			
		 	File outputfile = new File(fileName);
		 	
		    try {
				ImageIO.write(toSave, "png", outputfile);
				System.out.println(outputfile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	public BufferedImage drawEdges(BufferedImage map, int pixelScale, Chain districts) {
		BufferedImage toReturn = copyImage(map);
		Graphics2D painter = toReturn.createGraphics();
		Iterator<Node> allNodes = districts.getChainIterator();
		while (allNodes.hasNext()) {
				Node next = allNodes.next();
				next.detectEdge();
				if (next.isDistrictEdge() || next.x ==0 || next.y==0 || next.x == this.width-1 || next.y == this.height - 1)
					painter.setPaint(Color.black);
				else
					painter.setPaint(next.party.partyColor);
				painter.fillRect(next.x*pixelScale, next.y*pixelScale, pixelScale, pixelScale);
		}
		
		return toReturn;
	}
	
	public BufferedImage drawVoters(int pixelScale) {
		BufferedImage toSave = new BufferedImage(this.width * pixelScale, this.height * pixelScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D painter = toSave.createGraphics();
			for (Map.Entry<Integer,Node> entry : censusData.entrySet()) {
					painter.setPaint(entry.getValue().party.partyColor);
					painter.fillRect(entry.getValue().x*pixelScale, entry.getValue().y*pixelScale, pixelScale, pixelScale);
			}
			return toSave;
	}
	
	public BufferedImage drawDistrict(int pixelScale,Chain district, BufferedImage backdrop, int index, boolean goodColors) {
		Graphics2D painter = backdrop.createGraphics();
		
		if (goodColors)
			painter.setPaint(COLORS[index]);
		else
			painter.setPaint(COLORS2[index]);
			Iterator<Node> toIterate = district.getChainIterator();
			while (toIterate.hasNext()) {
				Node toDraw = toIterate.next();
				painter.fillRect(toDraw.x*pixelScale, toDraw.y*pixelScale, pixelScale, pixelScale);
				
			}
			
			return backdrop;
	}
	
	
	public void addVoter(int x, int y, Party party) {
		int addressHash = x + width*y;
		Node toAdd = new Node(party,x,y,addressHash);
		censusData.put(addressHash, toAdd);
	}
	
	public Node getVoter(int addressHash) {
		return this.censusData.get(addressHash);
	}
	
	public int getMaxVoter() {
		return width*height;
	}
}
