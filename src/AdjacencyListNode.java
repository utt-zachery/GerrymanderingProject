import java.util.ArrayList;
import java.util.List;

//List implementation of the graph
public class AdjacencyListNode extends AbstractNode {

	private List<AbstractNode> neighborHood;
	
	public AdjacencyListNode(Party party, int x, int y) {
		super(party, x, y);
		neighborHood = new ArrayList<AbstractNode>();
	}

	@Override
	public void addNeighbor(AbstractNode toAdd) {
		neighborHood.add(toAdd);
	}

	@Override
	public List<AbstractNode> getNeighbors() {
		return neighborHood;
	}
}
