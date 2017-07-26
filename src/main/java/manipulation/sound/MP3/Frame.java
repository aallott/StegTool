package manipulation.sound.MP3;

import manipulation.BitStream;
import manipulation.BitStreamException;

/**
 * <p>Class which handles frames of a MP3 file.
 * 
 * @author Ashley Allott
 */
public class Frame {
	
	BitStream bS;
	BitStream mainData;
	public Header header;
	public byte[] crc;
	public SideInfo sideInfo;
	public float[][][] samples;
	public int sideInfoLength;
	public BitStream previousData;
	
	/**
	 * <p>Class which handles the SideInfo of a  frame.
	 * 
	 * @author Ashley Allott
	 */
	public class SideInfo{
		public int mainDataBegin;
		public byte privateBits;
		public Channel[] ch;
		
		/**
		 * <p>Constructor, creates a new SideInfo.
		 * 
		 * <p>Initialises 2 channels as a frame is at most 2 channels.
		 */
		public SideInfo(){
			ch = new Channel[2];
			ch[0] = new Channel();
			ch[1] = new Channel();
		}
	}
	
	/**
	 * <p>Class which handles a Channel of a frame.
	 * 
	 * @author Ashley Allott
	 */
	public class Channel{ 
		public Granule[] gr;
		public byte[] scfsi;
		
		/**
		 * <p>Constructor, creates a new Channel.
		 */
		public Channel(){
			gr = new Granule[2];
			gr[0] = new Granule();
			gr[1] = new Granule();
			scfsi = new byte[4];
		}
	}
	
	/**
	 * <p>Class which handles a Granule of a frame.
	 * 
	 * @author Ashley Allott
	 */
	public class Granule{
		public int part2_3_length;
		public int big_Values;
		public int global_Gain;
		public int scaleFac_Compress;
		public byte window_Switching;
		public byte block_type;
		public byte mixed_blockFlag;
		public byte[] table_select;
		public byte[] subblock_gain;
		public byte region0_count;
		public byte region1_count;
		public byte preflag;
		public byte scalfac_scale;
		public byte count1table_select;
	}
	
	/**
	 * <p>Constructor, creates a new Frame object, and gets the data 
	 * from the frame bytes.
	 * 
	 * @param header		the header of the current frame
	 * @param frameBytes	byte array containing the frame data
	 */
	public Frame(Header header, byte[] frameBytes){
		this.bS = new BitStream(frameBytes);
		this.header = header;
		
		this.samples = new float[2][2][576];
		
		this.getCRC();
		this.getSideInfo();
		this.getMainData();
	}
	
	/**
	 * <p>Constructor, creates a new Frame object, and gets the data 
	 * from the frame bytes.
	 * 
	 * @param header		the header of the current frame
	 * @param frameBytes	byte array containing the frame data
	 * @param mainData		data of previous frames for bit reservoir
	 */
	public Frame(Header header, byte[] frameBytes, BitStream mainData){
		this.bS = new BitStream(frameBytes);
		this.previousData = mainData;
		this.header = header;
		
		this.samples = new float[2][2][576];
		
		this.getCRC();
		this.getSideInfo();
		this.getMainData();
	}
	
