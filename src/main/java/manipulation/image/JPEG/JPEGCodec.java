package manipulation.image.JPEG;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import manipulation.Codec;
import manipulation.MessageTooLargeException;
import manipulation.Utils;

/**
 * <p>Class used to handle JPEG files, allowing embedding and recovery of hidden messages.
 * 
 * @author Ashley Allott
 */
public class JPEGCodec implements Codec{
	
	public static final byte MARKER_TYPE_SOI = (byte)0xD8;
	public static final byte MARKER_TYPE_EOI = (byte)0xD9;
	public static final byte MARKER_TYPE_SOF_BASELINE = (byte)0xC0;
	public static final byte MARKER_TYPE_SOF_EXTENDED = (byte)0xC1;
	public static final byte MARKER_TYPE_SOF_PROGRESSIVE = (byte)0xC2;
	public static final byte MARKER_TYPE_SOF_LOSSLESS = (byte)0xC3;
	public static final byte MARKER_TYPE_DHT = (byte)0xC4;
	public static final byte MARKER_TYPE_DQT = (byte)0xDB;
	public static final byte MARKER_TYPE_DRI = (byte)0xDD;
	public static final byte MARKER_TYPE_SOS = (byte)0xDA;
	public static final byte MARKER_TYPE_APP_START = (byte)0xE0;
	public static final byte MARKER_TYPE_APP_END = (byte)0xEF;
	public static final byte MARKER_TYPE_COM = (byte)0xFE;
	
	byte[] originalBytes;
	int scanDataLength = -1;
	
	byte currentMarkerType;
	byte[] currentMarker = null;
	int currentMarkerPos = 0;
	
	private int restartInterval;
	
	private int imageX;
	private int imageY;
	
	private ArrayList<Component> imageComponents;
	private int componentHorizontalMax;
	private int componentVerticalMax;
	
	private Map<Integer, Huffman> huffmanTablesDC;
	private Map<Integer, Huffman> huffmanTablesAC;
	
	private ArrayList<MCU> mcuList;
	
	/**
	 * <p>Constructor. Creates a new, empty JPEGCodec ready to decode JPEG data bytes.
	 */
	public JPEGCodec(){
		imageComponents = new ArrayList<Component>();
		huffmanTablesDC = new HashMap<Integer, Huffman>();
		huffmanTablesAC = new HashMap<Integer, Huffman>();
		mcuList = new ArrayList<MCU>();
	}
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Reads a given JPEG byte array
	 */
	public void decodeStream(byte[] imageBytes) throws Exception{
		originalBytes = imageBytes;
		for(int i=0; i< imageBytes.length; i++){
			if(imageBytes[i] == (byte)0xFF){
				i++;
				if(imageBytes[i] == MARKER_TYPE_SOI){
					handleCurrentMarker();
				}else if(imageBytes[i] == MARKER_TYPE_SOF_BASELINE){
					handleCurrentMarker();
					
					byte[] length = new byte[2];
					i++;
					length[0] = imageBytes[i];
					i++;
					length[1] = imageBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					
					currentMarkerType = MARKER_TYPE_SOF_BASELINE;
					currentMarker = new byte[markerLength];
					currentMarker[currentMarkerPos] = imageBytes[i-1];
					currentMarkerPos++;
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;
				}else if(imageBytes[i] == MARKER_TYPE_SOF_EXTENDED){
					handleCurrentMarker();
					
					byte[] length = new byte[2];
					i++;
					length[0] = imageBytes[i];
					i++;
					length[1] = imageBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					
					currentMarkerType = MARKER_TYPE_SOF_EXTENDED;
					currentMarker = new byte[markerLength];
					currentMarker[currentMarkerPos] = imageBytes[i-1];
					currentMarkerPos++;
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;
				}else if(imageBytes[i] == MARKER_TYPE_DHT){
					handleCurrentMarker();

					byte[] length = new byte[2];
					i++;
					length[0] = imageBytes[i];
					i++;
					length[1] = imageBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					
					currentMarkerType = MARKER_TYPE_DHT;
					currentMarker = new byte[markerLength];
					currentMarker[currentMarkerPos] = imageBytes[i-1];
					currentMarkerPos++;
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;
				}else if(imageBytes[i] == MARKER_TYPE_DQT){
					handleCurrentMarker();
				}else if(imageBytes[i] == MARKER_TYPE_DRI){
					handleCurrentMarker();

					byte[] length = new byte[2];
					i++;
					length[0] = imageBytes[i];
					i++;
					length[1] = imageBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					
					currentMarkerType = MARKER_TYPE_DRI;
					currentMarker = new byte[markerLength];
					currentMarker[currentMarkerPos] = imageBytes[i-1];
					currentMarkerPos++;
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;
				}else if(imageBytes[i] == MARKER_TYPE_SOS){
					handleCurrentMarker();

					byte[] length = new byte[2];
					i++;
					length[0] = imageBytes[i];
					i++;
					length[1] = imageBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					
					currentMarkerType = MARKER_TYPE_SOS;
					currentMarker = new byte[markerLength];
					currentMarker[currentMarkerPos] = imageBytes[i-1];
					currentMarkerPos++;
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;				
				}else if(imageBytes[i] == MARKER_TYPE_COM){
					handleCurrentMarker();
				}else if(imageBytes[i] >= MARKER_TYPE_APP_START && imageBytes[i] <= MARKER_TYPE_APP_END){
					handleCurrentMarker();
				}else if(imageBytes[i] == MARKER_TYPE_EOI){
					handleCurrentMarker();
					break;
				}else if(imageBytes[i] == 0x00){
					//STUFF BYTE
					if(currentMarker != null){
						if(currentMarkerPos >= currentMarker.length){
							currentMarker = Arrays.copyOf(currentMarker, currentMarker.length + 1);
						}
						currentMarker[currentMarkerPos] = imageBytes[i-1];
						currentMarkerPos++;
					}
				}
			}else{
				if(currentMarker != null){
					if(currentMarkerPos >= currentMarker.length){
						currentMarker = Arrays.copyOf(currentMarker, currentMarker.length + 1);
					}
					currentMarker[currentMarkerPos] = imageBytes[i];
					currentMarkerPos++;
				}
			}
		}	
	}
	
