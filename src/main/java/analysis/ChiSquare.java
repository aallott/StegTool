package analysis;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import manipulation.WorkFile;

/**
 * <p>Performs a Chi-Square attack on a lossless image file format.
 * 
 * 
 * <p>Sources: 
 * <br>	https://github.com/b3dk7/StegExpose/blob/master/ChiSquare.java
 * <br>	http://www.guillermito2.net/stegano/tools/index.html
 * <br>	http://cuneytcaliskan.blogspot.co.uk/2011/12/steganalysis-chi-square-attack-lsb.html
 * 
 * @author Ashley Allott
 */
public class ChiSquare implements SteganalysisTest{
	
	private static final int CHI_CHUNK_SIZE = 128;
	private static final double CHI_POSITIVE_THRESHOLD = 0.2;
	
	private double[] chiResult;
	private double[] averageLSB;
	
	private int valuesLength = 256;
	private long[] values = new long[valuesLength];
	
	/**
	 * <p>Performs a Chi-Square attack on an image
	 * 
	 * @return	boolean value indicating suspected presence of a hidden message
	 * @throws IOException 
	 */
	public boolean performTest(WorkFile workFile) throws IOException{
		chiSquare(workFile.getCoverBufferedImage(), CHI_CHUNK_SIZE);
		double chiAverage = getAverage(chiResult);
		if(chiAverage > CHI_POSITIVE_THRESHOLD){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * <p>Gets the expected values of the values array, an image with embedded random LSB data
	 * will be similar to these average values.
	 * 
	 * @return	an array containing the average, expected PoV values
	 */
	private double[] getExpected(){
		double[] result = new double[values.length / 2];
		for(int i=0; i<result.length; i++){
			double avg = ((values[2*i] + values[2*i+1]) / 2);
			result[i] = avg;
		}
		return result;
	}
	
	/**
	 * <p>Gets the actual values for the Pairs-of-values
	 * 
	 * @return	an array containing the actual PoV values
	 */
	private long[] getPov(){
		long[] result = new long[values.length / 2];
		for(int i=0; i<result.length; i++){
			result[i] = values[2*i+1];
		}
		return result;
	}
	
	/**
	 * <p>Performs the actual Chi-Square test on the supplied image, producing an
	 * array of Chi-Square results on each byte chunk.
	 * 
	 * <p>Also produces a list of the LSB averages: as suggested in:
	 * http://www.guillermito2.net/stegano/tools/index.html
	 * 
	 * @param image			the image to be tested
	 * @param chunkSize		the size of the chunk to be individually tested
	 */
	private void chiSquare(BufferedImage image, int chunkSize){
		//Get the amount of chunks
		int chunkCount = (image.getWidth()*image.getHeight()*3/chunkSize);
		
		//Configure the result arrays
		chiResult = new double[chunkCount];
		averageLSB = new double[chunkCount];
		
		//Configure the count values
		int currentChunk = 0;
		int byteCount = 0;
		int count = 0;
		
		byte[] lsbData = new byte[image.getWidth()*image.getHeight()*3];
		
		//Initialise the Values array
		for(int i=0; i<values.length; i++){
			values[i] = 1;
		}
		
		//Iterate through the image pixel bytes
		for(int j=0; j<image.getHeight(); j++){
			for(int i=0; i<image.getWidth(); i++){
				//Get the rgb data of the current pixel
				int rgb = image.getRGB(i, j);
				
				//Handle the red byte
				int red = (rgb >> 16) & 0x000000FF;
				values[red]++;
				lsbData[count] = (byte) (red & 0b00000001);
				count++;
				byteCount++;
				if(byteCount > chunkSize){
					chiResult[currentChunk] = performChiTest();
					currentChunk++;
					byteCount = 0;
				}
				
				//Handle the green byte
				int green = (rgb >> 8 ) & 0x000000FF;
				values[green]++;
				lsbData[count] = (byte) (green & 0b00000001);
				count++;
				byteCount++;
				if(byteCount > chunkSize){
					chiResult[currentChunk] = performChiTest();
					currentChunk++;
					byteCount = 0;
				}
				
				//Handle the blue byte
				int blue = (rgb) & 0x000000FF;
				values[blue]++;
				lsbData[count] = (byte) (blue & 0b00000001);
				count++;
				byteCount++;
				if(byteCount > chunkSize){
					chiResult[currentChunk] = performChiTest();
					currentChunk++;
					byteCount = 0;
				}
					
			}
		}
		
		//Get average LSB data for each chunk
		int elementCount = 0;
		int lsbCount = 0;
		currentChunk = 0;
		for(int i=0; i<lsbData.length; i++){
			if(lsbData[i] == 1){
				lsbCount++;
			}
			elementCount++;
			if(elementCount > chunkSize){
				averageLSB[currentChunk] = ((double)lsbCount / (double)chunkSize);
				lsbCount = 0;
				elementCount = 0;
				currentChunk++;
			}
		}
	}
	
	/**
	 * <p>Performs the Chi-Square test statistical test on data on the Values data.
	 * 
	 * @return	p-value indicating a difference between the expected and actual values
	 * 				- a value close to 1 suggests the two arrays are similar
	 * 				- a value close to 0 suggests the two arrays are different
	 */
	private double performChiTest(){
		double[] expected = getExpected();
		long[] pov = getPov();
		double chiTestResult = new ChiSquareTest().chiSquareTest(expected, pov);
		return chiTestResult;
	}
	
	/**
	 * <p>Gets the mean average of a supplied list
	 * 
	 * @param list		the list to get the average from
	 * @return			the average value of the list
	 */
	private double getAverage(double[] list){
		double result = 0;
		for(double element: list){
			result += element;
		}
		return result / list.length;
	}
	
	/**
	 * vReturns the Chi-Square test results
	 * @return	double array containing the results
	 */
	public double[] getChiResults(){
		return chiResult;
	}
	
	/**
	 * <p>Returns the average LSB data
	 * @return	double array containing the average LSB
	 */
	public double[] getAverageLSB(){
		return averageLSB;
	}
	
	/**
	 * <p>Gets the estimated size of the hidden message
	 * 
	 * <p>The Chi-Square array contains the results of testing each chunksize.
	 * 
	 * <p>If an element is close to 1, then that chunk is suspicious of holding 
	 * a hidden message. to get the total size of the hidden message we go through
	 * the array finding elements close to 1 and multiplying this value by the chunksize
	 * 
	 * @return 	integer value containing the estimated size of the hidden message in bytes
	 */
	public int getMessageSizeEstimate(){
		int positiveChunks = 0;
		for(double element: chiResult){
			if(element > 0.8){
				positiveChunks++;
			}
		}
		return (positiveChunks * CHI_CHUNK_SIZE);
	}
	
}
