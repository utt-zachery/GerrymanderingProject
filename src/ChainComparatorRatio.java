import java.util.Comparator;

public class ChainComparatorRatio implements Comparator<Chain> {

    @Override
    public int compare(Chain o1, Chain o2) {
        double ratioOne = (o1.getSize() /2) + o1.getNetScore();
        ratioOne = ratioOne / o1.getSize();
        double ratioTwo = (o2.getSize() /2) + o2.getNetScore();
        ratioTwo = ratioTwo / o2.getSize();

        return (int) (ratioTwo / ratioOne);
    }

}