	/**
	 * <p>Calls the corresponding methods to handle each JPEG marker type
	 * 
	 * @throws Exception
	 */
	private void handleCurrentMarker() throws Exception{
		if(currentMarker != null){
			if(currentMarkerType == MARKER_TYPE_DHT){
				generateHuffmanTables(currentMarker);
				currentMarker = null;
				currentMarkerPos = 0;
			}else if(currentMarkerType == MARKER_TYPE_SOF_BASELINE){
				handleFrameData(currentMarker);
				currentMarker = null;
				currentMarkerPos = 0;
			}else if(currentMarkerType == MARKER_TYPE_SOF_EXTENDED){
				handleFrameData(currentMarker);
				currentMarker = null;
				currentMarkerPos = 0;
			}else if(currentMarkerType == MARKER_TYPE_SOS){
				handleScan(currentMarker);
				currentMarker = null;
				currentMarkerPos = 0;
			}else if(currentMarkerType == MARKER_TYPE_DRI){
				handleRestartInterval(currentMarker);
				currentMarker = null;
				currentMarkerPos = 0;
			}else{
				currentMarker = null;
				currentMarkerPos = 0;
			}
			
		}
	}
	/**
	 * <p>Handles Start of Frame (SOF) markers.
	 * 
	 * @param tableBytes 	byte array containing the SOF marker data
	 */
	private void handleFrameData(byte[] tableBytes){
		imageComponents = new ArrayList<Component>();
		int ctr = 0;
		
		byte[] length = new byte[2];
		length[0] = tableBytes[ctr];
		ctr++;
		length[1] = tableBytes[ctr];
		ctr++;
		short markerLength = (short)((length[0] << 8) | (length[1] & 0xFF));
		
		short samplePrecision = (short)((0x00 << 8) | (tableBytes[ctr] & 0xFF));
		ctr++;
		
		length[0] = tableBytes[ctr];
		ctr++;
		length[1] = tableBytes[ctr];
		ctr++;
		short imageHeight = (short)((length[0] << 8) | (length[1] & 0xFF));
		imageY = imageHeight;
		
		length[0] = tableBytes[ctr];
		ctr++;
		length[1] = tableBytes[ctr];
		ctr++;
		short imageWidth = (short)((length[0] << 8) | (length[1] & 0xFF));
		imageX = imageWidth;
		
		short componentNumber = (short)((0x00 << 8) | (tableBytes[ctr] & 0xFF));
		ctr++;
		
		for(int i=ctr; i<markerLength; i+=3){
			short componentID = (short)((0x00 << 8) | (tableBytes[i] & 0xFF));
			
			short componentHorizontalFactor = (short)(0x00 << 8 | (tableBytes[i+1] & 0b11110000) >> 4);
			short componentVerticalFactor = (short)(0x00 << 8 | (tableBytes[i+1] & 0b00001111));
			
			componentHorizontalMax = Math.max(componentHorizontalMax, componentHorizontalFactor);
			componentVerticalMax = Math.max(componentVerticalMax, componentVerticalFactor);
			
			short quantizationTableID = (short)((0x00 << 8) | (tableBytes[i+2] & 0xFF));
			
			if(componentNumber == 1){
				imageComponents.add(new Component(Component.SCAN_TYPE_NONINTERLEAVED, componentID, componentHorizontalFactor, componentVerticalFactor, quantizationTableID));
			}else{
				imageComponents.add(new Component(Component.SCAN_TYPE_INTERLEAVED, componentID, componentHorizontalFactor, componentVerticalFactor, quantizationTableID));
			}
		}
	}
	
