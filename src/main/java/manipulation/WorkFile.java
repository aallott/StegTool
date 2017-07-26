package manipulation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import manipulation.image.ImageEncoder;
import manipulation.image.JPEG.JPEGCodec;
import manipulation.sound.AudioEncoder;
import manipulation.sound.MP3.MP3Codec;

/**
 * <p>This class encapsulates the covertext and stegotext files and their associated functions
 * @author Ashley Allott
 */
public class WorkFile {
	
	public static final int FILE_TYPE_UNKNOWN = 0;
	public static final int FILE_TYPE_IMAGE = 1;
	public static final int FILE_TYPE_AUDIO = 2;
	
	public static final int FILE_FORMAT_UNKNOWN = 10;
	public static final int FILE_FORMAT_PNG = 11;
	public static final int FILE_FORMAT_BMP = 12;
	public static final int FILE_FORMAT_JPEG = 13;
	public static final int FILE_FORMAT_WAV = 14;
	public static final int FILE_FORMAT_MP3 = 15;
	
	public static final int FILE_STATUS_OK = 0;
	public static final int FILE_STATUS_BAD = 1;
	
	public File file;
	
	public File tempFile;
	
	public int fileType;
	public int fileFormat;
	public long fileCapacity;
	public short fileEBPB;
	public int fileStatus;
	
	private byte[] coverFileStream = null;
	private byte[] stegoFileStream = null;
	
	public JPEGCodec jpegCodec;
	public MP3Codec mp3Codec;
	
	/**
	 * <p>Constructor, creates a new WorkFile instance
	 * 
	 * <p>Initiates the file, setting the type, format, capacity and byte arrays.
	 * 
	 * @param file 	the file of which the workfile uses
	 * @throws Exception 
	 */
	public WorkFile(File file) throws Exception{
		this.file = file;
		this.fileType = getFileType(file);
		this.fileFormat = getFileFormat(file);	
		this.fileCapacity = 0;
		this.fileEBPB = 1;
		
		coverFileStream = new byte[(int)file.length()];
		try{
			FileInputStream fis = new FileInputStream(file);
			fis.read(coverFileStream);
			fis.close();
			this.fileCapacity = ImageEncoder.getMaxMessageSize(this, fileEBPB);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fileStatus = WorkFile.FILE_STATUS_BAD;
		}catch(IOException e){
			e.printStackTrace();
			fileStatus = WorkFile.FILE_STATUS_BAD;
		}catch(Exception e){
			e.printStackTrace();
			fileStatus = WorkFile.FILE_STATUS_BAD;
		}
		if(fileFormat == WorkFile.FILE_FORMAT_JPEG){
			jpegCodec = new JPEGCodec();
			jpegCodec.decodeStream(getCoverFileStream());
		}else if(fileFormat == WorkFile.FILE_FORMAT_MP3){
			mp3Codec = new MP3Codec();
			mp3Codec.decodeStream(getCoverFileStream());
		}
		updateEmbeddingCapactiy();
		fileStatus = WorkFile.FILE_STATUS_OK;
	}
	
	/**
	 * <p>Identifies what the type of the file is (audio or image)
	 * 
	 * @param file	the file to check the type of
	 * @return		integer value representing the file type
	 */
	public int getFileType(File file){
		if(file.getName().toLowerCase().endsWith(".png")){
			return FILE_TYPE_IMAGE;
		}else if(file.getName().toLowerCase().endsWith(".bmp")){
			return FILE_TYPE_IMAGE;
		}else if(file.getName().toLowerCase().endsWith(".jpeg")){
			return FILE_TYPE_IMAGE;
		}else if(file.getName().toLowerCase().endsWith(".jpg")){
			return FILE_TYPE_IMAGE;
		}else if(file.getName().toLowerCase().endsWith(".wav")){
			return FILE_TYPE_AUDIO;
		}else if(file.getName().toLowerCase().endsWith(".mp3")){
			return FILE_TYPE_AUDIO;
		}else{
			return FILE_TYPE_UNKNOWN;
		}
	}
	
