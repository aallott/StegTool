package manipulation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;

/**
 * <p>Class provides a collection of common functions to be used by other classes
 * 
 * @author Ashley Allott
 */
public final class Utils {
	
	private Utils(){}
	
	/**
	 * <p>Deletes the temp directory and files from the file system.
	 */
	public static void removeTempFolder(){
		File file = new File("./temp");
		if(file.isDirectory()){
			String files[] = file.list();
			for(String child : files) {
				File childFile = new File(file, child);
				childFile.delete();
			}
		}
		file.delete();
	}
	
	/**
	 * <p>Generates a byte array from an AudioInputStream object.
	 * 
	 * <p>The byte array is represetive of the audio data of the object, 
	 * allowing for manipulation.
	 * 
	 * @param audio	the AudioInputStream object to get an array from
	 * @return		the byte array of the audio data
	 * @throws IOException
	 */
	public static byte[] audioStreamToByte(AudioInputStream audio) throws IOException{
		byte[] coverAudioBytes = null;
		
        int frameSize = audio.getFormat().getFrameSize();
		int frameLength = (int)audio.getFrameLength();
		
		coverAudioBytes = new byte[(int) frameLength*frameSize];
		audio.read(coverAudioBytes);
		
		return coverAudioBytes;
	}
	
	/**
	 * <p>Gets the editable data from a buffered image, in a byte array format
	 * @param image		the bufferedImage to get the data from
	 * @return			a byte array for the editable data
	 */
	public static byte[] bufferedImageToByte(BufferedImage image){
		byte[] imageData = ((DataBufferByte)image.getData().getDataBuffer()).getData();
		return imageData;
	}
	
	/**
	 * <p>Converts a byte array into a bufferedImage
	 * @param imageData		the byte array containing the image data
	 * @return				a bufferedImage representative of the byte array
	 * @throws IOException
	 */
	public static BufferedImage byteToBufferedImage(byte[] imageData) throws IOException{
		InputStream inputStream = new ByteArrayInputStream(imageData);
		BufferedImage image = ImageIO.read(inputStream);
		return image;
	}
	
	/**
	 * <p> Used to create the byte representation of a short value, used for embedding 
	 * message length into the covertext. The array uses big endian to store the value.
	 * 
	 * @param value		the short value of which the byte array will represent
	 * @return			the byte array representative of a
	 */
	public static byte[] shortToByteArray(short value){
		byte[] shortBytes = new byte[]{
			(byte)(value>>8),
			(byte)(value)
		};
		return shortBytes;
	}
	
	/**
	 * <p> Used to create the byte representation of a integer value, used for embedding 
	 * message length into the covertext. The array uses big endian to store the value.
	 * 
	 * @param value		the integer value of which the byte array will represent
	 * @return			the byte array representative of a
	 */
	public static byte[] intToByteArray(int value){
		byte[] intBytes = new byte[]{
			(byte)(value >>> 24),
			(byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)(value)
		};
		return intBytes;
	}
	
	/**
	 * <p>Concatenates two supplied byte arrays.
	 * 
	 * @param one	byte array to be positioned first in the concatenated array
	 * @param two	byte array to be positioned second in the concatenated array
	 * @return		a byte array containing the elements of the two supplied arrays
	 */
	public static byte[] joinByteArray(byte[] one, byte[] two){
		byte[] newArray = new byte[one.length + two.length];
		
		for(int i=0; i<one.length; i++){
			newArray[i] = one[i];
		}
		for(int i=0; i<two.length; i++){
			newArray[one.length + i] = two[i];
		}
		return newArray;
	}
	
	/**
	 * <p>Inserts a byte array, content, within another array, original, at the given index.
	 * 
	 * @param original	byte array which will have an array, content, inserted into
	 * @param content	byte array which will be inserted into original
	 * @param index		the index of which the content array will be inserted into original
	 * @return			the resulting array of the insertion
	 */
	public static byte[] insertAt(byte[] original, byte[] content, int index){
		byte[] newArray = new byte[original.length + content.length];
		
		for(int i=0; i<index; i++){
			newArray[i] = original[i];
		}
		for(int i=index; i<(index+content.length); i++){
			newArray[i] = content[i-index];
		}
		for(int i=index; i<original.length; i++){
			newArray[i+content.length] = original[i];
		}
		return newArray;
	}
	
	/**
	 * <p>Turns a string binary representation into a byte array.
	 * 
	 * @param input	binary string
	 * @return		a byte array equal to the representation of the string
	 */
	public static byte[] stringToByte(String input){
		int numOfBytes = input.length() / 8;
		byte[] bytes = new byte[numOfBytes];
		for(int i = 0; i < numOfBytes; ++i){
			bytes[i] = (byte)Short.parseShort(input.substring(8 * i, (8 * i)+ 8), 2);
		}
		return bytes;
	}
	
	/**
	 * <p>Checks is two bytes arrays are equal
	 * @param b1	the first array for equality check
	 * @param b2	the second array for quality check
	 * @return		boolean value indicating if the two arrays are equal
	 */
	public static boolean checkEqual(byte[] b1, byte[] b2){
		if(b1.length != b2.length){
			return false;
		}
		for(int i=0; i<b1.length; i++){
			if(b1[i] != b2[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>Converts a byte array into a string representation of the bits contained
	 * 
	 * @param tableBytes	the byte array to be represented
	 * @return				a string representing the data
	 */
	public static String byteArrayToString(byte[] tableBytes){
		String byteArray = "";
		for(byte b: tableBytes){
			byteArray += Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
		}
		return byteArray;
	}
	
}
