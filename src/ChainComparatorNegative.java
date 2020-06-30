import java.util.Comparator;

public class ChainComparatorNegative implements Comparator<Chain> {

	public int compare(Chain o1, Chain o2) {
		return o1.getNetScore() - o2.getNetScore();
	}

}
