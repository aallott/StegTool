package manipulation.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import controller.DecodeController;
import controller.EncodeController;
import cryptography.Cyptography;
import manipulation.Encoder;
import manipulation.MessageTooLargeException;
import manipulation.Utils;
import manipulation.WorkFile;
import manipulation.image.JPEG.JPEGCodec;

/**
 * <p>This class is used to encode and decode a hidden message within a image media file.
 * @author Ashley Allott
 */
public class ImageEncoder implements Encoder{
	
	/**
	 * <p>Encodes a given string message within a given JPEG covertext, hiding the message within the 
	 * compressed scan data (as detailed in {@link manipulation.image.JPEG.JPEGCodec JPEGCodec}})
	 * 
	 * @param fileArray		the JPEG file covertext in byte array format
	 * @param message		the byte array message which is to be hidden within the given covertext.
	 * @param bitsPerByteForEncoding	
	 * 						the amount of bits per byte of the image data which will be used to hide the message.
	 * @param passwordKey	the string key which will be used as the encryption cipher's key
	 * @return				a byte array of the image data, altered to contain the message
	 * @throws Exception
	 */
	public static byte[] encode_JPEG(byte[] fileArray, byte[] message, int bitsPerByteForEncoding, String passwordKey) throws Exception{
		JPEGCodec jpegCodec = new JPEGCodec();
		jpegCodec.decodeStream(fileArray);
		jpegCodec.encode_msg(message, passwordKey, bitsPerByteForEncoding);
		byte[] newImage = jpegCodec.genStego();
		return newImage;
	}
	
