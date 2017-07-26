package manipulation.image.JPEG;

/**
 * <p>Class which handles a DataUnit in of a JPEG image.
 * 
 * @author Ashley Allott
 */
public class DataUnit {
	public int dcValue;
	public int acValues[];
	private int acCtr;
	
	/**
	 * <p>Constructor, creates a empty DataUnit
	 */
	public DataUnit(){
		acValues = new int[63];
		acCtr = 0;
	}
	
	/**
	 * <p>Sets a specified amount of AC values to 0.
	 * 
	 * @param runLength	the length of AC value to be set to 0
	 */
	public void setZeroRun(int runLength){
		for(int i=0; i<runLength; i++){
			acValues[acCtr] = 0;
			acCtr++;
		}
	}
	
	/**
	 * <p>Sets the current AC value to the specified value.
	 * 
	 * @param ac	the value to set the current AC value to
	 */
	public void setCurrentAC(int ac){
		acValues[acCtr] = ac;
		acCtr++;
	}
}
