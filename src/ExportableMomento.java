import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class ExportableMomento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Party minorityParty;
	private Party majorityParty;
	private Map<Integer, Party> paneData;
	private int percentageParty1;
	private int percentageParty2;
	
	public ExportableMomento() {};
	
	public ExportableMomento(Party minorityParty, Party majorityParty, Map<Integer, Party> paneData, int percentageParty1, int percentageParty2) {
		this.minorityParty=minorityParty;
		this.majorityParty=majorityParty;
		this.paneData=paneData;
		this.percentageParty1=percentageParty1;
		this.percentageParty2=percentageParty2;
	}
	
	public static ExportableMomento importMap(String fileName) {
	      try
	      {
	         FileInputStream fis = new FileInputStream(fileName);
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         ExportableMomento toReturn = (ExportableMomento) ois.readObject();
	         ois.close();
	         fis.close();
	         return toReturn;
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         return null;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("Class not found");
	         c.printStackTrace();
	         return null;
	      }
	}
	
	public void export(String fileName) {
		try
        {
               FileOutputStream fos = new FileOutputStream(fileName);
               ObjectOutputStream oos = new ObjectOutputStream(fos);
               oos.writeObject(this);
               oos.close();
               fos.close();
               System.out.printf("Serialized HashMap data is saved in " + fileName);
        }catch(IOException ioe)
         {
               ioe.printStackTrace();
         }
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Party getMinorityParty() {
		return minorityParty;
	}

	public Party getMajorityParty() {
		return majorityParty;
	}

	public Map<Integer, Party> getPaneData() {
		return paneData;
	}

	public int getPercentageParty1() {
		return percentageParty1;
	}

	public int getPercentageParty2() {
		return percentageParty2;
	}
	
}
