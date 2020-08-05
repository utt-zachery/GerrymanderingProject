import java.util.ArrayList;
import java.util.List;


//Per our project feedback, we implement the Adjacency Matrix
//The Adjaency Matrix performs TERRIBLY
//		Uses up to 3 GB of MEMORY!
//		Increases runtime by orders of magnitude


public class AdjacencyMatrixNode extends AbstractNode {

	private static boolean[][] matrix;
	private int hashCode;
	private CensusMap allVoters;
	
	public AdjacencyMatrixNode(Party party, int x, int y,CensusMap allVoters) {
		super(party, x, y);
		this.hashCode=x + allVoters.getWidth()*y;
		this.allVoters = allVoters;
		
		if (matrix == null) {
			matrix = new boolean[allVoters.getMaxVoter()][allVoters.getMaxVoter()];
			for (int q=0; q<allVoters.getMaxVoter(); q++) {
				for (int p=0;p<allVoters.getMaxVoter(); p++) {
					matrix[q][p] = false;
				}
			}
		}
	}

	@Override
	public List<AbstractNode> getNeighbors() {
		List<AbstractNode> toReturn = new ArrayList<AbstractNode>();
		
		boolean[] row = matrix[this.hashCode];
		
		for (int i=0; i < row.length; i++) {
			if (row[i] == true) {
 				toReturn.add(allVoters.getVoter(i));
			}
		}
		return toReturn;
	}

	@Override
	public void addNeighbor(AbstractNode toAdd) {
		matrix[this.hashCode][toAdd.getX() + this.allVoters.getWidth() * toAdd.getY()] = true;
	}
}