	/**
	 * <p>Identifies what the format of the file is.
	 * 
	 * @param file	the file to check the type of
	 * @return		integer value representing the file format
	 */
	public int getFileFormat(File file){
		if(file.getName().toLowerCase().endsWith(".png")){
			return FILE_FORMAT_PNG;
		}else if(file.getName().toLowerCase().endsWith(".bmp")){
			return FILE_FORMAT_BMP;
		}else if(file.getName().toLowerCase().endsWith(".jpeg")){
			return FILE_FORMAT_JPEG;
		}else if(file.getName().toLowerCase().endsWith(".jpg")){
			return FILE_FORMAT_JPEG;
		}else if(file.getName().toLowerCase().endsWith(".wav")){
			return FILE_FORMAT_WAV;
		}else if(file.getName().toLowerCase().endsWith(".mp3")){
			return FILE_FORMAT_MP3;
		}else{
			return FILE_TYPE_UNKNOWN;
		}
	}
	
	/**
	 * <p>Checks the give filename to see if it has the correct file suffix
	 * for the stegotext output, if not - it adds the correct suffix to the
	 * filename.
	 * 
	 * @param fileName	filename to be checked
	 * @return			checked filename, with the correct suffix
	 */
	public String checkFileExtension(String fileName){
		if(file.getName().toLowerCase().endsWith(".png")){
			if(fileName.toLowerCase().endsWith(".png")){
				return fileName;
			}else{
				return fileName += ".png";
			}
		}else if(file.getName().toLowerCase().endsWith(".bmp")){
			if(fileName.toLowerCase().endsWith(".bmp")){
				return fileName;
			}else{
				return fileName += ".bmp";
			}
		}else if(file.getName().toLowerCase().endsWith(".wav")){
			if(fileName.toLowerCase().endsWith(".wav")){
				return fileName;
			}else{
				return fileName += ".wav";
			}
		}else if(file.getName().toLowerCase().endsWith(".mp3")){
			if(fileName.toLowerCase().endsWith(".mp3")){
				return fileName;
			}else{
				return fileName += ".mp3";
			}
		}else if(file.getName().toLowerCase().endsWith(".jpeg")){
			if(fileName.toLowerCase().endsWith(".jpeg")){
				return fileName;
			}else{
				return fileName += ".jpeg";
			}
		}else if(file.getName().toLowerCase().endsWith(".jpg")){
			if(fileName.toLowerCase().endsWith(".jpg")){
				return fileName;
			}else{
				return fileName += ".jpg";
			}
		}else{
			return fileName;
		}
	}
	
	/**
	 * <p>Gets the cover file byte stream
	 * 
	 * @return	byte array representing the covertext
	 */
	public byte[] getCoverFileStream(){
		return this.coverFileStream;
	}
	
	/**
	 * <p>Gets the stego file byte stream
	 * 
	 * @return	byte array representing the stegotext
	 */
	public byte[] getStegoFileStream(){
		return this.stegoFileStream;
	}
	
	/**
	 * <p>Sets the stegotext file byte stream
	 * 
	 * @param stegoFileStream	the byte array containing the stegotext
	 */
	public void setStegoFileStream(byte[] stegoFileStream){
		this.stegoFileStream = stegoFileStream;
	}
	
	/**
	 * <p>Saves the current file to a temp directory and returns the created file.
	 * 
	 * @return				the created File
	 * @throws IOException
	 */
	public File getTempStegoFile() throws IOException{
		
		Path path = Paths.get(this.checkFileExtension("./temp/stego"));
		Files.deleteIfExists(path);
        Files.createDirectories(path.getParent());
		
		boolean checkFile = checkFile(path.toFile());
		if(!checkFile){
			//File cannot be read, use another name
			path = Paths.get(this.checkFileExtension("./temp/stego1"));
		}
		
		
        tempFile = path.toFile();
        
		if(fileFormat == WorkFile.FILE_FORMAT_WAV){
			AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
			if (AudioSystem.isFileTypeSupported(fileType, this.getStegoAIS())) {
				AudioSystem.write(this.getStegoAIS(), AudioFileFormat.Type.WAVE, tempFile);
			}else{
				System.out.println("File type not supported");
			}
		}else if(fileFormat == WorkFile.FILE_FORMAT_MP3){
			FileOutputStream stream = new FileOutputStream(tempFile);
			try {
			    stream.write(this.getStegoFileStream());
			} finally {
			    stream.close();
			}
		}
		
		return tempFile;
	}
	
	/**
	 * <p>Check to see if the supplied file is readable.
	 * 
	 * @param file	the file to check
	 * @return		boolean value indicating if the file can be read.
	 */
	public boolean checkFile(File file){
	    if(!file.exists()){
	    	return false;
	    }
	    if (!file.canRead()){
	    	return false;
	    }
	    
	    try{
	        FileReader fileReader = new FileReader(file.getAbsolutePath());
	        fileReader.read();
	        fileReader.close();
	    }catch(Exception e){
	        return false;
	    }
	    return true;
	}
	
