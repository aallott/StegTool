package manipulation;

import java.util.Arrays;

/**
 * <p>Class handles reading and writing of bits; adding a layer of abstraction to 
 * make the process of reading varied amount of bits easier.
 * 
 * @author Ashley Allott
 */
public class BitStream {
	
	private  byte[] data;
	private int endPointer;
	
	private int ctr;
	private int bitPos;
	
	/**
	 * <p>Constructor, create a new BitStream instance.
	 * @param dataBytes	a byte array to add to the BitStream for reading
	 */
	public BitStream(byte[] dataBytes){
		this.data = dataBytes;
		if(dataBytes != null){
			this.endPointer = (dataBytes.length*8);
		}else{
			this.endPointer = 0;
		}
		this.ctr = 0;
		this.bitPos = 0;
	}
	
	/**
	 * <p>Gets a specified number of bits from the bitStream (1,32), returning
	 * the bits in the form of a rightmost shifted integer.
	 * 
	 * <p>The pointer shifts with the read bits. 
	 * 
	 * @param numberOfBits	value of bits to read from the stream
	 * @return				integer value containing the read bits
	 * @throws BitStreamException
	 */
	public int getBits(int numberOfBits) throws BitStreamException{
		int returnValue = 0x00000000;
		if(numberOfBits > 0 && numberOfBits <= 32){
			for(int i=0; i<numberOfBits; i++){
				if(bitPos == 8){
					ctr++;
					bitPos = 0;
					//if(ctr == data.length){
					if(((ctr*8) + bitPos) == endPointer){
						throw new BitStreamException("Reached end of stream");
					}
				}
				returnValue = returnValue << 1;
				returnValue = (returnValue | (data[ctr] >> (7-bitPos) & 0b1));
				bitPos++;
			}
		}
		return returnValue;
	}
	
	/**
	 * <p>Gets a specified number of bits from the bitStream with a specified starting
	 * position in the stream.
	 * 
	 * @param start			pointer index to start the reading from
	 * @param numberOfBits	value of bits to read from the stream
	 * @return				integer value containing the read bits
	 * @throws BitStreamException
	 */
	public int getBits(int start, int numberOfBits) throws BitStreamException{
		int curPos = this.getPosition();
		this.resetStream(start);
		
		int returnValue = 0x00000000;
		if(numberOfBits > 0 && numberOfBits < 32){
			for(int i=0; i<numberOfBits; i++){
				if(bitPos == 8){
					ctr++;
					bitPos = 0;
					//if(ctr == data.length){
					if(((ctr*8) + bitPos) == endPointer){
						throw new BitStreamException("Reached end of stream");
					}
				}
				returnValue = returnValue << 1;
				returnValue = (returnValue | (data[ctr] >> (7-bitPos) & 0b1));
				bitPos++;
			}
		}
		
		this.resetStream(curPos);
		return returnValue;
	}
	
	/**
	 * <p>Resets the bitStream back to the beginning of the data.
	 */
	public void resetStream(){
		ctr = 0;
		bitPos = 0;
	}
	
	/**
	 * <p>Resets the bitStream to the specified position
	 * 
	 * @param pos	integer index value to set the stream to
	 */
	public void resetStream(int pos){
		ctr = pos / 8;
		bitPos = (pos % 8) - 1;
	}
	
	/**
	 * <p>Checks if the data has reached the end of the data.
	 * 
	 * @return	boolean value indiccating if the data has reached 
	 * the end of the stream
	 */
	public boolean isEndOfData(){
		if(ctr == data.length){
			return true;
		}else if(ctr == (data.length-1)){
			if(bitPos == 8){
				return true;
			}
		}
		return false;
	}
	/**
	 * <p>Gets the position that the bitStream is currently at in the data.
	 * @return
	 */
	public int getPosition(){
		return (ctr*8) + bitPos;
 	}
	/**
	 * <p>Gets the end position of the data.
	 * @return
	 */
	public int getEndPosition(){
		return endPointer;
	}
	/**
	 * <p>Adds a bit to the data the stream reads from.
	 * 
	 * <p>Takes the rightmost bit of a byte as the bit to be added.
	 * 
	 * @param bit	the bit value (in byte form) to add to the bitStream
	 */
	public void addBit(byte bit){
		if(data == null){
			data = new byte[1];
		}
		if(endPointer == (data.length*8)){
			//Extend Data Array
			data = Arrays.copyOf(data, (data.length+1));
			
			//Initialise new byte
			data[data.length-1] = (byte)0x00;
			
			//Add new bit
			byte movedBit = (byte)((bit & 0b1) << 7-(endPointer % 8));
			data[data.length-1] = (byte)(data[data.length-1] | movedBit);	
			endPointer++;
		}else{
			//Add new bit
			byte movedBit = (byte)((bit & 0b1) << 7-(endPointer % 8));
			data[data.length-1] = (byte)(data[data.length-1] | movedBit);
			endPointer++;
		}
	}
	/**
	 * <p>Adds a byte to the data the stream reads from.
	 * 
	 * @param byte_	the byte to be added to the bitStream
	 */
	public void addByte(byte byte_){
		for(int i=0; i<8; i++){
			byte bit = (byte) (byte_ >> 7-i);
			this.addBit(bit);
		}
	}
	/**
	 * <p>Adds a byte array to the data the stream reads from.
	 * 
	 * @param bytes	the byte array to be added to the bitSteam
	 */
	public void addBytes(byte[] bytes){
		for(int i=0; i<bytes.length; i++){
			this.addByte(bytes[i]);
		}
	}
	
	/**
	 * <p>Produces a string representation of the byte data in the bitStream.
	 */
	public String toString(){
		String returnString = "";
		try{
			int pos = this.getPosition();
			this.resetStream();
			for(int i=0; i<this.endPointer; i++){
				returnString += Integer.toBinaryString(this.getBits(1)) ;
			}
			this.resetStream(pos);
		}catch(BitStreamException e){
			e.printStackTrace();
		}
		return returnString;
	}
	
	/**
	 * <p>Gets the entire data array the bitStream contains.
	 * @return	byte array of the bitStream data.
	 */
	public byte[] getData(){
		return data;
	}
}
