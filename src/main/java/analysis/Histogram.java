package analysis;

import java.awt.image.BufferedImage;
import java.io.IOException;

import manipulation.WorkFile;

/**
 * <p>Calculates pixel intensity data for display by HistogramChart
 * 
 * @author Ashley Allott
 */
public class Histogram {
	
	public static final int HISTOGRAM_DATA_RED = 1;
	public static final int HISTOGRAM_DATA_GREEN = 2;
	public static final int HISTOGRAM_DATA_BLUE = 3;
	
	private WorkFile workFile;
	private BufferedImage dBI;
	
	private int red[];
	private int green[];
	private int blue[];
	
	/**
	 * <p>Constructor. Initialises data.
	 * 
	 * @param workFile WorkFile containing the file to be analysed
	 */
	public Histogram(WorkFile workFile){
		this.workFile = workFile;
		red = new int[256];
		green = new int[256];
		blue = new int[256];
	}
	
	/**
	 * <p>Public method used to call internal method to calculate intensity
	 * values.
	 */
	public void calValues(){
		if(workFile.fileType != WorkFile.FILE_TYPE_IMAGE){
			throw new UnsupportedOperationException("The file supplied is not an image!");
		}
		try {
			this.genValues(workFile.getCoverBufferedImage());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Method which calculates pixel intensity data for each color components (
	 * red, green and blue)
	 * @param bI
	 */
	private void genValues(BufferedImage bI){
		//Iterate through the image pixel data
		for(int y=0; y<bI.getHeight(); y++){
			for(int x=0; x<bI.getWidth(); x++){
				//Get the rgb value for the current pixel
				int rgb = bI.getRGB(x, y);
				
				//Extract the individual color components
				int red = (rgb >> 16) & 0x000000FF;
				int green = (rgb >> 8 ) & 0x000000FF;
				int blue = (rgb) & 0x000000FF;
				
				//Add the data to the relevant array
				this.red[red] += 1;
				this.green[green] += 1;
				this.blue[blue] += 1;
			}
		}
	}
	
	/**
	 * <p><p>Method which generates a difference image and calculates pixel intensity values
	 * @param bI bufferedImage to generate difference image from
	 */
	private void genDifferenceImage(BufferedImage bI){
		if(workFile.fileType != WorkFile.FILE_TYPE_IMAGE){
			throw new UnsupportedOperationException("The file supplied is not an image!");
		}
		dBI = new BufferedImage(bI.getWidth()-1, bI.getHeight(), bI.getType());
		for(int y=0; y<dBI.getHeight(); y++){
			for(int x=0; x<dBI.getWidth(); x++){
				int rgb = bI.getRGB(x+1, y) - bI.getRGB(x, y);
				dBI.setRGB(x, y, Math.max(rgb, 0) );
				
				int red = (rgb >> 16) & 0x000000FF;
				int green = (rgb >> 8 ) & 0x000000FF;
				int blue = (rgb) & 0x000000FF;
				
				this.red[red] += 1;
				this.green[green] += 1;
				this.blue[blue] += 1;
			}
		}
	}
	
	/**
	 * <p>Method which flips the LSB values of an image
	 * 
	 * @param bI	bufferedImage of which to flip the LSB data of
	 * @return		the LSB flipped image
	 */
	private BufferedImage flipLSBPlanes(BufferedImage bI){
		BufferedImage fBI = new BufferedImage(bI.getWidth(), bI.getHeight(), bI.getType());
		for(int y=0; y<fBI.getHeight(); y++){
			for(int x=0; x<fBI.getWidth(); x++){
				int rgb = bI.getRGB(x, y);
				if(rgb%2 == 0){
					fBI.setRGB(x, y, rgb+1);
				}else{
					fBI.setRGB(x, y, rgb-1);
				}
			}
		}
		return fBI;
	}
	
	/**
	 * <p>Returns a data array containing on of the three color components:
	 * <br> 	- HISTOGRAM_DATA_RED
	 * <br>	- HISTOGRAM_DATA_GREEN
	 * <br> 	- HISTOGRAM_DATA_BLUE
	 * 
	 * @param type	integer value selecting a color component
	 * @return		data array of color component intensity values
	 */
	public int[] getData(int type){
		if(type == Histogram.HISTOGRAM_DATA_RED){
			return this.red;
		}else if(type == Histogram.HISTOGRAM_DATA_GREEN){
			return this.green;
		}else if(type == Histogram.HISTOGRAM_DATA_BLUE){
			return this.blue;
		}
		return null;
	}
	
}
