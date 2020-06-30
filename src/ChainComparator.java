import java.util.Comparator;

public class ChainComparator implements Comparator<Chain> {

	public int compare(Chain o1, Chain o2) {
		return o2.getNetScore() - o1.getNetScore();
	}

}
