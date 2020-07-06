import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JPanel;

public class MouseProcesser implements MouseListener, MouseMotionListener {

	private MapPane mainPane;
	private MouseEvent lastVoter;
	private JPanel holder;
	public MouseProcesser(MapPane mainPane,JPanel holder) {
		this.mainPane=mainPane;	
		this.holder=holder;
	}
	
	
	public void processVoter(int x, int y) {

		if (this.mainPane.getRemainingVoters() > 0) {
		int xMapped = x / mainPane.getZoomFactor();
		int yMapped = y / mainPane.getZoomFactor();
		
		if (x < this.mainPane.getZoomFactor() * this.mainPane.getCensusMap().getWidth() && x>=0)
			if (y < this.mainPane.getZoomFactor() * this.mainPane.getCensusMap().getHeight() && y>= 0)
			{
			
				int indexHash = xMapped * this.mainPane.getCensusMap().getWidth() + yMapped;
				
				
				if (!this.mainPane.getPaneData().get(indexHash).equals(this.mainPane.getMinorityParty())) {
					this.mainPane.getPaneData().remove(indexHash);
					this.mainPane.getPaneData().put(indexHash, this.mainPane.getMinorityParty());
			
					this.mainPane.clickVoter();
					
					this.mainPane.repaint();
					this.mainPane.revalidate();
				}
			}
		}
	
	}
	public void mouseDragged(MouseEvent e) {
		int xMapped2 = e.getX() / mainPane.getZoomFactor();
		int yMapped2 = e.getY() / mainPane.getZoomFactor();
		
		int xMapped1 = this.lastVoter.getX() / mainPane.getZoomFactor();
		int yMapped1 = this.lastVoter.getY() / mainPane.getZoomFactor();
		
		int delX = xMapped2 - xMapped1;
		int delY = yMapped2 - yMapped1;
		
		HashSet<Point> pointCollection = new HashSet<Point>();
		
		double t = 0.0;
		
		while (t <= 1.0) {
			
			int newX = (int)(xMapped1 + t*delX);
			int newY = (int)(yMapped1 + t*delY);
			
			Point testPt = new Point(newX, newY);
			if (!pointCollection.contains(testPt)) {
				pointCollection.add(testPt);
			}
			
			t=t+0.01;
		}
		
		Iterator<Point> it = pointCollection.iterator();
	     while(it.hasNext()){
	    	 Point l = it.next();
	    	 processVoter(mainPane.getZoomFactor()*l.x, mainPane.getZoomFactor()*l.y);
	     }
		
		this.lastVoter = e;
		holder.repaint();
		holder.revalidate();
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		this.lastVoter = e;
		processVoter(e.getX(), e.getY());
		holder.repaint();
		holder.revalidate();
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