	/**
	 * <p>Gets the CRC data from the bitStream if present
	 */
	private void getCRC(){
		if(header.crc){
			try{
				crc = new byte[2];
				for(int i=0; i<crc.length; i++){
					crc[i] = (byte)bS.getBits(8);
				}
			}catch(BitStreamException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>Gets the side information from the bitStream
	 */
	private void getSideInfo(){
		sideInfo = new SideInfo();
		try{
			sideInfo.mainDataBegin = bS.getBits(9);

			if(header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
				//5 bit length
				sideInfo.privateBits = (byte)bS.getBits(5);
			}else{
				//3 bit length
				sideInfo.privateBits = (byte)bS.getBits(3);
			}
			
			//4 bit length
			int channels = 0;
			if(header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
				channels = 1;
			}else{
				channels = 2;
			}
			for(int i=0; i<channels; i++){
				sideInfo.ch[i].scfsi[0] = (byte)bS.getBits(1);
				sideInfo.ch[i].scfsi[1] = (byte)bS.getBits(1);
				sideInfo.ch[i].scfsi[2] = (byte)bS.getBits(1);
				sideInfo.ch[i].scfsi[3] = (byte)bS.getBits(1);
			}
				
			for(int gr=0; gr<2; gr++){
				for(int ch=0; ch<channels; ch++){
					//12 bit length
					sideInfo.ch[ch].gr[gr].part2_3_length = bS.getBits(12);
					
					//9 bit length
					sideInfo.ch[ch].gr[gr].big_Values = bS.getBits(9);
					
					//8 bit length
					sideInfo.ch[ch].gr[gr].global_Gain = bS.getBits(8);
					
					//4 bit length
					sideInfo.ch[ch].gr[gr].scaleFac_Compress = bS.getBits(4);
					
					//1 bit length
					sideInfo.ch[ch].gr[gr].window_Switching = (byte)bS.getBits(1);
					
					if(sideInfo.ch[ch].gr[gr].window_Switching != 0){
						//2 bit length
						sideInfo.ch[ch].gr[gr].block_type =  (byte)bS.getBits(2);
						
						//1 bit length
						sideInfo.ch[ch].gr[gr].mixed_blockFlag = (byte)bS.getBits(1);
						
						//10 bit length (5x2)
						sideInfo.ch[ch].gr[gr].table_select = new byte[2];
						sideInfo.ch[ch].gr[gr].table_select[0] = (byte)bS.getBits(5);							
						sideInfo.ch[ch].gr[gr].table_select[1] = (byte)bS.getBits(5);	
						
						//9 bits length (3x3)
						sideInfo.ch[ch].gr[gr].subblock_gain = new byte[3];
						sideInfo.ch[ch].gr[gr].subblock_gain[0] = (byte)bS.getBits(3);
						sideInfo.ch[ch].gr[gr].subblock_gain[1] = (byte)bS.getBits(3);
						sideInfo.ch[ch].gr[gr].subblock_gain[2] = (byte)bS.getBits(3);
						
						if(sideInfo.ch[ch].gr[gr].block_type == 0){
							//ERROR
						}else if(sideInfo.ch[ch].gr[gr].block_type == 2 && sideInfo.ch[ch].gr[gr].mixed_blockFlag == 0){
							sideInfo.ch[ch].gr[gr].region0_count = 8;
						}else{
							sideInfo.ch[ch].gr[gr].region0_count = 7;
						}	
						sideInfo.ch[ch].gr[gr].region1_count = (byte)(20 - sideInfo.ch[ch].gr[gr].region0_count);
						
					}else{
						
						sideInfo.ch[ch].gr[gr].block_type = 0;
						sideInfo.ch[ch].gr[gr].mixed_blockFlag = 0;
						
						
						//15 bit length (5x3)
						sideInfo.ch[ch].gr[gr].table_select = new byte[3];
						sideInfo.ch[ch].gr[gr].table_select[0] = (byte) bS.getBits(5);
						sideInfo.ch[ch].gr[gr].table_select[1] = (byte) bS.getBits(5);
						sideInfo.ch[ch].gr[gr].table_select[2] = (byte) bS.getBits(5);
						
						//4 bit length
						sideInfo.ch[ch].gr[gr].region0_count = (byte)bS.getBits(4);
						
						//3 bit length
						sideInfo.ch[ch].gr[gr].region1_count = (byte)bS.getBits(3);
					}
					//1 bit length
					sideInfo.ch[ch].gr[gr].preflag = (byte)bS.getBits(1);
					
					//1 bit length
					sideInfo.ch[ch].gr[gr].scalfac_scale = (byte)bS.getBits(1);
		
					//1 bit length
					sideInfo.ch[ch].gr[gr].count1table_select = (byte)bS.getBits(1);
				}
			}
			
			sideInfoLength = bS.getPosition();
		}catch(BitStreamException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Gets the mainData, applying bitReservior if the main_data_begin specifies it.
	 */
	private void getMainData(){
		try{
			int length = bS.getEndPosition() - bS.getPosition();
			this.mainData = new BitStream(null);
			for(int i=0; i<length; i++){
				mainData.addBit((byte)bS.getBits(1));
			}
		}catch(BitStreamException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Outpus the contents of the frame via a Bitstream.
	 * 
	 * @return 	bitStream containing the frame data
	 */
	public BitStream output(){
		BitStream oS = header.output(null);
		if(header.crc){
			oS.addByte(crc[0]);
			oS.addByte(crc[1]);
		}
		
				
		for(int i=0; i<9; i++){
			oS.addBit((byte)((sideInfo.mainDataBegin >> (8-i)) & 0b1));
		}
		
		if(header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
			//5 bit length
			for(int i=0; i<5; i++){
				oS.addBit((byte)((sideInfo.privateBits >> (4-i)) & 0b1));
			}
		}else{
			//3 bit length
			for(int i=0; i<3; i++){
				oS.addBit((byte)((sideInfo.privateBits >> (2-i)) & 0b1));
			}
		}
		
		//4 bit length
		int channels = 0;
		if(header.channelMode == Header.CHANNEL_MODE_SINGLE_CHANNEL){
			channels = 1;
		}else{
			channels = 2;
		}
		for(int i=0; i<channels; i++){
			oS.addBit((byte)((sideInfo.ch[i].scfsi[0]) & 0b1));
			oS.addBit((byte)((sideInfo.ch[i].scfsi[1]) & 0b1));
			oS.addBit((byte)((sideInfo.ch[i].scfsi[2]) & 0b1));
			oS.addBit((byte)((sideInfo.ch[i].scfsi[3]) & 0b1));
		}
			
		for(int gr=0; gr<2; gr++){
			for(int ch=0; ch<channels; ch++){
				//12 bit length
				for(int i=0; i<12; i++){
					oS.addBit((byte)((sideInfo.ch[ch].gr[gr].part2_3_length >> (11-i)) & 0b1));
				}
				
				//9 bit length
				for(int i=0; i<9; i++){
					oS.addBit((byte)((sideInfo.ch[ch].gr[gr].big_Values >> (8-i)) & 0b1));
				}
				
				//8 bit length
				for(int i=0; i<8; i++){
					oS.addBit((byte)((sideInfo.ch[ch].gr[gr].global_Gain >> (7-i)) & 0b1));
				}
				
				//4 bit length
				for(int i=0; i<4; i++){
					oS.addBit((byte)((sideInfo.ch[ch].gr[gr].scaleFac_Compress >> (3-i)) & 0b1));
				}
				
				//1 bit length
				oS.addBit((byte)((sideInfo.ch[ch].gr[gr].window_Switching) & 0b1));
				
				if(sideInfo.ch[ch].gr[gr].window_Switching != 0){
					//2 bit length
					for(int i=0; i<2; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].block_type >> (1-i)) & 0b1));
					}
					
					//1 bit length
					oS.addBit((byte)((sideInfo.ch[ch].gr[gr].mixed_blockFlag) & 0b1));
					
					//10 bit length (5x2)
					for(int i=0; i<5; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].table_select[0] >> (4-i)) & 0b1));
					}
					for(int i=0; i<5; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].table_select[1] >> (4-i)) & 0b1));
					}
					
					//9 bits length (3x3)
					for(int i=0; i<3; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].subblock_gain[0] >> (2-i)) & 0b1));
					}
					for(int i=0; i<3; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].subblock_gain[1] >> (2-i)) & 0b1));
					}
					for(int i=0; i<3; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].subblock_gain[2] >> (2-i)) & 0b1));
					}						
				}else{
					
					//15 bit length (5x3)	
					for(int i=0; i<5; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].table_select[0] >> (4-i)) & 0b1));
					}
					for(int i=0; i<5; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].table_select[1] >> (4-i)) & 0b1));
					}
					for(int i=0; i<5; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].table_select[2] >> (4-i)) & 0b1));
					}
					
					//4 bit length
					for(int i=0; i<4; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].region0_count >> (3-i)) & 0b1));
					}
					
					//3 bit length
					for(int i=0; i<3; i++){
						oS.addBit((byte)((sideInfo.ch[ch].gr[gr].region1_count >> (2-i)) & 0b1));
					}
				}
				//1 bit length
				oS.addBit((byte)((sideInfo.ch[ch].gr[gr].preflag) & 0b1));
				
				//1 bit length
				oS.addBit((byte)((sideInfo.ch[ch].gr[gr].scalfac_scale) & 0b1));
				
				//1 bit length
				oS.addBit((byte)((sideInfo.ch[ch].gr[gr].count1table_select) & 0b1));
			}
		}
		
		try{
			mainData.resetStream();
			for(int i=0; i<mainData.getEndPosition(); i++){
				oS.addBit((byte)mainData.getBits(1));
			}
		}catch(BitStreamException e){
			e.printStackTrace();
		}
		return oS;
		
	}
	
}