	/**
	 * 
	 * <p>Gets the file extension of the workFile
	 * @return	the string file extension
	 */
	public String getFileExtension(){
		int i = file.getName().toLowerCase().lastIndexOf('.');
		if(i > 0){
		    return file.getName().toLowerCase().substring(i+1);
		}else{
			return "";
		}
	}
	
	/**
	 * <p>Gets the BufferedImage object of the workfile cover (only is the file is of an image format)
	 * 
	 * @return	the BufferedImage object
	 * @throws IOException
	 */
	public BufferedImage getCoverBufferedImage() throws IOException{
		if(fileType == FILE_TYPE_IMAGE){
			BufferedImage bufferedImage = null;
			bufferedImage = ImageIO.read(file);
			return bufferedImage;
		}else{
			return null;
		}
	}
	
	/**
	 * <p>Gets the Covertext AudioInputStream object of the workfile (only is the file is of an audio format)
	 * 
	 * @return	the AudioInputStream object
	 * @throws IOException
	 */
	public AudioInputStream getCoverAIS() throws IOException{
		if(fileType == FILE_TYPE_AUDIO){
			if(fileFormat == FILE_FORMAT_MP3){
				return null;
			}else{
				try {
					AudioInputStream audioInputStream = null;
					audioInputStream = AudioSystem.getAudioInputStream(file);
						
					AudioInputStream audio = audioInputStream;
						
					int frameSize = audio.getFormat().getFrameSize();
					int frameLength = (int)audio.getFrameLength();
					byte[] audioBytes = new byte[(int) frameLength*frameSize];
						
					audioInputStream.read(audioBytes);
						
					AudioInputStream newAudio = new AudioInputStream(new ByteArrayInputStream(audioBytes), audio.getFormat(), audio.getFrameLength());
					audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes), audio.getFormat(), audio.getFrameLength());
						
					return newAudio;
				}catch(IOException e) {
					e.printStackTrace();
					return null;
				}catch(UnsupportedAudioFileException e) {
					e.printStackTrace();
				}
			}
		}else{
			return null;
		}
		return null;
	}
	
	/**
	 * <p>Gets the Stegotext AudioInputStream object of the workfile (only is the file is of an audio format)
	 * 
	 * @return	the AudioInputStream object
	 * @throws IOException
	 */
	public AudioInputStream getStegoAIS() throws IOException{
		if(fileType == FILE_TYPE_AUDIO){
			try {
				AudioInputStream audioInputStream = null;
				audioInputStream = AudioSystem.getAudioInputStream(file);
					
				AudioInputStream audio = audioInputStream;
					
				int frameSize = audio.getFormat().getFrameSize();
				int frameLength = (int)audio.getFrameLength();
				byte[] audioBytes = new byte[(int) frameLength*frameSize];
					
				audioInputStream.read(audioBytes);
					
				AudioInputStream newAudio = new AudioInputStream(new ByteArrayInputStream(stegoFileStream), getCoverAIS().getFormat(), getCoverAIS().getFrameLength());
				audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBytes), audio.getFormat(), audio.getFrameLength());
					
				return newAudio;
			}catch(IOException e) {
				e.printStackTrace();
				return null;
			}catch(UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
		}else{
			return null;
		}
		return null;
	}
	
	/**
	 * <p>Update the embedding capacity specified for the file
	 */
	public void updateEmbeddingCapactiy(){
		if(this.fileType == FILE_TYPE_IMAGE){
			try {
				if(this.fileFormat == WorkFile.FILE_FORMAT_JPEG){
					this.fileCapacity = jpegCodec.embedCapacity(fileEBPB);
				}else{
					this.fileCapacity = ImageEncoder.getMaxMessageSize(this, fileEBPB);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileStatus = WorkFile.FILE_STATUS_BAD;
			}
		}else if(this.fileType == FILE_TYPE_AUDIO){
			if(this.fileFormat == WorkFile.FILE_FORMAT_MP3){
				this.fileCapacity = mp3Codec.getCapacity();
			}else{
				this.fileCapacity = AudioEncoder.getMaxMessageSize(this, fileEBPB);
			}
		}
	}
}
