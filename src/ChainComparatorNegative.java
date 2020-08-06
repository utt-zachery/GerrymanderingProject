import java.util.Comparator;

//Allows heap to sort districts
public class ChainComparatorNegative implements Comparator<Chain> {

	public int compare(Chain o1, Chain o2) {
		return o1.getNetScore() - o2.getNetScore();
	}

}
