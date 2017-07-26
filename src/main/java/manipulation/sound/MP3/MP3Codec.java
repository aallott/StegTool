package manipulation.sound.MP3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import manipulation.BitStream;
import manipulation.BitStreamException;
import manipulation.Codec;
import manipulation.MessageTooLargeException;
import manipulation.Utils;

/**
 * <p>Class used to handle MP3 files, allowing embedding and recovery of hidden messages.
 * 
 * @author Ashley Allott
 */
public class MP3Codec implements Codec{
	
	public static final int TYPE_MPEG_2_5 = (byte)0b00;
	public static final int TYPE_MPEG_2 = (byte)0b10;
	public static final int TYPE_MPEG_1 = (byte)0b11;
	
	public static final int LAYER_3 = (byte)0b01;
	public static final int LAYER_2 = (byte)0b10;
	public static final int LAYER_1 = (byte)0b11;
	
	public BitStream mainData;
	public int capacity;
	public long length;
	public long frameLength;
	public byte[] id3;
	
	// bits, [v1,l1], [v1,l2], [v1,l3], [v2,l1], [v2,l2&l3]
	public static final int[][] BITRATE_TABLE = {
			{(byte)0b0000, 0,0,0,0,0},
			{(byte)0b0001, 32,32,32,32,8},
			{(byte)0b0010, 64,48,40,48,16},
			{(byte)0b0011, 96,56,48,56,24},
			{(byte)0b0100, 128,64,56,64,32},
			{(byte)0b0101, 160,80,64,80,40},
			{(byte)0b0110, 192,96,80,96,48},
			{(byte)0b0111, 224,112,96,112,56},
			{(byte)0b1000, 256,128,112,128,64},
			{(byte)0b1001, 288,160,128,144,80},
			{(byte)0b1010, 320,192,160,160,92},
			{(byte)0b1011, 352,224,192,176,112},
			{(byte)0b1100, 384,256,224,192,128},
			{(byte)0b1101, 416,320,256,224,144},
			{(byte)0b1110, 448,384,320,256,160},
			{(byte)0b1111, -1,-1,-1,-1,-1}
	};
	
	// bits, mpeg1, mpeg2
	public static final int[][] SAMPLE_TABLE = {
			{(byte)0b00, 44100, 22050},
			{(byte)0b01, 48000, 24000},
			{(byte)0b10, 32000, 16000},
			{(byte)0b11, -1, -1}
	};
	
	public ArrayList<Frame> frameList;
	
