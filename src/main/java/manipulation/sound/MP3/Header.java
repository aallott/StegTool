package manipulation.sound.MP3;

import manipulation.BitStream;

/**
 * <p>Class which handles Header's, of frames, of a MP3 file.
 * 
 * @author Ashley Allott
 */
public class Header {
	
	public static final int CHANNEL_MODE_STEREO = (byte)0b00;
	public static final int CHANNEL_MODE_JOINT_STEREO = (byte)0b01;
	public static final int CHANNEL_MODE_DUAL_CHANNEL = (byte)0b10;
	public static final int CHANNEL_MODE_SINGLE_CHANNEL = (byte)0b11;
	
	public int sync;
	public byte versionID;
	public byte layerDesc;
	public byte protectionBit;
	public byte bitrateIndex;
	public byte sRFIndex;
	public byte paddingBit;
	public byte privateBit;
	public byte channelMode;
	public byte modeExtension;
	public byte copyrightBit;
	public byte originalBit;
	public byte emphasis;
	public int frameSize;
	public int samplesPerFrame;
	public double bitRate;
	public double sampleRate;
	public boolean crc;
	public int length;
	
	private BitStream bS;
	
	/**
	 * <p>Constructor, creates a new Header object and constructs the data.
	 * 
	 * @param headerBytes	byte array containing the header data
	 * @throws Exception
	 */
	public Header(byte[] headerBytes) throws Exception{
		this.bS = new BitStream(headerBytes);
		this.parseHeaderBlock();
	}
	
	/**
	 * <p>Reads the header bytes and gets the data.
	 * 
	 * @return		the size of the frame (in bytes)
	 * @throws Exception
	 */
	private int parseHeaderBlock() throws Exception{
		sync = bS.getBits(11);
		
		versionID = (byte)bS.getBits(2);
		
		layerDesc = (byte)bS.getBits(2);
		
		protectionBit =  (byte)bS.getBits(1);

		bitrateIndex =  (byte)bS.getBits(4);

		sRFIndex =  (byte)bS.getBits(2);

		paddingBit =  (byte)bS.getBits(1);

		privateBit =  (byte)bS.getBits(1);

		channelMode =  (byte)bS.getBits(2);
		
		modeExtension =  (byte)bS.getBits(2);

		copyrightBit =  (byte)bS.getBits(1);

		originalBit =  (byte)bS.getBits(1);

		emphasis =  (byte)bS.getBits(2);
		
		if(protectionBit == 0b0){
			crc = true;
		}else{
			crc = false;
		}
		
		samplesPerFrame = MP3Codec.getSamplePerFrame(versionID, layerDesc);

		bitRate = (double)MP3Codec.getBitrate(bitrateIndex, versionID, layerDesc) * 1000;

		sampleRate =  (double)MP3Codec.getSampleRate(sRFIndex, versionID);
		
		int paddingSize;
		if(layerDesc == MP3Codec.LAYER_1){
			paddingSize = 4;
		}else{
			paddingSize = 1;
		}
		if(paddingBit == 0b1){
			paddingSize = 1 * paddingSize;
		}else{
			paddingSize = 0;
		}
		length = (int)((samplesPerFrame / sampleRate) * 1000);
		frameSize = (int)((samplesPerFrame / (double)8 * bitRate) / sampleRate) + paddingSize;
		return frameSize;
	}
	
	/**
	 * <p>Outpus the contents of the header via a Bitstream.
	 * 
	 * @param bS  	passed bitStream to add the current header's data to
	 * @return		bitStream containing the header data
	 */
	public BitStream output(BitStream bS){
		BitStream oS = new BitStream(null);
		if(bS != null && bS.getData() != null){
			oS = new BitStream(bS.getData());
		}
		
		for(int i=0; i<11; i++){
			oS.addBit((byte)((sync >> (10-i)) & 0b1));
		}
		
		for(int i=0; i<2; i++){
			oS.addBit((byte)((versionID >> (1-i)) & 0b1));
		}

		for(int i=0; i<2; i++){
			oS.addBit((byte)((layerDesc >> (1-i)) & 0b1));
		}

		oS.addBit((byte)((protectionBit) & 0b1));
		
		for(int i=0; i<4; i++){
			oS.addBit((byte)((bitrateIndex >> (3-i)) & 0b1));
		}
		for(int i=0; i<2; i++){
			oS.addBit((byte)((sRFIndex >> (1-i)) & 0b1));
		}
		
		oS.addBit((byte)((paddingBit) & 0b1));
		
		oS.addBit((byte)((privateBit) & 0b1));
		
		for(int i=0; i<2; i++){
			oS.addBit((byte)((channelMode >> (1-i)) & 0b1));
		}
		
		for(int i=0; i<2; i++){
			oS.addBit((byte)((modeExtension >> (1-i)) & 0b1));
		}
		
		oS.addBit((byte)((copyrightBit) & 0b1));
		
		oS.addBit((byte)((originalBit) & 0b1));
		
		for(int i=0; i<2; i++){
			oS.addBit((byte)((originalBit >> (1-i)) & 0b1));
		}
	
		return oS;
	}
}