	/**
	 * <p>Handles restart interval (DRI) markers
	 * @param tableBytes
	 */
	private void handleRestartInterval(byte[] tableBytes){
		restartInterval = (short)(((tableBytes[2] & 0xFF) << 8) | (tableBytes[3] & 0xFF));
	}
	
	/**
	 * <p>Handles the decompression of the scan data from a SOS marker.
	 * 
	 * <p>The method takes the compressed data, in byte array form, and reads it
	 * to contruct a list of the JPEG MCU's containing the quanitised DCT values
	 * for each color components.
	 * 
	 * @param tableBytes	the byte array contaning the compressed scan data
	 * @param mcuCount		the amount of MCU's the JPEG file contains
	 * @throws Exception
	 */
	private void decompressScanData(byte[] tableBytes, int mcuCount) throws Exception{
		//Convert the byte array to a String
		getSymbolByteString = Utils.byteArrayToString(tableBytes);
		
		
		int lastDC = 0;
		//Create the requied amount of MCUs
		for(int mcu=0; mcu<mcuCount; mcu++){
			//Create MCU object
			MCU currentMCU = new MCU();
			for(Component c: imageComponents){
				lastDC = 0;
				for(int i=0; i<c.componentVerticalFactor; i++){
					for(int j=0; j<c.componentHorizontalFactor; j++){				
						//Create dataUnit
						DataUnit dataUnit = new DataUnit();
						
						//Get DC
						//Get huffman encoded value
						byte huffmanSymbol = getSymbol(0, c.dcTableID);
						
						int dcValue;
						
						if(huffmanSymbol != 0){
							//Read unencoded bits of length huffman encoded value
							int readValue = Integer.parseInt(getSymbolByteString.substring(0, (int)huffmanSymbol), 2);
							getSymbolByteString = getSymbolByteString.substring(huffmanSymbol);
							
							//Calculate the difference
							int difference = extend(readValue, huffmanSymbol);
							
							//Calculate the DC value, and update the lastDC value
							dcValue = lastDC + difference;
							lastDC = dcValue;
						}else{
							//No extra bits for DC value
							dcValue = lastDC;
						}
						
						dataUnit.dcValue = dcValue;
						
						int processedACValues = 0;
						while(processedACValues < 63){
							huffmanSymbol = getSymbol(1, c.acTableID);
							short acZeroRun = (short)(0x00 << 8 | (huffmanSymbol & 0b11110000) >> 4);
							byte acMagValue = (byte)(huffmanSymbol & 0b00001111);
							
							if(acMagValue != 0){
								int readValue = Integer.parseInt(getSymbolByteString.substring(0, (int)acMagValue), 2);
								getSymbolByteString = getSymbolByteString.substring(acMagValue);
								
								//Calculate the AC coefficient
								int acCoefficient = extend(readValue, acMagValue);
								
								//Set the zero run
								dataUnit.setZeroRun(acZeroRun);
								processedACValues += acZeroRun;
								//Set the AC value
								dataUnit.setCurrentAC(acCoefficient);
								processedACValues += 1;
							}else{
								if(acZeroRun == 0b1111){
									dataUnit.setZeroRun(16);
									processedACValues += 16;
								}else if(acZeroRun == 0b0000){
									dataUnit.setZeroRun((63 - processedACValues));
									processedACValues += (63 - processedACValues);
								}
							}
						}
						currentMCU.addDataUnit(dataUnit);
					}
				}
			}
			mcuList.add(currentMCU);
		}
	}
	
