package manipulation.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.sound.sampled.AudioInputStream;
import controller.DecodeController;
import controller.EncodeController;
import cryptography.Cyptography;
import manipulation.Encoder;
import manipulation.MessageTooLargeException;
import manipulation.Utils;
import manipulation.WorkFile;

/**
 * <p>This class is used to encode and decode a hidden message within a audio media file.
 * @author Ashley Allott
 */
public class AudioEncoder implements Encoder{

	/**
	 * <p>Recovers the LSB data from the first embedding step, required
	 * for the second embedding process.
	 * 
	 * @param audioData		the stegotext which contains the LSB data
	 * @param bitsUsedForEncoding
	 * 						the amount of bits per byte of the image data which will be used to 
	 * 						hide the message.		
	 * @return				byte array of the LSB data
	 */
	public static byte[] getEmbedData(byte[] audioData, int bitsUsedForEncoding){	
		int audioBytesPos = 0;
		
		int LSBDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsUsedForEncoding));
		LSBDataSize = LSBDataSize -1;
		LSBDataSize = (int)Math.floor((double)LSBDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE) - 1;
		LSBDataSize = LSBDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		byte[] audioLSBData = new byte[LSBDataSize];
		
		if(bitsUsedForEncoding == 1){
			for(int i=0; i<audioLSBData.length; i++){
				//System.out.println("I: " + i);
				for(int j=0; j<8; j++){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 1) | (byte)(audioData[audioBytesPos] & 0b00000001));
					audioBytesPos++;
				}
				//imgBytesPos++;
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 2){
			for(int i=0; i<audioLSBData.length; i++){
				for(int j=0; j<8; j+=2){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 2) | (byte)(audioData[audioBytesPos] & 0b00000011));
					audioBytesPos++;
				}
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 4){
			for(int i=0; i<audioLSBData.length; i++){
				for(int j=0; j<8; j+=4){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 4) | (byte)(audioData[audioBytesPos] & 0b00001111));
					audioBytesPos++;
				}
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 8){
			for(int i=0; i<audioLSBData.length; i++){
				audioLSBData[i] = (byte)(audioData[audioBytesPos]);
				audioBytesPos++;
			}
			return audioLSBData;
		}
		return null;
	}
	/**
	 * <p>Recovers the LSB data from a given stegotext
	 * 
	 * @param 	audioData	the stegotext containing the message
	 * @return	byte array of the LSB data
	 */
	public static byte[] getEncodedData(byte[] audioData){
		int audioBytesPos = 0;
		
		//Recover the bitsPerByteSetting
		int bitsUsedForEncoding;
		byte curByte = 0b0;
		for(int bit=0; bit < 8; bit++){
			byte curBit = (byte)(audioData[audioBytesPos] & 0b00000001);
			curByte = (byte) (curByte << 1 | curBit);
			audioBytesPos++;
		}
		bitsUsedForEncoding = curByte;
		
		int LSBDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsUsedForEncoding));
		LSBDataSize = (int)Math.floor((double)LSBDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE);
		LSBDataSize = LSBDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		byte[] audioLSBData = new byte[LSBDataSize];
		
		if(bitsUsedForEncoding == 1){
			for(int i=0; i<audioLSBData.length; i++){
				//System.out.println("I: " + i);
				for(int j=0; j<8; j++){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 1) | (byte)(audioData[audioBytesPos] & 0b00000001));
					audioBytesPos++;
				}
				//imgBytesPos++;
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 2){
			for(int i=0; i<audioLSBData.length; i++){
				for(int j=0; j<8; j+=2){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 2) | (byte)(audioData[audioBytesPos] & 0b00000011));
					audioBytesPos++;
				}
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 4){
			for(int i=0; i<audioLSBData.length; i++){
				for(int j=0; j<8; j+=4){
					audioLSBData[i] = (byte)((byte)(audioLSBData[i] << 4) | (byte)(audioData[audioBytesPos] & 0b00001111));
					audioBytesPos++;
				}
			}
			return audioLSBData;
		}else if(bitsUsedForEncoding == 8){
			for(int i=0; i<audioLSBData.length; i++){
				audioLSBData[i] = (byte)(audioData[audioBytesPos]);
				audioBytesPos++;
			}
			return audioLSBData;
		}
		return null;
	}
	/**
	 * <p>Embed data bytes in the image data, using provided BPB value.
	 * 
	 * 
	 * @param audioData				the covertext audio data which is used to hide the given message.	
	 * @param alteredBytes			the bytes of data to be inserted
	 * @param bitsUsedForEncoding	the amount of bits per byte of the image data which will be used to hide the message.
	 * @return						the audio bytes with embedded data
	 * @throws MessageTooLargeException
	 * 								if the message is too large to be hidden in the given covertext.
	 */
	public static byte[] PreEmbedLSBBytes(byte[] audioData, byte[] alteredBytes, short bitsUsedForEncoding) throws MessageTooLargeException{
		int avaiableDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsUsedForEncoding));
		avaiableDataSize = avaiableDataSize - 1;
		avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE)-1;
		avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
		
		if(alteredBytes.length > avaiableDataSize){
			throw new MessageTooLargeException("The message is too large to be hidden in the given image");
		}else{			
			int audioBytePos = 0;
			
			if(bitsUsedForEncoding == 1){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j++){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11111110);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000001); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 2){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=2){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11111100);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000011); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 4){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=4){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11110000);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00001111); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 8){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=8){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b00000000);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b11111111); 	
						audioBytePos++;
					}
				}
				return audioData;
			}
			return null;	
		}
	}
	/**
	 * <p>Embed data bytes in the image data, using provided BPB value, alongside the BPB.
	 * 
	 * @param audioData				the covertext image data which is used to hide the given message.	
	 * @param alteredBytes			the bytes of data to be inserted
	 * @param bitsUsedForEncoding	the amount of bits per byte of the image data which will be used to hide the message.
	 * @return						the image bytes with embedded data
	 * @throws MessageTooLargeException
	 * 								if the message is too large to be hidden in the given covertext.
	 */
	public static byte[] embedLSBBytes(byte[] audioData, byte[] alteredBytes, short bitsUsedForEncoding) throws MessageTooLargeException{
		int avaiableDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsUsedForEncoding));
		avaiableDataSize = avaiableDataSize - 1;
		
		
		if(alteredBytes.length > avaiableDataSize){
			throw new MessageTooLargeException("The message is too large to be hidden in the given image");
		}else{
			byte bitsPerByte = (byte)(bitsUsedForEncoding & 0xFF);
			
			int audioBytePos = 0;
			
			//Embed the integer value indicating the amount of bits used for the encoding in the first 8 bytes
			for(int bit=0; bit < 8; bit++){
				//updateEncodeProgress(e,((double)contentBytePos / (bitsUsedForEncoding.length + msgBytes.length + msgLengthBytes.length)));
				audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11111110 | (bitsPerByte >>> Math.abs(7-bit) & 0b00000001));
				audioBytePos++;
			}
			
			//Embed the data bytes
			if(bitsUsedForEncoding == 1){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j++){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11111110);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000001); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 2){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=2){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11111100);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00000011); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 4){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=4){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b11110000);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b00001111); 	
						audioBytePos++;
					}
				}
				return audioData;
			}else if(bitsUsedForEncoding == 8){
				for(int i=0; i<alteredBytes.length; i++){
					for(int j=0; j<8; j+=8){
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] & 0b00000000);
						audioData[audioBytePos] = (byte)(audioData[audioBytePos] | (alteredBytes[(i)] >> Math.abs((8-bitsUsedForEncoding)-j)) & 0b11111111); 	
						audioBytePos++;
					}
				}
				return audioData;
			}
			return null;	
		}
	}
	/**
	 * <p>Encodes a given string message within a given AudiInputStream covertext using Least 
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
	 * <p>(5) This, now encrypted LSB data, is reinserted back into the audio data LSB
	 * 
	 * <p>Citation (embedding process follows the principles of):
	 * <br>http://www.dreamincode.net/forums/topic/27950-steganography/
	 * 
	 * @param file			the covertext which is used to hide the given message.
	 * @param message		the byte array message which is to be hidden within the given covertext.	
	 * @param bitsPerByteForEncoding	
	 * 						the amount of bits per byte of the image data which will be used to hide the message.
	 * @param passwordKey	the string key which will be used as the encryption cipher's key
	 * @return				a byte array of the audio data, altered to contain the message
	 * @throws MessageTooLargeException
	 * 						if the message is too large to be hidden in the given covertext.
	 */
	public byte[] encode_msg(WorkFile file, byte[] message, short bitsPerByteForEncoding, String passwordKey) throws MessageTooLargeException{
		try{
			byte[] audioData = Utils.audioStreamToByte(file.getCoverAIS());
			
			int avaiableDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsPerByteForEncoding));
			avaiableDataSize = avaiableDataSize - 5;
			avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE) - 1;
			avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
			
			if(message.length > (avaiableDataSize)){
				throw new MessageTooLargeException("The message is too large to be hidden in the given image");
			}else{
				//Get the hash of the password
				byte[] hashedKey = Cyptography.hashKey(passwordKey);
				
				//Get the message in byte format
				byte[] messageLength = intToByteArray(message.length);
				
				//First embedding of the data - Embed the message (along with the length) in the audio data bytes
				byte[] embededData = PreEmbedLSBBytes(Utils.audioStreamToByte(file.getCoverAIS()), Utils.joinByteArray(messageLength, message), bitsPerByteForEncoding);
				
				//Get all of the LSB data from the image, at the maximum supported by the encryption cipher (16 byte blocks)
				byte[] allLSBData = getEmbedData(embededData, bitsPerByteForEncoding);
				
				//Encrypt all of the LSB data, not just the message, as to hide the message's existence within the data
				byte[] ciphertext = Cyptography.cipherEncrypt(hashedKey, allLSBData);
				
				//Second embedding of the data - Embed the encrypted LSB data back into the image data
				byte[] newAudioBytes = embedLSBBytes(Utils.audioStreamToByte(file.getCoverAIS()), ciphertext, bitsPerByteForEncoding);
	
				//Return the new LSB data
				return newAudioBytes;
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(NoSuchAlgorithmException e1){
			e1.printStackTrace();
		}
		return null;
	}
	/**
	 * <p>Decodes a hidden message from a given AudioInputStream stegotext, reversing the technique
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
	 * @throws BadPaddingException
	 */
	public byte[] decode_msg(WorkFile file, String passwordKey) throws BadPaddingException{
		try{
			//Get the byte audio data
			byte[] audioData = Utils.audioStreamToByte(file.getCoverAIS());
			
			//Get the encoded LSB data
			byte[] recoveredData = getEncodedData(audioData);
			
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
			
			byte[] message = new byte[messageLength];
			for(int i=0; i<messageLength; i++){
				message[i] = decyptData[4+i];
			}
			
			//Recover the amount of bytes specified by the message length
			return message;
		}catch(NoSuchAlgorithmException e1){
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>Encodes a hidden message within a audio file, in AudioInputStream format, using various degrees of 
	 * least significant bit embedding.
	 * 
	 * <p>All of the various inputs are converted to bytes for easier manipulation during the embedding process.
	 * 
	 * <p>Before the message itself is encoded into the audio file, the amount of bits per
	 * byte being used to encode the message, is encoded into the audio first (using 1bit 
	 * lsb embedding). After that, the length of the message (in bytes) encoding into the audio file,
	 * being an integer value, takes from up from 4 to 32 bytes of the audio bytes based upon the bits per 
	 * byte parameter.
	 * 
	 * <p>Once the length has been encoded, the message itself is encoded within the audio file. Similarly
	 * to the message length encoded before, the message's bytes are encoded using the least significant
	 * bits in each byte of image data, requiring from 1 to 8 bytes of image data per byte of message.
	 * 
	 * <p>Citation (encoding process follows the principles of):
	 * <br>http://www.dreamincode.net/forums/topic/27950-steganography/
	 * 
	 * @param audio		the covertext which is used to hide the given message.
	 * @param message	the String message which is to be hidden within the given covertext.	
	 * @param bitsPerByteForEncoding 
	 * 					the amount of bits per byte of the audio data which will be used to hide the message.
	 * @param eC			the EncodeController of which this method is called from (used for callbacks to update the 
	 * 					encoding progress)
	 * @return			the AudioInputStream stegotext containing the hidden message
	 * @throws MessageTooLargeException if the message is too large to be hidden in the given covertext.
	 */
	@Deprecated
	public static AudioInputStream encode_msg(AudioInputStream audio, String message, int bitsPerByteForEncoding, EncodeController eC) throws MessageTooLargeException{
		try { 
			byte[] bitsUsedForEncoding = intToByteArray(bitsPerByteForEncoding);	
			byte[] msgBytes = message.getBytes();
			byte[] msgLengthBytes = intToByteArray(msgBytes.length);
			
			byte[] coverAudioBytes = null;
			
			int audioBytesPos = 0;
			int contentBytePos = 0;
			
	        int frameSize = audio.getFormat().getFrameSize();
			int frameLength = (int)audio.getFrameLength();
			
			coverAudioBytes = new byte[(int) frameLength*frameSize];
			audio.read(coverAudioBytes);

			
			//Embed the integer value indicating the amount of bits used for the encoding in the first 32 bytes
			for(int i=0; i<4; i++){
				for(int bit=0; bit < 8; bit++){
					updateEncodeProgress(eC,((double)contentBytePos / (msgBytes.length + msgLengthBytes.length)));
					coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11111110 | (bitsUsedForEncoding[i] >>> Math.abs(7-bit) & 0b00000001));
					audioBytesPos++;
				}
				contentBytePos++;
			}
			//Embed the integer value of the length of the message in bytes
			for(int i=0; i<4; i++){
				updateEncodeProgress(eC,((double)contentBytePos / (msgBytes.length + msgLengthBytes.length)));
				for(int bit=0; bit < 8; bit=(bit+bitsPerByteForEncoding)){
					if(bitsPerByteForEncoding == 1){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11111110 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000001));
					}else if(bitsPerByteForEncoding == 2){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11111100 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000011));
					}else if(bitsPerByteForEncoding == 4){	
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11110000 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00001111));
					}else if(bitsPerByteForEncoding == 8){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b00000000 | (msgLengthBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b11111111));
					}
					audioBytesPos++;
				}
				contentBytePos++;
			}
			//Embed the message
			for(int i=0; i<msgBytes.length; i++){
				updateEncodeProgress(eC,((double)contentBytePos / (msgBytes.length + msgLengthBytes.length)));
				for(int bit=0; bit < 8; bit=(bit+bitsPerByteForEncoding)){
					if(bitsPerByteForEncoding == 1){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11111110 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000001));
					}else if(bitsPerByteForEncoding == 2){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11111100 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00000011));
					}else if(bitsPerByteForEncoding == 4){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b11110000 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b00001111));
					}else if(bitsPerByteForEncoding == 8){
						coverAudioBytes[audioBytesPos] = (byte)(coverAudioBytes[audioBytesPos] & 0b00000000 | (msgBytes[i] >>> Math.abs((8-bitsPerByteForEncoding)-bit) & 0b11111111));
					}
					audioBytesPos++;
				}
				contentBytePos++;
			}
			
			AudioInputStream encodedAudio = new AudioInputStream(new ByteArrayInputStream(coverAudioBytes), audio.getFormat(), audio.getFrameLength());
			
			return encodedAudio;
		} catch(IOException e){
		    System.out.println("Error occcured during audio encoding");
		    e.printStackTrace();
		    return audio;
		}	
	}
	
	/**
	 * <p>Decodes a hidden message from a given AudioInputStream stegotext, using the same Least
	 * Significant Bits technique used to encode the message.
	 * 
	 * <p>Firstly, the the amount of bits per byte data is decoded from the stegotext, which is
	 * embedded in the first 4 bytes of data.
	 * 
	 * <p>Next, the length of the hidden message is decoded from the given audio file via extracting
	 * the first 1 to 4 bytes worth of LSB's from the audio data and constructing an integer from this data.
	 * 
	 * <p>The length value is used to know how much of the audio data contains the hidden message, which
	 * allows the decoder to form a string from the extracted data without misinterpreting bit values
	 * which are not related to the hidden message
	 * 
	 * @param audio		the AudioInputStream stegotext which contains the hidden message.
	 * @param d			the DecodeController of which this method is called from (used for callbacks to update the 
	 * 					decoding progress)
	 * @return			the String message decoded from the stegotext.
	 */
	@Deprecated
	public static String decode_msg(AudioInputStream audio, DecodeController d){
		try{	
			byte[] stegoAudioBytes = null;
			
			int audioBytesPos = 0;
			int contentBytePos = 0;
			
	        int frameSize = audio.getFormat().getFrameSize();
			int frameLength = (int) audio.getFrameLength();
			
			stegoAudioBytes = new byte[(int) frameLength*frameSize];
			audio.read(stegoAudioBytes);
			
			
			// get the amount of bits per bytes used for msg 
			byte[] bitsUsedForEncoding = new byte[4];
			for(int i=0; i<4; i++){
				updateDecodeProgress(d,((double)contentBytePos / (stegoAudioBytes.length)));
				byte curByte = 0b0;
				for(int bit=0; bit < 8; bit++){
					byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00000001);
					curByte = (byte) (curByte << 1 | curBit);
					audioBytesPos++;
				}
				bitsUsedForEncoding[i] = curByte;
			}
			ByteBuffer b = ByteBuffer.wrap(bitsUsedForEncoding);
			b.order(ByteOrder.BIG_ENDIAN);
			int audioBitsForMsgBytes = b.getInt();
			
			//First, recover the length of the hidden message (bytes)
			byte[] msgLengthBytes = new byte[4];
			for(int i=0; i<4; i++){
				updateDecodeProgress(d,((double)contentBytePos / (stegoAudioBytes.length)));
				byte curByte = 0b0;
				for(int bit=0; bit < 8; bit=(bit+audioBitsForMsgBytes)){
					if(audioBitsForMsgBytes == 1){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00000001);
						curByte = (byte) (curByte << 1 | curBit);
					}else if(audioBitsForMsgBytes == 2){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00000011);
						curByte = (byte) (curByte << 2 | curBit);
					}else if(audioBitsForMsgBytes == 4){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00001111);
						curByte = (byte) (curByte << 4 | curBit);
					}else if(audioBitsForMsgBytes == 8){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b11111111);
						curByte = (byte) (curByte << 8 | curBit);
					}
					audioBytesPos++;
				}
				msgLengthBytes[i] = curByte;
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
				for(int bit=0; bit < 8; bit=(bit+audioBitsForMsgBytes)){
					if(audioBitsForMsgBytes == 1){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00000001);
						curByte = (byte) (curByte << 1 | curBit);
					}else if(audioBitsForMsgBytes == 2){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00000011);
						curByte = (byte) (curByte << 2 | curBit);
					}else if(audioBitsForMsgBytes == 4){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b00001111);
						curByte = (byte) (curByte << 4 | curBit);
					}else if(audioBitsForMsgBytes == 8){
						byte curBit = (byte)(stegoAudioBytes[audioBytesPos] & 0b11111111);
						curByte = (byte) (curByte << 8 | curBit);
					}
					audioBytesPos++;
				}
				msgBytes[i] = curByte;
			}
			String recoveredString = new String(msgBytes);
			return recoveredString;
		}catch(IOException e){
		    System.out.println("Error occcured during audio encoding");
		    e.printStackTrace();
		    return "";
		}	
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
	 * <p>Called by the encode function on each loop iteration to update the progress of the encoding 
	 * process to the EncoderController which calls the method.
	 * 
	 * @param eC		the EncoderController object which calls this method, used to update the percent.
	 * @param percent	the decimal percent value representing the encode progress.
	 */
	private static void updateEncodeProgress(EncodeController eC, double percent){
		percent *= 100;
		eC.encodeProgress((int)percent);
	}
	/**
	 * <p> Used to create the byte representation of a integer value, used for embedding 
	 * message length into the covertext. The array uses big endian to store the value.
	 * 
	 * @param value		the integer value of which the byte array will respresent
	 * @return			the byte array representative of a
	 */
	private static byte[] intToByteArray(int value) {
		byte[] intBytes = new byte[]{
			(byte)(value >>> 24),
			(byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)(value)
		};
		return intBytes;
	}

	/**
	 * <p>Used to calculate how much space (in bytes) a given audio stegotext has for hiding message bits.
	 * The bytes available for the message equal to: 
	 * (((total_audio_bytes - bits_per_byte_bytes) * bits_per_byte) / 8) - message_length_bytes
	 * @param file		the covertext workFile
	 * @param bitsPerByteForEncoding	
	 * 					the amount of bits per stegotext byte allocated for encoding
	 * @return			the amount of space available for a hidden message in the covertext
	 */
	public static long getMaxMessageSize(WorkFile file, short bitsPerByteForEncoding){
		if(file.fileFormat != WorkFile.FILE_FORMAT_MP3){
			try{
				AudioInputStream audio = file.getCoverAIS();
				if(audio != null){
					byte[] audioData = Utils.audioStreamToByte(audio);
					
					//Total amount of lsb data, in bytes
					int avaiableDataSize = (int)Math.floor((double)(audioData.length) / (double)(8/bitsPerByteForEncoding));
					//Minus 1 byte, used for lsb setting
					avaiableDataSize = avaiableDataSize - 1;
					//Get the maximum amount avaible to fit within the cipher block size, and minus one to accomidate for padding
					avaiableDataSize = (int)Math.floor((double)avaiableDataSize / (double)Cyptography.CIPHER_BLOCK_SIZE)-1;
					avaiableDataSize = avaiableDataSize * Cyptography.CIPHER_BLOCK_SIZE;
					//Minus 4 bytes, used to store the message size
					avaiableDataSize = avaiableDataSize - 4;
					
					return avaiableDataSize;
				}else{
					return 0;
				}	
			}catch(IOException e1){
				e1.printStackTrace();
				return 0;
			}
		}
		return 0;
	}
	
	
}