	/**
	 * <p>Recovers the LSB data from the first embedding step, required
	 * for the second embedding process.
	 * 
	 * @param imageData		the stegotext which contains the LSB data
	 * @param bitsUsedForEncoding
	 * 						the amount of bits per byte of the image data which will be used to 
	 * 						hide the message.		
	 * @return				byte array of the LSB data
	 */
	public static byte[] getEmbedData(byte[] imageData, int bitsUsedForEncoding){	
		int imgBytesPos = 0;
		
		int LSBDataSize = (imageData.length) / (8/bitsUsedForEncoding);
		LSBDataSize = LSBDataSize -1;
		LSBDataSize = (int)Math.floor((double)LSBDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE) - 1;
		LSBDataSize = LSBDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		byte[] imageLSBData = new byte[LSBDataSize];
		
		if(bitsUsedForEncoding == 1){
			for(int i=0; i<imageLSBData.length; i++){
				//System.out.println("I: " + i);
				for(int j=0; j<8; j++){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 1) | (byte)(imageData[imgBytesPos] & 0b00000001));
					imgBytesPos++;
				}
				//imgBytesPos++;
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 2){
			for(int i=0; i<imageLSBData.length; i++){
				for(int j=0; j<8; j+=2){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 2) | (byte)(imageData[imgBytesPos] & 0b00000011));
					imgBytesPos++;
				}
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 4){
			for(int i=0; i<imageLSBData.length; i++){
				for(int j=0; j<8; j+=4){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 4) | (byte)(imageData[imgBytesPos] & 0b00001111));
					imgBytesPos++;
				}
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 8){
			for(int i=0; i<imageLSBData.length; i++){
				imageLSBData[i] = (byte)(imageData[imgBytesPos]);
				imgBytesPos++;
			}
			return imageLSBData;
		}
		return null;
	}
	/**
	 * <p>Recovers the LSB data from a given stegotext
	 * 
	 * @param 	imageData	the stegotext containing the message
	 * @return	byte array of the LSB data
	 */
	public static byte[] getEncodedData(byte[] imageData) throws Exception{
		int imgBytesPos = 0;
		
		//Recover the bitsPerByteSetting
		int bitsUsedForEncoding;
		byte curByte = 0b0;
		for(int bit=0; bit < 8; bit++){
			byte curBit = (byte)(imageData[imgBytesPos] & 0b00000001);
			curByte = (byte) (curByte << 1 | curBit);
			imgBytesPos++;
		}
		bitsUsedForEncoding = curByte;
		
		//Specify the max size supported by the encryption algorithm
		int LSBDataSize = (int)Math.floor((double)(imageData.length) / (double)(8/bitsUsedForEncoding));
		LSBDataSize = LSBDataSize -1;
		LSBDataSize = (int)Math.floor((double)LSBDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE);
		LSBDataSize = LSBDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		//Recover the actual LSB data
		byte[] imageLSBData = new byte[LSBDataSize];
		if(bitsUsedForEncoding == 1){
			for(int i=0; i<imageLSBData.length; i++){
				for(int j=0; j<8; j++){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 1) | (byte)(imageData[imgBytesPos] & 0b00000001));
					imgBytesPos++;
				}
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 2){
			for(int i=0; i<imageLSBData.length; i++){
				for(int j=0; j<8; j+=2){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 2) | (byte)(imageData[imgBytesPos] & 0b00000011));
					imgBytesPos++;
				}
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 4){
			for(int i=0; i<imageLSBData.length; i++){
				for(int j=0; j<8; j+=4){
					imageLSBData[i] = (byte)((byte)(imageLSBData[i] << 4) | (byte)(imageData[imgBytesPos] & 0b00001111));
					imgBytesPos++;
				}
			}
			return imageLSBData;
		}else if(bitsUsedForEncoding == 8){
			for(int i=0; i<imageLSBData.length; i++){
				imageLSBData[i] = (byte)(imageData[imgBytesPos]);
				imgBytesPos++;
			}
			return imageLSBData;
		}
		return null;
	}
	/**
	 * <p>Embed data bytes in the image data, using provided BPB value.
	 * 
	 * 
	 * @param imageData				the covertext image data which is used to hide the given message.	
	 * @param alteredBytes			the bytes of data to be inserted
	 * @param bitsUsedForEncoding	the amount of bits per byte of the image data which will be used to hide the message.
	 * @return						the image bytes with embedded data
	 * @throws MessageTooLargeException
	 * 								if the message is too large to be hidden in the given covertext.
	 */
	public static byte[] PreEmbedLSBBytes(byte[] imageData, byte[] alteredBytes, short bitsUsedForEncoding) throws MessageTooLargeException{
		int avaiableDataSize = (imageData.length) / (8/bitsUsedForEncoding);
		avaiableDataSize = avaiableDataSize - 1;
		avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE)-1;
		avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		if(alteredBytes.length > avaiableDataSize){
			throw new MessageTooLargeException("The message is too large to be hidden in the given image");
		}else{			
			int imgBytePos = 0;
			
			if(bitsUsedForEncoding == 1){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j++){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11111110);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000001); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 2){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=2){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11111100);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000011); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 4){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=4){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11110000);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00001111); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 8){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=8){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b00000000);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b11111111); 	
						imgBytePos++;
					}
				}
				return imageData;
			}
			return null;	
		}
	}
	/**
	 * <p>Embed data bytes in the image data, using provided BPB value, alongside the BPB.
	 * 
	 * @param imageData				the covertext image data which is used to hide the given message.	
	 * @param alteredBytes			the bytes of data to be inserted
	 * @param bitsUsedForEncoding	the amount of bits per byte of the image data which will be used to hide the message.
	 * @return						the image bytes with embedded data
	 * @throws MessageTooLargeException
	 * 								if the message is too large to be hidden in the given covertext.
	 */
	public static byte[] embedLSBBytes(byte[] imageData, byte[] alteredBytes, short bitsUsedForEncoding) throws MessageTooLargeException{
		int avaiableDataSize = (imageData.length) / (8/bitsUsedForEncoding);
		avaiableDataSize = avaiableDataSize - 1;
		
		if(alteredBytes.length > avaiableDataSize){
			throw new MessageTooLargeException("The message is too large to be hidden in the given image");
		}else{
			byte bitsPerByte = (byte)(bitsUsedForEncoding & 0xFF);
			
			int imgBytePos = 0;
			
			//Embed the integer value indicating the amount of bits used for the encoding in the first 8 bytes
			for(int bit=0; bit < 8; bit++){
				//updateEncodeProgress(e,((double)contentBytePos / (bitsUsedForEncoding.length + msgBytes.length + msgLengthBytes.length)));
				imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11111110 | (bitsPerByte >>> Math.abs(7-bit) & 0b00000001));
				imgBytePos++;
			}
			
			//Embed the data bytes
			if(bitsUsedForEncoding == 1){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j++){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11111110);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000001); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 2){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=2){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11111100);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000011); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 4){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=4){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b11110000);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00001111); 	
						imgBytePos++;
					}
				}
				return imageData;
			}else if(bitsUsedForEncoding == 8){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=8){
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] & 0b00000000);
						imageData[imgBytePos] = (byte)(imageData[imgBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b11111111); 	
						imgBytePos++;
					}
				}
				return imageData;
			}
			return null;	
		}
	}
	
	/**
	 * <p>Encodes a given string message within a given BufferedImage covertext using Least 
	 * Significant Bit embedding.
	 * 
	 * <p>The embedding process is as follows:
	 * 
	 * <p>(1) The message byte array is joined to the end of the message-length byte array,
	 * forming an byte array containing the message-length, and the message.
	 * 
	 * <p>(2) This array is embedding into the image data using the BPB(Bits Per Byte value)
	 * 
	 * <p>(3) All of the LSB data from the image is extracted into a single byte array, forming
	 * an array of all the LSB data which fits within the Encryption cipher algorithm's block size.
	 * This means the array's size will be the length of the image data array, rounded down to the
	 * lowest multiple of 16, minus 16 - the minus 16 required due to the padding scheme adding
	 * an extra block is the data is full.
	 * 
	 * <p>(4) This LSB data is encrypted using the Encryption Cipher, using a hashed version
	 * of the supplied string key.
	 * 
	 * <p>(5) This, now encrypted LSB data, is reinserted back into the image data LSB
	 * 
	 * <p>Citation (embedding process follows the principles of):
	 * <br>http://www.dreamincode.net/forums/topic/27950-steganography/
	 * 
	 * @param file			the covertext which is used to hide the given message.
	 * @param message		the byte array message which is to be hidden within the given covertext.	
	 * @param bitsPerByteForEncoding	
	 * 						the amount of bits per byte of the image data which will be used to hide the message.
	 * @param passwordKey	the string key which will be used as the encryption cipher's key
	 * @return				a byte array of the image data, altered to contain the message
	 * @throws MessageTooLargeException
	 * 						if the message is too large to be hidden in the given covertext.
	 */
	public byte[] encode_msg(WorkFile file, byte[] message, short bitsPerByteForEncoding, String passwordKey) throws MessageTooLargeException{
		
		try{
			byte[] imageData = Utils.bufferedImageToByte(file.getCoverBufferedImage());
			
			int avaiableDataSize = (imageData.length) / (8/bitsPerByteForEncoding);
			avaiableDataSize = avaiableDataSize - 5;
			avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE) - 1;
			avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
			
			if(message.length > (avaiableDataSize)){
				throw new MessageTooLargeException("The message is too large to be hidden in the given image");
			}else{
				//Get the hash of the password
				byte[] hashedKey = Cyptography.hashKey(passwordKey);
				
				//Get the message in byte format
				byte[] messageLength = Utils.intToByteArray(message.length);
				
				//First embedding of the data - Embed the message (along with the length) in the image data bytes
				byte[] embededData = PreEmbedLSBBytes(Utils.bufferedImageToByte(file.getCoverBufferedImage()), Utils.joinByteArray(messageLength, message), bitsPerByteForEncoding);
				
				//Get all of the LSB data from the image, at the maximum supported by the encryption cipher (16 byte blocks)
				byte[] allLSBData = getEmbedData(embededData, bitsPerByteForEncoding);
				
				//Encrypt all of the LSB data, not just the message, as to hide the message's existence within the data
				byte[] ciphertext = Cyptography.cipherEncrypt(hashedKey, allLSBData);
				
				//Second embedding of the data - Embed the encrypted LSB data back into the image data
				byte[] newImageData = embedLSBBytes(Utils.bufferedImageToByte(file.getCoverBufferedImage()), ciphertext, bitsPerByteForEncoding);
				
				return newImageData;
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * <p>Decodes a hidden message from a given BufferedImage stegotext, reversing the technique
	 * used in the encoding process.
	 * 
	 * <p>The recovery process is as follows:
	 * 
	 * <p>(1) The LSB data is recovered from the image data to form a single byte array
	 * containing the LSB data.
	 * 
	 * <p>(2) - The supplied string password is used to get it's hashed equivalent. This
	 * hashed key is then used by the encryption cipher to get the plaintext LSB data.
	 * 
	 * <p>(3) - The message length value is extracted from the LSB data, using the first 
	 * 4 bytes to form an integer, which informs how long the message is (in bytes).
	 * 
	 * <p>(4) - Using the recovered message length, the algorithm nows how  many elements
	 * of the LSB data are part of the message, thus it recovers the relevant bytes, which
	 * are returned.
	 *
	 * @param file			the WorkFile stegotext which contains the hidden message.
	 * @param passwordKey	the string key which will be used as the encryption cipher's key
	 * @return				the byte array message recovered from the stegotext.
	 * @throws MessageTooLargeException
	 * @throws BadPaddingException
	 */
	public byte[] decode_msg(WorkFile file, String passwordKey) throws BadPaddingException, Exception{
		try{
			//Get the byte image data
			byte[] imageData = Utils.bufferedImageToByte(file.getCoverBufferedImage());
			
			//Get the encoded LSB data
			byte[] recoveredData = getEncodedData(imageData);
			
			//Get a byte array containing the hashed password key
			byte[] hashedKey = Cyptography.hashKey(passwordKey);
			
			//Decrypt the recovered data
			byte[] decyptData = Cyptography.cipherDecrypt(hashedKey, recoveredData);
			
			//Get the message length value from the first 4 bytes
			byte[] messageLengthBytes = new byte[4];
			messageLengthBytes[0] = decyptData[0];
			messageLengthBytes[1] = decyptData[1];
			messageLengthBytes[2] = decyptData[2];
			messageLengthBytes[3] = decyptData[3];
			
			ByteBuffer b = ByteBuffer.wrap(messageLengthBytes);
			b.order(ByteOrder.BIG_ENDIAN);
			int messageLength = b.getInt();
			
			//Recover the amount of bytes specified by the message length
			byte[] message = new byte[messageLength];
			for(int i=0; i<messageLength; i++){
				message[i] = decyptData[4+i];
			}
			
			return message;
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * <p>Encodes a given string message within a given BufferedImage covertext using Least 
	 * Significant Bit embedding.
	 * 
	 * <p>The BufferedImage, message and the message length are first converted into byte
	 * arrays, which allow for the manipulation to insert the hidden message.
	 * 
	 * <p>Before the message itself is encoded into the image file, the amount of bits per
	 * image byte being used to encode the message, is encoded into the image first (using 1bit 
	 * lsb embedding). After that, the length of the message (in bytes) is added to the image, 
	 * this allows for the decoder to be able to know where the message lies in the image. This 
	 * length, being an integer value, takes from up from 4 to 32 bytes of the image bytes based 
	 * upon the bits per image byte setting.
	 * 
	 * <p>Once the length has been encoded, the message itself is encoded within the image. Similarly
	 * to the message length encoded before, the message's bytes are encoded using the least significant
	 * bits in each byte of image data, requiring from 1 to 8 bytes of image data per byte of message.
	 * 
	 * <p>Citation (encoding process follows the principles of):
	 * <br>http://www.dreamincode.net/forums/topic/27950-steganography/
	 * 
	 * @param image		the covertext which is used to hide the given message.
	 * @param message	the String message which is to be hidden within the given covertext.	
	 * @param bitsPerByteForEncoding 
	 * 					the amount of bits per byte of the image data which will be used to hide the message.
	 * @param e			the EncodeController of which this method is called from (used for callbacks to update the 
	 * 					encoding progress)
	 * @return			the BufferedImage stegotext containing the hidden message
	 * @throws MessageTooLargeException 
	 * 					if the message is too large to be hidden in the given covertext.
	 */
	@Deprecated
	public static BufferedImage encode_msg(BufferedImage image, String message, short bitsPerByteForEncoding, EncodeController e) throws MessageTooLargeException{
		if(message.length() > getMaxMessageSize(image, bitsPerByteForEncoding)){
			throw new MessageTooLargeException("The message is too large to be hidden in the given image");
		}else{

			byte[] bitsUsedForEncoding = Utils.shortToByteArray(bitsPerByteForEncoding);	
			byte[] imgBytes = ((DataBufferByte) image.getData().getDataBuffer()).getData();
			byte[] msgBytes = message.getBytes();
			byte[] msgLengthBytes = Utils.intToByteArray(msgBytes.length);
		
			int imgBytesPos = 0;
			int contentBytePos = 0;
			
			//Embed the integer value indicating the amount of bits used for the encoding in the first 16 bytes
			for(int i=0; i<bitsUsedForEncoding.length; i++){
				for(int bit=0; bit < 8; bit++){
					updateEncodeProgress(e,((double)contentBytePos / (bitsUsedForEncoding.length + msgBytes.length + msgLengthBytes.length)));
					imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11111110 | (bitsUsedForEncoding[i] >>> Math.abs(7-bit) & 0b00000001));
					imgBytesPos++;
				}
				contentBytePos++;
			}
			//Embed the integer value of the length of the message in bytes
			for(int i=0; i<msgLengthBytes.length; i++){
				updateEncodeProgress(e,((double)contentBytePos / (bitsUsedForEncoding.length + msgBytes.length + msgLengthBytes.length)));
				for(int bit=0; bit < 8; bit=(bit+bitsPerByteForEncoding)){
					if(bitsPerByteForEncoding == 1){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11111110 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000001));
					}else if(bitsPerByteForEncoding == 2){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11111100 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000011));
					}else if(bitsPerByteForEncoding == 4){	
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11110000 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00001111));
					}else if(bitsPerByteForEncoding == 8){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b00000000 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b11111111));
					}
					imgBytesPos++;
				}
				contentBytePos++;
			}
			//Embed the message
			for(int i=0; i<msgBytes.length; i++){
				updateEncodeProgress(e,((double)contentBytePos / (bitsUsedForEncoding.length + msgBytes.length + msgLengthBytes.length)));
				for(int bit=0; bit < 8; bit=(bit+bitsPerByteForEncoding)){
					if(bitsPerByteForEncoding == 1){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11111110 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000001));
					}else if(bitsPerByteForEncoding == 2){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11111100 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000011));
					}else if(bitsPerByteForEncoding == 4){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b11110000 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00001111));
					}else if(bitsPerByteForEncoding == 8){
						imgBytes[imgBytesPos] = (byte)(imgBytes[imgBytesPos] & 0b00000000 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b11111111));
					}
					imgBytesPos++;
				}
				contentBytePos++;
			}
			
			//Create a new bufferedimage, based upon the original image, with the new data
			BufferedImage encodedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			encodedImage.setData(Raster.createRaster(encodedImage.getSampleModel(), new DataBufferByte(imgBytes, imgBytes.length), new Point()));
		
			return encodedImage;
		}
	}
	
	/**
	 * <p>Called by the encode function on each loop iteration to update the progress of the encoding 
	 * process to the EncoderController which calls the method.
	 * 
	 * @param eC		the EncoderController object which calls this method, used to update the percent.
	 * @param percent	the decimal percent value representing the encode progress.
	 */
	private static void updateEncodeProgress(EncodeController eC, double percent){
		if(eC != null){
			percent *= 100;
			eC.encodeProgress((int)percent);
		}
	}
	
	/**
	 * <p>Decodes a hidden message from a given BufferedImage stegotext, using the same Least
	 * Significant Bits technique used to encode the image.
	 * 
	 * <p>Firstly, the the amount of bits per image byte data is decoded from the image, which is
	 * embedded in the first 4 bytes of data.
	 * 
	 * <p>Next, the length of the hidden message is decoded from the given image via extracting
	 * the first 1 to 4 bytes worth of LSB's from the image data and constructing an integer from this data.
	 * 
	 * <p>The length value is used to know how much of the image data contains the hidden message, which
	 * allows the decoder to form a string from the extracted data without misinterpreting bit values
	 * which have nothing to do with the message.
	 * 
	 * @param image		the BufferedImage stegotext which contains the hidden message.
	 * @return			the String message decoded from the stegotext.
	 */
	@Deprecated
	public static String decode_msg(BufferedImage image, DecodeController d){
		byte[] imgBytes = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		
		int imgBytesPos = 0;
		int contentBytePos = 0;
		
		// get the amount of bits per bytes used for msg 
		byte[] bitsUsedForEncoding = new byte[4];
		for(int i=0; i<2; i++){
			updateDecodeProgress(d,((double)contentBytePos / (imgBytes.length)));
			byte curByte = 0b0;
			for(int bit=0; bit < 8; bit++){
				byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00000001);
				curByte = (byte) (curByte << 1 | curBit);
				imgBytesPos++;
			}
			bitsUsedForEncoding[i] = curByte;
			contentBytePos++;
		}
		ByteBuffer b = ByteBuffer.wrap(bitsUsedForEncoding);
		b.order(ByteOrder.BIG_ENDIAN);
		short imgBitsForMsgBytes = b.getShort();
		
		//First, recover the length of the hidden message (bytes)
		byte[] msgLengthBytes = new byte[4];
		for(int i=0; i<4; i++){
			updateDecodeProgress(d,((double)contentBytePos / (imgBytes.length)));
			byte curByte = 0b0;
			for(int bit=0; bit < 8; bit=(bit+imgBitsForMsgBytes)){
				if(imgBitsForMsgBytes == 1){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00000001);
					curByte = (byte) (curByte << 1 | curBit);
				}else if(imgBitsForMsgBytes == 2){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00000011);
					curByte = (byte) (curByte << 2 | curBit);
				}else if(imgBitsForMsgBytes == 4){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00001111);
					curByte = (byte) (curByte << 4 | curBit);
				}else if(imgBitsForMsgBytes == 8){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b11111111);
					curByte = (byte) (curByte << 8 | curBit);
				}
				imgBytesPos++;
			}
			msgLengthBytes[i] = curByte;
			contentBytePos++;
		}
		
		int contentBytePosMark = contentBytePos;
		
		b = ByteBuffer.wrap(msgLengthBytes);
		b.order(ByteOrder.BIG_ENDIAN);
		int msgLength = b.getInt();
		
		//Using the length of the message recover the message
		byte[] msgBytes = new byte[msgLength];
		for(int i=0; i<msgLength; i++){
			updateDecodeProgress(d,((double)contentBytePos / (msgLength + contentBytePosMark)));
			byte curByte = 0b0;
			for(int bit=0; bit < 8; bit=(bit+imgBitsForMsgBytes)){
				if(imgBitsForMsgBytes == 1){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00000001);
					curByte = (byte) (curByte << 1 | curBit);
				}else if(imgBitsForMsgBytes == 2){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00000011);
					curByte = (byte) (curByte << 2 | curBit);
				}else if(imgBitsForMsgBytes == 4){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b00001111);
					curByte = (byte) (curByte << 4 | curBit);
				}else if(imgBitsForMsgBytes == 8){
					byte curBit = (byte)(imgBytes[imgBytesPos] & 0b11111111);
					curByte = (byte) (curByte << 8 | curBit);
				}
				imgBytesPos++;
			}
			msgBytes[i] = curByte;
			contentBytePos++;
		}
		
		//Create a new string from the byte array and return it
		String recoveredString = new String(msgBytes);
		return recoveredString;
	}
	/**
	 * <p>Called by the decode function on each loop iteration to update the progress of the decoding 
	 * process to the DecoderController which calls the method.
	 * 
	 * @param dC		the DecoderController object which calls this method, used to update the percent.
	 * @param percent	the decimal percent value representing the decode progress.
	 */
	private static void updateDecodeProgress(DecodeController dC, double percent){
		percent *= 100;
		dC.decodeProgress((int)percent);
	}
	/**
	 * <p>Used to calculate how much space (in bytes) a given image stegotext has for hiding message bits.
	 * The bytes available for the message equal to: 
	 * (((total_image_bytes - bits_per_byte_bytes) * bits_per_byte) / 8) - message_length_bytes
	 * @param image		the covertext image
	 * @param bitsPerByteForEncoding	
	 * 					the amount of bits per stegotext byte allocated for encoding
	 * @return			the amount of space available for a hidden message in the covertext
	 */
	public static long getMaxMessageSize(BufferedImage image, int bitsPerByteForEncoding){
		if(image != null){
			byte[] imgBytes = ((DataBufferByte) image.getData().getDataBuffer()).getData();
			long bytes = ((imgBytes.length-32) * bitsPerByteForEncoding) / 8;
			bytes = Math.max(bytes-(32*bitsPerByteForEncoding), 0);
			return bytes;
		}else{
			return 0;
		}
	}
	
	/**
	 * <p>Gets the maximum message size of a given Image workfile
	 * 
	 * @param image						the workfile containing the file
	 * @param bitsPerByteForEncoding	the degradation value applied to the file
	 * @return							long value indicate the maximum message size supported
	 * @throws Exception
	 */
	public static long getMaxMessageSize(WorkFile image, short bitsPerByteForEncoding) throws Exception{
		if(image != null){
			if(image.fileType == WorkFile.FILE_TYPE_IMAGE){
				if(image.fileFormat != WorkFile.FILE_FORMAT_JPEG){
					try{
						byte[] imageData = ((DataBufferByte)image.getCoverBufferedImage().getData().getDataBuffer()).getData();
						
						//Total amount of lsb data, in bytes
						int avaiableDataSize = (imageData.length) / (8/bitsPerByteForEncoding);
						//Minus 1 byte, used for lsb setting
						avaiableDataSize = avaiableDataSize - 1;
						//Get the maximum amount avaible to fit within the cipher block size, and minus one to accomidate for padding
						avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE)-1;
						avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
						//Minus 4 bytes, used to store the message size
						avaiableDataSize = avaiableDataSize - 4;
						
						return avaiableDataSize;
					}catch(IOException e) {
						e.printStackTrace();
					}
				}
				return 0;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
}