	/**
	 * <p>Converts a magnitude value and additional value to a coefficient difference value
	 * 
	 * <p>Source: Based upon the algorithm 8.2 from 'Compressed Image File Formats JPEG, PNG, GIF, XBM, BMP' John Miano
	 * 
	 * @param additional	integer value specifying the additional bits
	 * @param magnitude		integer value specifying the magnitude
	 * @return				the calculated difference value
	 */
	private int extend(int additional, byte magnitude){		
		int vt = (1 << (magnitude - 1));
		
		if(additional < vt){
			return additional + ((-1) << magnitude) + 1;
		}else{
			return additional;
		}
	}
	
	private int magnitude;
	private int additional;
	/**
	 * <p>Does the opposite of the extend function, converting a coefficient value to
	 * a magnitude value and additional bits value.
	 * 
	 * <p>Source: Based upon the algorithm 8.2 from 'Compressed Image File Formats JPEG, PNG, GIF, XBM, BMP' John Miano
	 * 
	 * @param value 	coefficient value to be converted
	 */
	private void reverseExtend(int value){
		if(value>=0){
			additional = (byte) value;
		}else{
			value = -value;
			additional = (byte) ~value;
		}
		magnitude = 0;
		while(value != 0){
			value = value>>1;
			magnitude++;
		}
	}
	
	private String getSymbolByteString = "";
	/**
	 * <p>Gets the next Huffman Coded value from the 'getSymbolByteString' string, based upon
	 * the Huffman table specified.
	 * 
	 * 
	 * @param tableClass	the class of the Huffman Table (0 = DC, 1 = AC)
	 * @param tableID		the id of the Huffman Table
	 * @return				the next symbol present in the string.
	 * @throws Exception
	 */
	private byte getSymbol(int tableClass, int tableID) throws Exception{
		Huffman huffmanTable = null;
		
		
		if(tableClass == 0){
			huffmanTable = huffmanTablesDC.get(tableID);
		}else if(tableClass == 1){
			huffmanTable = huffmanTablesAC.get(tableID);
		}
			
		for(int i=0; i<16; i++){
			String currentCode = getSymbolByteString.substring(0, i+1);
			try{
				byte symbol = huffmanTable.getSymbol(currentCode);
				getSymbolByteString = getSymbolByteString.substring(i+1);
				return symbol; 
			}catch(Exception e){
				if(i==15){
					throw new Exception("Symbol not present for given code");
				}
			}
		}
		return (byte) 0xFF;
	}
	
	/**
	 * <p>Gets the embedding capacity for a hidden message in the JPEG structure.
	 * 
	 * @param acForMessage	the degradation value specifying how many AC values 
	 * 						are used for the message
	 * @return				an integer value containing the maximum capacity supported
	 */
	public int embedCapacity(int acForMessage){
		int capacity = 0;
		for(MCU currentMCU: mcuList){
			capacity += (currentMCU.data.size() * acForMessage);
		}
		capacity = (capacity-acForMessage) / 8;
		capacity = capacity - 4;
		return capacity;
	}
	
	public static final int JPEG_AC_EMBED_OFFSET = 8;
	/**
	 * {@inheritDoc}
	 * 
	 * <p> Provides embedding of a message in the current JPEG structure.
	 */
  	public void encode_msg(byte[] message, String passwordKey, int acForMessage) throws MessageTooLargeException{
  		byte[] messageLength = Utils.intToByteArray(message.length);
  		byte acMessage = (byte)acForMessage;
  		byte[] embedData = Utils.joinByteArray(messageLength, message);
  		
  		boolean acMessageSet = false;
  		
  		int ctr = 0;
		int bytePos = 0;
		if(acForMessage > 0 && acForMessage < (63-JPEG_AC_EMBED_OFFSET)){
  			for(MCU currentMCU: mcuList){
  				for(DataUnit dU: currentMCU.data){
  					if(ctr == embedData.length){
  						break;
  					}
  					if(acMessageSet){
  						byte currentMessageByte;
  	  					byte curBit;
  	  					int offset = 63 - (JPEG_AC_EMBED_OFFSET  + acMessage);
  	  					for(int i=offset; i<(offset+acMessage); i++){
  	  						if((bytePos!=0) && (bytePos%8==0)){
  	  							ctr++;
  	  							if(ctr == embedData.length){
	  								break;
	  							}
  	  						}
  	  						currentMessageByte = embedData[ctr];
  	  						curBit = (byte)((currentMessageByte >> (7-(bytePos%8))) & 0b00000001);
  	  						if(curBit == 0b00000001){
  	  							//Bit: 1
  	  							dU.acValues[JPEG_AC_EMBED_OFFSET+i] = 1;
  	  						}else{
  	  							//Bit: 0
  	  							dU.acValues[JPEG_AC_EMBED_OFFSET+i] = 0;
  	  						}
  	  						bytePos++;
  	  					}
  					}else{
  						for(int i=0; i<8; i++){
  	  						byte curBit = (byte)(acMessage >> (7-(i%8)) & 0b00000001);
  	  						if(curBit == 0b00000001){
  	  							//Bit: 1
  	  							dU.acValues[JPEG_AC_EMBED_OFFSET+i] = 1;
  	  						}else{
  	  							//Bit: 0
  	  							dU.acValues[JPEG_AC_EMBED_OFFSET+i] = 0;
  	  						}
  	  					}
  						acMessageSet = true;
  					}
  				}
  			}
  		}
		if(ctr != embedData.length){
			throw new MessageTooLargeException("Message Too Large");
		}
	}
  	
