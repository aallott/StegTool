package manipulation.image.JPEG;

/**
 * Class which handles the Components of a JPEG image.
 * 
 * @author Ashley Allott
 */
public class Component {
	public static final int SCAN_TYPE_NONINTERLEAVED = 0;
	public static final int SCAN_TYPE_INTERLEAVED = 1;
	
	public int scanType;
	
	public int componentID;
	public int componentHorizontalFactor;
	public int componentVerticalFactor;
	public int quantizationTableID;
	
	public int dcTableID;
	public int acTableID;
	
	public int pixelX;
	public int pixelY;
	
	public int MCUx;
	public int MCUy;
	
	/**
	 * Constructor, creates a new component using the passed parameters.
	 * 
	 * @param scanType					the type of scan the JPEG file uses
	 * @param componentID				the id of the component
	 * @param componentHorizontalFactor	the horizontal sampling value
	 * @param componentVerticalFactor	the vertical sampling value
	 * @param quantizationTableID		the id of the quantization value used for the component
	 */
	public Component(int scanType, int componentID, int componentHorizontalFactor, int componentVerticalFactor, int quantizationTableID){
		this.scanType = scanType;
		this.componentID = componentID;
		this.componentHorizontalFactor = componentHorizontalFactor;
		this.componentVerticalFactor = componentVerticalFactor;
		this.quantizationTableID = quantizationTableID;
	}
	
	/**
	 * <p>Calculates the amount of horizontal pixels a data unit spans
	 * @param xMax	the maximum component horizontal sampling in the JPEG image
	 */
	public void calPixelX(int xMax){
		this.pixelX = 8 * xMax / this.componentHorizontalFactor;
	}
	
	/**
	 *  <p>Calculates the amount of vertical pixels a data unit spans
	 *  
	 * @param yMax	the maximum component vertical sampling in the JPEG image
	 */
	public void calPixelY(int yMax){
		this.pixelY = 8 * yMax / this.componentVerticalFactor;
	}
	
	/**
	 * <p>Calculates the amount of MCU's in a Non-Interleaved JPEG image.
	 * 
	 * @param imageWidth	the width of the image
	 * @param imageHeight	the height of the image
	 */
	public void calMCU(int imageWidth, int imageHeight){
		this.MCUx = (imageWidth + pixelX - 1) / pixelX;
		this.MCUy = (imageHeight + pixelY- 1) / pixelY;
	}
	
	/**
	  * <p>Calculates the amount of MCU's in a Interleaved JPEG image.
	  * 
	 * @param imageWidth	the width of the image
	 * @param imageHeight	the height of the image
	 * @param xMax			maximum horizontal sampling of all components in the image
	 * @param yMax			maximum vertical sampling of all components in the image
	 */
	public void calMCUInterleaved(int imageWidth, int imageHeight, int xMax, int yMax){
		this.MCUx = (imageWidth + 8 * xMax - 1) / pixelX;
		this.MCUy = (imageHeight + 8 * yMax - 1) / pixelY;
	}
}
