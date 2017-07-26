package manipulation.image.JPEG;

import java.util.ArrayList;

/**
 * <p>Class which handles Minimum Codec Units of a JPEG image
 * 
 * @author Ashley Allott
 */
public class MCU {
	
	public ArrayList<DataUnit> data;
	
	/**
	 * <p>Constructor, creates a new MCU
	 */
	public MCU(){
		data = new ArrayList<DataUnit>();
	}
	
	/**
	 * <p>Adds a DataUnit to the MCU
	 * 
	 * @param dUnit	the DataUnit to be added
	 */
	public void addDataUnit(DataUnit dUnit){
		data.add(dUnit);
	}
}