  	/**
	 * {@inheritDoc}
	 * 
	 * <p> Provides recovery of a message from the current JPEG structure.
	 */
  	public byte[] decode_msg(String passwordKey) throws MessageTooLargeException, Exception{
  		//Byte for getting the AC degradation value
  		byte acMessage = (byte)0x00;
  		boolean acMessageFound = false;
  		
  		//Byte array for getting the recovered data.
  		byte[] recoveredData = new byte[0];
  		
		//Goes through the JPEG files MCUs, and dataUnits, to construct a byte array
  		//from the embedded AC values
  		int ctr = 0;
		int bytePos = 0;
		byte currentByte = (byte)0x00;
		int capacity = 0;
		for(MCU currentMCU: mcuList){
			for(DataUnit dU: currentMCU.data){
				if(acMessageFound){
					//If the AC degradation value has been recovered
					int offset = 63 - (JPEG_AC_EMBED_OFFSET  + acMessage);
	  				for(int i=offset; i<(offset+acMessage); i++){
  						if((bytePos!=0) && (bytePos%8==0)){
  							if(ctr == capacity){
  								break;
  							}
  							recoveredData[ctr] = currentByte;
  							ctr++;
  							currentByte = (byte)0x00;
  						}
  						
  						if(dU.acValues[JPEG_AC_EMBED_OFFSET+i] == 1){
  							currentByte = (byte)(currentByte << 1);
  							currentByte = (byte) (currentByte | 0b00000001);
  						}else if(dU.acValues[JPEG_AC_EMBED_OFFSET+i] == 0){
  							currentByte = (byte)(currentByte << 1);
  							currentByte = (byte) (currentByte | 0b00000000);
  						}
  						bytePos++;
  					}					
				}else{
					//if the AC degradation value needs to be recovered
					for(int i=0; i<8; i++){
						if(dU.acValues[JPEG_AC_EMBED_OFFSET+i] == 1){
							acMessage = (byte)(acMessage << 1);
							acMessage = (byte) (acMessage | 0b00000001);
						}else if(dU.acValues[JPEG_AC_EMBED_OFFSET+i] == 0){
							acMessage = (byte)(acMessage << 1);
							acMessage = (byte) (acMessage | 0b00000000);
						}
  					}
					acMessageFound = true;
					capacity = embedCapacity(acMessage)+4;
					recoveredData = new byte[capacity];
				}		
			}
		}
		
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
  	 * <p>Generates a string representation of the scan data of the JPEG structure.
  	 * 
  	 * @return	a string containing the compressed scan data of the JPEG structure.
  	 * @throws Exception
  	 */
  	private String encodeScan() throws Exception{
  		String scanString = "";
  		
  		//Iterate through each MCU
  		for(MCU currentMCU: mcuList){
  			int dataUnitCtr = 0;
  			
  			//Iterate though each of the components
  			for(Component c: imageComponents){
  				int lastDC = 0;
  				for(int i=0; i<c.componentVerticalFactor; i++){
  					for(int j=0; j<c.componentHorizontalFactor; j++){
  						DataUnit currentDataUnit = currentMCU.data.get(dataUnitCtr);
  						
  						//Get the correct HuffmanTables for the current AC and DC values
  						int currentDCTableID = c.dcTableID;
  						int currentACTableID = c.acTableID;
  						Huffman dcTable = this.huffmanTablesDC.get(currentDCTableID);
  						Huffman acTable = this.huffmanTablesAC.get(currentACTableID);
  						
  						//Write the DC value to the string
  						int dcDifference = currentDataUnit.dcValue - lastDC;
  						lastDC = currentDataUnit.dcValue;
  						
  						reverseExtend(dcDifference);
  						
  						String dcMag = "";
  						String magString = "";
  						if(magnitude > 0){
  							String intString = Integer.toBinaryString((additional));
  							if(intString.length() < (magnitude-1)){
  								for(int x=intString.length();x<(magnitude-1);x++){
  									intString = "0" + intString;
   								}
  							}
  							if(dcDifference >= 0){
  								magString = 1 + intString.substring((intString.length()-(magnitude-1)));
  							}else{
  								magString = 0 + intString.substring((intString.length()-(magnitude-1)));
  							}
  						}
						try{
							dcMag = this.getHuffmanCode(0, currentDCTableID, (byte)magnitude);
							scanString = scanString + dcMag;
							if(magnitude > 0){
								scanString = scanString + magString;
							}
						}catch(Exception e){
							if(dcTable.checkSpace()){
								dcMag = dcTable.addNewSymbol((byte)magnitude);
								scanString = scanString + dcMag;
								if(magnitude > 0){
									scanString = scanString + magString;
								}
							}
						}

						//Write the AC values to the string
  						byte zeroRun = 0;
  						int[] acValues = currentDataUnit.acValues;
  						for(int acCtr=0; acCtr<acValues.length; acCtr++){
  							if(acValues[acCtr] != 0){
  								while(zeroRun >= 16){
  									try{
  										scanString = scanString + this.getHuffmanCode(1, currentACTableID, (byte)0xF0);
  									}catch(Exception e){
  										if(acTable.checkSpace()){
  											scanString = scanString + acTable.addNewSymbol((byte)0xF0);
  										}
  									}
  									zeroRun -= 16;
  								}
  								reverseExtend(acValues[acCtr]);
								String addString = "";
		  						if(magnitude > 0){
		  							String intString = Integer.toBinaryString((additional));
		  							if(intString.length() < (magnitude-1)){
		  								for(int x=intString.length();x<(magnitude-1);x++){
		  									intString = "0" + intString;
		   								}
		  							}  							
		  							if(acValues[acCtr] >= 0){
		  								addString = 1 + intString.substring((intString.length()-(magnitude-1)));
		  							}else{
		  								addString = 0 + intString.substring((intString.length()-(magnitude-1)));
		  							}
		  						}
		  						byte acCode = ((byte)(((zeroRun << 4) | magnitude) & 0xFF));
  								try{
  									scanString = scanString + this.getHuffmanCode(1, currentACTableID, acCode);
  									scanString = scanString + addString;
  								}catch(Exception e){
  									if(acTable.checkSpace()){
  										scanString = scanString + acTable.addNewSymbol(acCode);
  										scanString = scanString + addString;
  									}
  								}
  								zeroRun=0;
  							}else{
  								zeroRun++;
  							}
  						}
  						if(zeroRun != 0){
							try{
								scanString = scanString + this.getHuffmanCode(1, currentACTableID, (byte)0x00);
							}catch(Exception e){
								if(acTable.checkSpace()){
									scanString = scanString + acTable.addNewSymbol((byte)0x00);
								}
							}
						}
  						dataUnitCtr++;
  					}
  				}
  			}
  		}
  		//Apply padding to the scan to fill byte space
  		if(scanString.length() % 8 != 0){
  			int x = scanString.length() / 8;
  			x = (x+1)*8;
  			x = x - scanString.length();
  			for(int i=0; i<x;i++){
  				scanString = scanString + "1";
  			}
  		}
  		
  		//Add required 0x00 padding after any occurrences of 0xFF in the string
  		byte[] scanBytes = Utils.stringToByte(scanString);
  		byte[] padderByte = {(byte)0x00};
  		for(int i=0; i<scanBytes.length; i++){
  			if(scanBytes[i] == (byte)0xFF){
  				scanBytes = Utils.insertAt(scanBytes, padderByte, i+1); 
  				i=i+1;
  			}
  		}
  		scanString = "";
  		for(int i=0; i<scanBytes.length;i++){
  			scanString = scanString + Integer.toBinaryString((scanBytes[i] & 0xFF)+ 0x100).substring(1);
		}
  		return scanString;
  	}
  	
  	/**
  	 * <p>Gets the matching huffman symbol for a supplied code.
  	 * 
  	 * @param tableClass	the class of the Huffman table
  	 * @param tableID		the id of the Huffman table
  	 * @param code			the code to get the Huffman symbol for
  	 * @return
  	 * @throws Exception
  	 */
	private String getHuffmanCode(int tableClass, int tableID, byte code) throws Exception{
		Huffman huffmanTable = null;
		
		if(tableClass == 0){
			huffmanTable = huffmanTablesDC.get(tableID);
		}else if(tableClass == 1){
			huffmanTable = huffmanTablesAC.get(tableID);
		}
		return huffmanTable.getCode(code);
	}
	
	/**
	 * <p>Handles Start of Scan (SOS) markers
	 * @param tableBytes
	 * @throws Exception
	 */
	private void handleScan(byte[] tableBytes) throws Exception{
		int ctr = 0;
		
		byte[] length = new byte[2];
		length[0] = tableBytes[ctr];
		ctr++;
		length[1] = tableBytes[ctr];
		ctr++;
		short markerLength = (short)((length[0] << 8) | (length[1] & 0xFF));
		
		short compCount = (short)(0x00 << 8 | tableBytes[ctr] & 0xFF);
		ctr++;
		
		ArrayList<Component> compList = new ArrayList<Component>();
		for(int i=0; i<compCount; i++){
			short compID = (short)((0x00 << 8) | (tableBytes[ctr] & 0xFF));
			ctr++;
			
			short dcID = (short)(0x00 << 8 | (tableBytes[ctr] & 0b11110000) >> 4);
			short acID = (short)(0x00 << 8 | (tableBytes[ctr] & 0b00001111));
			ctr++;
					
			for(Component c: imageComponents){
				if(c.componentID == compID){
					c.dcTableID = dcID;
					c.acTableID = acID;
					compList.add(c);
				}
			}
		}
		imageComponents = compList;
		
		byte selectionStart = tableBytes[ctr];
		ctr++;
		
		byte selectionEnd = tableBytes[ctr];
		ctr++;

		byte approx = tableBytes[ctr];
		ctr++;
		
		int MCUx = -1;
		int MCUy = -1; 
		
		for(int i=0; i<imageComponents.size(); i++){
			imageComponents.get(i).calPixelX(componentHorizontalMax);
			imageComponents.get(i).calPixelY(componentVerticalMax);
			
			if(imageComponents.size() == 1){
				imageComponents.get(i).calMCU(imageX, imageY);
			}else{
				imageComponents.get(i).calMCUInterleaved(imageX, imageY, componentHorizontalMax, componentVerticalMax);
			}
			MCUx = imageComponents.get(i).MCUx;
			MCUy = imageComponents.get(i).MCUy;
		}
		
		byte[] compressedData = Arrays.copyOfRange(tableBytes, ctr, tableBytes.length);
		scanDataLength = compressedData.length;
		
		decompressScanData(compressedData, (MCUx * MCUy));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Converts the JPEG into a byte array format.
	 */
	public byte[] genStego() throws Exception{
		boolean outputHuffmanTables = false;
		byte[] newBytes = originalBytes;
		for(int i=0; i< newBytes.length; i++){
			if(newBytes[i] == (byte)0xFF){
				i++;
				if(newBytes[i] == MARKER_TYPE_SOI){
				}else if(newBytes[i] == MARKER_TYPE_SOF_BASELINE){
				}else if(newBytes[i] == MARKER_TYPE_SOF_EXTENDED){
				}else if(newBytes[i] == MARKER_TYPE_DHT){
					int start = i-1;
					
					byte[] length = new byte[2];
					i++;
					length[0] = newBytes[i];
					i++;
					length[1] = newBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					int end = i+(markerLength-1);
					
					//Remove the old huffman tables
					newBytes = Utils.joinByteArray(Arrays.copyOfRange(newBytes, 0, start), Arrays.copyOfRange(newBytes, end, newBytes.length));
					i = start-1;
					
					//Insert new ones 
					int insertAtIndex = start;
					if(!outputHuffmanTables){
						encodeScan();
						for(int j=0; j<huffmanTablesDC.size(); j++){
							Huffman h = huffmanTablesDC.get(j);
							
							byte[] hBytes = Utils.stringToByte(h.genDHTString());
							byte[] bytesDHT;
							byte[] markerBytes = {(byte)0xFF, (byte)MARKER_TYPE_DHT};
							byte[] lengthBytes = Utils.shortToByteArray((short) (hBytes.length+2));
							bytesDHT = Utils.joinByteArray(markerBytes, lengthBytes);
							bytesDHT = Utils.joinByteArray(bytesDHT, hBytes);
							
							newBytes = Utils.insertAt(newBytes, bytesDHT, insertAtIndex);
							insertAtIndex = insertAtIndex + bytesDHT.length;
						}
						for(int j=0; j<huffmanTablesAC.size(); j++){
							Huffman h = huffmanTablesAC.get(j);
							
							byte[] hBytes = Utils.stringToByte(h.genDHTString());
							byte[] bytesDHT;
							byte[] markerBytes = {(byte)0xFF, (byte)MARKER_TYPE_DHT};
							byte[] lengthBytes = Utils.shortToByteArray((short) (hBytes.length+2));
							bytesDHT = Utils.joinByteArray(markerBytes, lengthBytes);
							bytesDHT = Utils.joinByteArray(bytesDHT, hBytes);
							
							newBytes = Utils.insertAt(newBytes, bytesDHT, insertAtIndex);
							insertAtIndex = insertAtIndex + bytesDHT.length;
						}
						i = insertAtIndex-1;
						outputHuffmanTables = true;
					}
				}else if(newBytes[i] == MARKER_TYPE_DQT){
				}else if(newBytes[i] == MARKER_TYPE_DRI){
				}else if(newBytes[i] == MARKER_TYPE_SOS){
					int start = i-1;
					
					byte[] length = new byte[2];
					i++;
					int headerStart = i;
					length[0] = newBytes[i];
					i++;
					length[1] = newBytes[i];
					short markerLength = (short) ((length[0] << 8) | (length[1] & 0xFF));
					int end = i + markerLength;
					
					byte[] markerHeader = new byte[markerLength];
					for(int j=0; j<markerLength; j++){
						markerHeader[j] = newBytes[headerStart + j];
					}

					newBytes = Utils.joinByteArray(Arrays.copyOfRange(newBytes, 0, start), Arrays.copyOfRange(newBytes, (end + scanDataLength + 1), newBytes.length));
					
					byte[] markerBytes = {(byte)0xFF, (byte)MARKER_TYPE_SOS};
					byte[] bytesSOSHeader = Utils.joinByteArray(markerBytes, markerHeader);
					byte[] bytesSOSData = Utils.stringToByte(encodeScan());
					byte[] bytesSOS = Utils.joinByteArray(bytesSOSHeader, bytesSOSData);
					newBytes = Utils.insertAt(newBytes, bytesSOS, start);
					
					i = start + bytesSOS.length;
				}else if(newBytes[i] == MARKER_TYPE_COM){
				}else if(newBytes[i] >= MARKER_TYPE_APP_START && newBytes[i] <= MARKER_TYPE_APP_END){
				}else if(newBytes[i] == MARKER_TYPE_EOI){
					break;
				}else if(newBytes[i] == 0x00){
				}
			}
		}	
		return newBytes;
	}
	
	/**
	 * <p>Handles Define Huffman Tables (DHT) markers.
	 * 
	 * <p>Generates a Huffman tree representation for each define table in the define structure.
	 * 
	 * @param tableBytes	byte array containing the DHT data
	 */
	private void generateHuffmanTables(byte[] tableBytes){
		short tableClass;
		short tableID;
		short[] codeLengths = new short[16];
		
		short[] codeSymbols;
		
		int ctr = 0;
		
		byte[] length = new byte[2];
		length[0] = tableBytes[ctr];
		ctr++;
		length[1] = tableBytes[ctr];
		short markerLength = (short)(((length[0] << 8) | (length[1] & 0xFF))-2);
		ctr++;
		
		boolean finished = false;
		while(!finished){			
			tableClass = (short)(0x00 << 8 | (tableBytes[ctr] & 0b11110000) >> 4);
			tableID = (short)(0x00 << 8 | (tableBytes[ctr] & 0b00001111));
			ctr++;
			
			int totalCodeLength = 0;
			for(int j=0; j<16; j++){
				short codeLength = (short)(0x00 << 8 | (tableBytes[ctr] & 0b11111111));
				codeLengths[j] = codeLength;
				totalCodeLength += codeLength;
				ctr++;
			}	
			codeSymbols = new short[totalCodeLength];
			for(int j=0; j<totalCodeLength; j++){
				codeSymbols[j] = (short)(0x00 << 8 | (tableBytes[ctr] & 0b11111111));
				ctr++;
			}
			if(tableClass == 0){
				huffmanTablesDC.put((int)tableID, new Huffman(tableClass, tableID, codeLengths, codeSymbols));
			}else if(tableClass == 1){
				huffmanTablesAC.put((int)tableID, new Huffman(tableClass, tableID, codeLengths, codeSymbols));
			}
			if(ctr == tableBytes.length){
				finished = true;
			}
		}
	}
}
