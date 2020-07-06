import java.awt.Color;
import java.io.Serializable;

public class Party implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int PARTY_COUNT = 0;
	
	public int partyID;
	public String partyName;
	public Color partyColor;
	
	public Party(String partyName, Color partyColor) {
		this.partyID= PARTY_COUNT++;
		this.partyColor = partyColor;
		this.partyName=partyName;
	}
	
	@Override
	public boolean equals(Object toCheck) {
		if (toCheck instanceof Party)
			return ((Party)toCheck).partyID == this.partyID;
		return false;
	}
}
