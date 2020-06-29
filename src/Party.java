import java.awt.Color;
import java.awt.Paint;

public class Party {
	private static int PARTY_COUNT = 0;
	
	public int partyID;
	public String partyName;
	public Paint partyColor;
	
	public Party(String partyName, Color partyColor) {
		this.partyID= PARTY_COUNT++;
		this.partyColor = partyColor;
	}
	
	@Override
	public boolean equals(Object toCheck) {
		if (toCheck instanceof Party)
			return ((Party)toCheck).partyID == this.partyID;
		return false;
	}
}
