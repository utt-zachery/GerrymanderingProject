import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

public class CensusMap {

	private Map<Integer, Node> censusData;
	private int width;
	private int height;
	
	public CensusMap(int width, int height) {
		this.width=width;
		this.height=height;
		this.censusData= new HashMap<Integer, Node>();
	}
	
	public void saveImage(String fileName, int pixelScale) {
		BufferedImage toSave = new BufferedImage(this.width * pixelScale, this.height * pixelScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D painter = toSave.createGraphics();
			for (Map.Entry<Integer,Node> entry : censusData.entrySet()) {
				if (entry.getValue().isInhabited) {
					painter.setPaint(entry.getValue().party.partyColor);
					painter.fillRect(entry.getValue().x*pixelScale, entry.getValue().y*pixelScale, pixelScale, pixelScale);
				}
			}
		 	File outputfile = new File(fileName);
		    try {
				ImageIO.write(toSave, "png", outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void addVoter(int x, int y, Party party) {
		int addressHash = x + width*y;
		Node toAdd = new Node(party,x,y);
		censusData.put(addressHash, toAdd);
	}
	
	public void insertSpacers() {
		int lastAddress = 0;
		
		Stack<Integer> toPut = new Stack<Integer>();
		Stack<Node> toPutNode = new Stack<Node>();
		
		for (Map.Entry<Integer,Node> entry : censusData.entrySet()) {
			if (entry.getKey() - lastAddress > 1) {
				for (int i= lastAddress+1; i < entry.getKey(); i++) {
					toPut.add(i);
					toPutNode.add(new EmptyNode(i % width,i / width));
				}
			}
			lastAddress = entry.getKey();
		}
		
		while (toPut.empty() == false) {
			censusData.put(toPut.pop(),toPutNode.pop());
		}
		
		for (int i= lastAddress+1; i < width*height; i++) {
			censusData.put(i, new EmptyNode(i % width,i / width));
		}
	}
}
