import java.util.Comparator;

public class ChainComparatorRatio implements Comparator<Chain> {
    @Override
    public int compare(Chain o1, Chain o2) {

        if (Math.abs(o1.getRatio() - .5) > Math.abs(o2.getRatio() - .5)){// if o1 is closer to 50%, its the better district
            return  5;
        }
        else if (Math.abs(o1.getRatio() - .5) < Math.abs(o2.getRatio() - .5)){//if o1 is farther its the worst district
            return -1;
        }
        else{ // else there both at 50%
            return 0;
        }
    }
}