	/**
	 * <p>Constructor, creates a entry MP3Codec instance.
	 */
	public MP3Codec(){
		length = 0;
		capacity = 0;
		frameList = new ArrayList<Frame>();
		mainData = new BitStream(null);
	}
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Reads a given MP3 byte array
	 */
	public void decodeStream(byte[] audioBytes){
		try{
			if(true){
				for(int i=0; i< audioBytes.length;){
					if(audioBytes[i] == (byte)0b01001001 && 
							audioBytes[i+1] == (byte)0b01000100 &&
							audioBytes[i+2] == (byte)0b00110011){
						//Found ID3 tag
					}
					if(audioBytes[i] == (byte)0xFF){
						if(((audioBytes[i+1] >> 5) & 0b111) == (byte)0b111){
							int frameStart = i;
							
							if(frameStart != 0){
								if(id3 == null){
									id3 = Arrays.copyOfRange(audioBytes, 0, i);
								}
							}
							
							byte[] headerBytes = null;
							headerBytes = new byte[4];
							for(int j=0; j<4; j++){
								headerBytes[j] = audioBytes[i];
								i++;
							}
							
							Header header = new Header(headerBytes);
							int frameLength = header.frameSize;
							
							int j = i + (frameLength-headerBytes.length);
							
							byte[] dataBytes;
							boolean temp = false;
							if((frameLength-headerBytes.length) > 0){
								dataBytes = new byte[(frameLength-headerBytes.length)];
								for(int x=i; x<i + (frameLength-headerBytes.length); x++){
									if(!temp){
										temp = true;
									}
									dataBytes[x-(i)] = audioBytes[x];
								}
							}else{
								dataBytes = new byte[0];
							}
							
							if(j != frameStart && j > 0){
								if(j == audioBytes.length){
									Frame curFrame = new Frame(header, dataBytes, mainData);
									frameList.add(curFrame);
									mainData.addBytes(Arrays.copyOfRange(dataBytes, curFrame.sideInfoLength, dataBytes.length));
									break;
								}else{
									if((audioBytes[j] == (byte)0xFF) && (((audioBytes[j+1] >> 5) & 0b111) == (byte)0b111)){
										Frame curFrame = new Frame(header, dataBytes, mainData);
										frameList.add(curFrame);
										mainData.addBytes(Arrays.copyOfRange(dataBytes, curFrame.sideInfoLength, dataBytes.length));
										
										i = j;
									}
								}
							}else{
								i++;
							}
						}else{
							i++;
						}
					}else{
						i++;
					}		
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		calCapacity();
		calLength();
	}
	
	/**
	 * <p>Recovers embedded data from the MP3 file's private field data.
	 * 
	 * @return	byte array containing the recovered message data
	 */
	private byte[] getEmbeddedData(){
		BitStream bS = new BitStream(null);
		for(int i=0 ;i<frameList.size(); i++){
			Frame curFrame = frameList.get(i);
			if(curFrame.header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
				//5bits
				for(int j=0; j<5; j++){
					byte curBit = (byte)((curFrame.sideInfo.privateBits >> (4-j)) & 0b1);
					bS.addBit(curBit);
				}
			}else{
				//3bits
				for(int j=0; j<3; j++){
					byte curBit = (byte)((curFrame.sideInfo.privateBits >> (2-j)) & 0b1);
					bS.addBit(curBit);
				}
			}
		}
		byte[] recoveredData = bS.getData();
		return recoveredData;
	}
	
	/**
	 * <p>Embeds message data within the MP3 file's private field data.
	 * 
	 * @param bS	bitStream containing the message to be hidden
	 * @throws BitStreamException
	 */
	private void embedData(BitStream bS) throws BitStreamException{
		for(int i=0 ;i<frameList.size(); i++){
			Frame curFrame = frameList.get(i);
			if(curFrame.header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
				//5bits
				curFrame.sideInfo.privateBits = (byte)0;
				for(int j=0; j<5; j++){
					if(!bS.isEndOfData()){
						byte curBit = (byte)((bS.getBits(1)) << (4-j));
						curFrame.sideInfo.privateBits = (byte) (curFrame.sideInfo.privateBits | curBit);
					}
				}
			}else{
				//3bits
				curFrame.sideInfo.privateBits = (byte)0;
				for(int j=0; j<3; j++){
					if(!bS.isEndOfData()){
						byte curBit = (byte)((bS.getBits(1)) << (2-j));
						curFrame.sideInfo.privateBits = (byte) (curFrame.sideInfo.privateBits | curBit);
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Provides embedding of a message in the current MP3 structure.
	 */
	public void encode_msg(byte[] message, String passwordKey, int degradation) throws MessageTooLargeException{
		if(message.length < getCapacity()){
			try{
				//Get the message in byte format
				byte[] messageLength = Utils.intToByteArray(message.length);
				
				//Embed the data
				embedData(new BitStream(Utils.joinByteArray(messageLength, message)));
				
			}catch(BitStreamException e){
				e.printStackTrace();
			}
		}else{
			throw new MessageTooLargeException("Message Too Large");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Provides recovery of a message from the current MP3 structure.
	 */
	public byte[] decode_msg(String passwordKey) throws MessageTooLargeException, Exception{
		//Get the embedded data
		byte[] recoveredData = getEmbeddedData();
		
		//Get the message length from the recovered data
		byte[] messageLengthBytes = new byte[4];
		messageLengthBytes[0] = recoveredData[0];
		messageLengthBytes[1] = recoveredData[1];
		messageLengthBytes[2] = recoveredData[2];
		messageLengthBytes[3] = recoveredData[3];
		
		ByteBuffer b = ByteBuffer.wrap(messageLengthBytes);
		b.order(ByteOrder.BIG_ENDIAN);
		int messageLength = b.getInt();
		
		//Recover the amount of bytes specified by the message length
		byte[] message = new byte[messageLength];
		for(int i=0; i<messageLength; i++){
			message[i] = recoveredData[4+i];
		}
		
		return message;
	}
	
	/**
	 * <p>Calculates the time duration of the MP3 file. 
	 */
	private void calLength(){
		for(int i=0 ;i<frameList.size(); i++){
			Frame curFrame = frameList.get(i);
			length = length + curFrame.header.length;
		}
		frameLength = frameList.get(0).header.length;
	}
	
	/**
	 * <p>Calculates the embedding capacity of the MP3 file
	 */
	private void calCapacity(){
		for(int i=0 ;i<frameList.size(); i++){
			Frame curFrame = frameList.get(i);
			if(curFrame.header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
				//5bits
				capacity = capacity + 5;
			}else{
				//3bits
				capacity = capacity + 3;
			}
		}
	}
	
	/**
	 * <p>Gets the embedding capacity of the MP3 file in bytes.
	 * 
	 * @return	the capacity, in bytes, of the MP3 file
	 */
	public int getCapacity(){
		int avaiableDataSize = (capacity) / (8);
		return avaiableDataSize;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Converts the MP3 into a byte array format.
	 */
	public byte[] genStego(){
		byte[] newBytes = new byte[0];
		for(int i=0; i<frameList.size(); i++){
			newBytes = Utils.joinByteArray(newBytes, Utils.stringToByte(frameList.get(i).output().toString()));
		}
		return newBytes;
	}
	
	/**
	 * <p>Gets the amount of samples for a given MPEG version and layer type.
	 * 
	 * @param mpegVersion	the MPEG version of the frame
	 * @param layerVersion	the Layer type of the frame
	 * @return				the sample rate of the frame
	 */
	public static int getSamplePerFrame(byte mpegVersion, byte layerVersion){
		if(mpegVersion == TYPE_MPEG_1){
			if(layerVersion == LAYER_3){
				return 1152;
			}else if(layerVersion == LAYER_2){
				return 1152;
			}else if(layerVersion == LAYER_1){
				return 384;
			}
		}else if(mpegVersion == TYPE_MPEG_2 | mpegVersion == TYPE_MPEG_2_5){
			if(layerVersion == LAYER_3){
				return 576;
			}else if(layerVersion == LAYER_2){
				return 1152;
			}else if(layerVersion == LAYER_1){
				return 384;
			}
		}
		return -1;
	}
	
	/**
	 * <p>Gets the Bit rate for a given MPEG version and layer type.
	 * 
	 * @param bitRateValue		the bitrate index of the frame
	 * @param mpegVersion		the MPEG version of the  frame
	 * @param layerVersion		the Layer type of the frame
	 * @return					the bit rate of the frame
	 */
	public static int getBitrate(byte bitRateValue, int mpegVersion, int layerVersion){
		for(int i=0; i<BITRATE_TABLE.length; i++){
			if(BITRATE_TABLE[i][0] == bitRateValue){
				if(mpegVersion == TYPE_MPEG_1){
					if(layerVersion == LAYER_1){
						return BITRATE_TABLE[i][1];
					}else if(layerVersion == LAYER_2){
						return BITRATE_TABLE[i][2];
					}else if(layerVersion == LAYER_3){
						return BITRATE_TABLE[i][3];
					}
				}else if(mpegVersion == TYPE_MPEG_2 || mpegVersion == TYPE_MPEG_2_5){
					if(layerVersion == LAYER_1){
						return BITRATE_TABLE[i][4];
					}else if(layerVersion == LAYER_2){
						return BITRATE_TABLE[i][5];
					}else if(layerVersion == LAYER_3){
						return BITRATE_TABLE[i][5];
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * <p>Gets the sample rate for a given MPEG version.
	 * 
	 * @param sampleValue	the sample index of the frame
	 * @param mpegVersion	the MPEG version of the frame
	 * @return				the sample rate of the frame
	 */
	public static int getSampleRate(byte sampleValue, int mpegVersion){
		for(int i=0; i<SAMPLE_TABLE.length; i++){
			if(SAMPLE_TABLE[i][0] == sampleValue){
				if(mpegVersion == TYPE_MPEG_1){
					return SAMPLE_TABLE[i][1];
				}else if(mpegVersion == TYPE_MPEG_2 || mpegVersion == TYPE_MPEG_2_5){
					return SAMPLE_TABLE[i][2];
				}
			}
		}
		return -1;
	}
	
}
