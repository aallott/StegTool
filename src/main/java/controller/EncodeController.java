package controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cryptography.Cyptography;
import javazoom.jl.decoder.JavaLayerException;
import manipulation.CoverFileFilter;
import manipulation.MessageTooLargeException;
import manipulation.WorkFile;
import manipulation.image.ImageEncoder;
import manipulation.image.JPEG.JPEGCodec;
import manipulation.sound.AudioEncoder;
import manipulation.sound.AudioPlayer;
import views.EncodeView;
import views.MainFrame;

/**
 * <p>This class handles the back end processing for the encode part of the application.
 * @author Ashley Allott
 */
public class EncodeController implements ActionListener, KeyListener, MouseListener{
	
	private MainFrame mainFrame;
	private EncodeView eView;
	
	private WorkFile workFile;
	
	private BufferedImage image_stego;
	
	AudioPlayer aP_cover;
	AudioPlayer aP_stego;
	
	private Thread encoderThread;
	
	/**
	 * <p>Constructor, creates a new EncodeController instance
	 * @param mainFrame		The MainFrame from which the object was created
	 */
	public EncodeController(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		eView = new EncodeView();
		eView.setBtn_coverBrowseActionListener(this);
		eView.setTxtArea_messageKeyListener(this);
		eView.setBtn_encodeStartActionListener(this);
		eView.setBtn_stegoExportActionListener(this);
		eView.setCmb_bitsPerByteActionListener(this);
		eView.setAudioCoverBtn_PlayActionLisener(this);
		eView.setAudioCoverBtn_PauseActionLisener(this);
		eView.setAudioCoverBtn_StopActionLisener(this);
		eView.setAudioStegoBtn_PlayActionLisener(this);
		eView.setAudioStegoBtn_PauseActionLisener(this);
		eView.setAudioStegoBtn_StopActionLisener(this);
		eView.setImagePreview_coverMouseListener(this);
		eView.setImagePreview_stegoMouseListener(this);
		updateCharCount(false);
	}
	
	/**
	 * <p>Get the encodeView of which the controller manipulates.
	 * @return	EncodeView object the controller interacts with
	 */
	public EncodeView getEncodeView(){
		return eView;
	}

	/**
	 * <p>Handles ActionEvents of the the corresponding EncodeView, providing
	 * button functionality.
	 * 
	 * ActionEvent	passed by the object performing the action 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == eView.getBtn_coverBrowse()){
			getCovertext();
		}else if(e.getSource() == eView.getBtn_encodeStart()){
			if(checkReady(false)){
				encode();
			}
		}else if(e.getSource() == eView.getBtn_stegoExport()){
			saveStegotext();
		}else if(e.getSource() == eView.getCmb_bitsPerByte()){
			checkReady(true);
		}else if(e.getSource() == eView.getAudioCoverBtn_Play()){
			try {
				aP_cover.play();
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource() == eView.getAudioCoverBtn_Pause()){
			aP_cover.pause();
		}else if(e.getSource() == eView.getAudioCoverBtn_Stop()){
			aP_cover.stop();
		}else if(e.getSource() == eView.getAudioStegoBtn_Play()){	
			try {
				aP_stego.play();
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource() == eView.getAudioStegoBtn_Pause()){
			aP_stego.pause();
		}else if(e.getSource() == eView.getAudioStegoBtn_Stop()){
			aP_stego.stop();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
	}
	/**
	 * <p>Handles the key up event of the hidden message text area, which
	 * is used to keep track of the message being entered.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == eView.getTxtArea_message()){
			updateCharCount(false);
			checkReady(false);
		}
	}
	/**
	 * <p>Checks whether or not the application is in the correct state to be 
	 * able to encode the message in the covertext.
	 * 
	 * <p>Based upon the current file type, audio or image, it checks if a covertext 
	 * is present, that a message has been entered and that the message is within 
	 * the size limits to be encoded in the covertext with the selected degradation
	 * setting. 
	 * 
	 * @return	boolean indicating if the application can successfully encode the 
	 * 			message in the covertext.
	 */
	private boolean checkReady(boolean reloadCapacity){
		if(reloadCapacity){
			updateCharCount(true);
		}
		//Check file has been loaded
		if(workFile != null){ 
			if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
				//Image File
				if(!eView.getTxtArea_message().getText().isEmpty()){
					if(getMsgByteCount() <= workFile.fileCapacity){
						eView.getBtn_encodeStart().setEnabled(true);
						eView.setStatus("Ready to encode", EncodeView.STATUS_GOOD);
						return true;
					}else{
						eView.setStatus("Hidden Message is too large", EncodeView.STATUS_BAD);
					}
				}else{
					eView.setStatus("No hidden message input", EncodeView.STATUS_NORMAL);
				}
			}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
				//Audio File
				if(!eView.getTxtArea_message().getText().isEmpty()){
					if(getMsgByteCount() <= workFile.fileCapacity){
						eView.getBtn_encodeStart().setEnabled(true);
						eView.setStatus("Ready to encode", EncodeView.STATUS_GOOD);
						return true;
					}else{
						eView.setStatus("Hidden Message is too large", EncodeView.STATUS_BAD);
					}
				}else{
					eView.setStatus("No hidden message input", EncodeView.STATUS_NORMAL);
				}
			}	
		}else{
			eView.setStatus("No covertext selected", EncodeView.STATUS_NORMAL);
			eView.getBtn_encodeStart().setEnabled(false);
			return false;
		}
		return false;
	}
	/**
	 * <p>Begins the encoding progress, embedding the hidden message in the covertext
	 * to produce a stegotext.
	 * 
	 * <p>The encoder is called from a new Thread as to prevent the program from becoming
	 * non-responsive.
	 * 
	 */
	private void encode(){
		eView.setStegoText(null);
		eView.getBtn_stegoExport().setEnabled(false);
		
		encoderThread = new Thread(new Runnable() {
		     public void run() {
		    	try {
		    		//Display loading bar
		    		LoadingMessage lM = new LoadingMessage("Encoding");
		    		Thread lMT = new Thread(lM);
		    		lMT.start();
		    		if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
		    			//Image File Encoding
		    			ImageEncoder imgEncoder = new ImageEncoder();
		    			if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){	
		    				//JPEG Encoding
	    					workFile.jpegCodec.encode_msg(eView.getTxtArea_message().getText().getBytes(), eView.getPassword(), (short)eView.getCmb_bitsPerByte().getSelectedItem());
	    					workFile.setStegoFileStream(workFile.jpegCodec.genStego());    					
	    					InputStream inputStream = new ByteArrayInputStream(workFile.getStegoFileStream());
	    					image_stego = ImageIO.read(inputStream);
		    			}else{	   
		    				//Non-JPEG image encoding
		    				byte[] encodedData = imgEncoder.encode_msg(workFile, eView.getTxtArea_message().getText().getBytes(),(short)eView.getCmb_bitsPerByte().getSelectedItem(), eView.getPassword());
		    				BufferedImage encodedImage = new BufferedImage(workFile.getCoverBufferedImage().getWidth(), workFile.getCoverBufferedImage().getHeight(), workFile.getCoverBufferedImage().getType());
		    				encodedImage.setData(Raster.createRaster(encodedImage.getSampleModel(), new DataBufferByte(encodedData, encodedData.length), new Point()));
		    				image_stego = encodedImage;		    					
		    			}
		    		}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
		    			//Audio File Encoding
		    			if(workFile.fileFormat == WorkFile.FILE_FORMAT_MP3){
		    				//MP3 Encoding
	 						workFile.mp3Codec.encode_msg(eView.getTxtArea_message().getText().getBytes(), eView.getPassword(), 0);
	 						workFile.setStegoFileStream(workFile.mp3Codec.genStego());
		    			}else{
		    				//Non MP3 audio Encoding
		    				AudioEncoder audioEncoder = new AudioEncoder();
			    			workFile.setStegoFileStream(audioEncoder.encode_msg(workFile, eView.getTxtArea_message().getText().getBytes(), (short)eView.getCmb_bitsPerByte().getSelectedItem(), eView.getPassword()));
		    			}
		    		}
		    		lM.stop();
		 			encodingComplete();
		 		} catch (MessageTooLargeException e1) {
		 			JOptionPane.showMessageDialog(eView,
							"Error occured while encoding selected File.",
							"Encoding Unsuccessful - E1", JOptionPane.ERROR_MESSAGE);
		 			eView.setStatus("Encoding Error", EncodeView.STATUS_BAD);
		 		} catch (Exception e2){
		 			JOptionPane.showMessageDialog(eView,
							"Error occured while encoding selected File.",
							"Encoding Unsuccessful - E2", JOptionPane.ERROR_MESSAGE);
		 			eView.setStatus("Encoding Error", EncodeView.STATUS_BAD);
		 			e2.printStackTrace();
		 		}
		     }
		});  
		encoderThread.start();	
	}
	
	/**
	 * <p>Class controls a animated loading bar for use when loading and encoding
	 */
	private class LoadingMessage implements Runnable{
		private volatile boolean stop;
		private String message;
		public LoadingMessage(String message){
			this.message = message;
		}
		public void start(){
			stop = false;
			this.run();
		}
		public void stop(){
			stop = true;
		}
		@Override
		public void run() {
			try{
				int barState = 0;
				while(!stop){
					if(barState == 0){
						eView.setStatus(message + " /***", EncodeView.STATUS_NORMAL);
						barState = 1;
					}else if(barState == 1){
						eView.setStatus(message + " */**", EncodeView.STATUS_NORMAL);
						barState = 2;
					}else if(barState == 2){
						eView.setStatus(message + " **/*", EncodeView.STATUS_NORMAL);
						barState = 3;
					}else if(barState == 3){
						eView.setStatus(message + " ***/", EncodeView.STATUS_NORMAL);
						barState = 0;
					}else if(barState == 4){
						eView.setStatus(message + " ****", EncodeView.STATUS_NORMAL);
						barState = 0;
					}
					Thread.sleep(400);
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * <p>Called by the encoder thread once the encoding has completed to 
	 * display the completed stegotext.
	 * 
	 * <p>Sets the stego-preview to the new stegotext based upon its type.
	 * The image stegotext is displayed in the corresponding image preview,
	 * while audio stegotext's are added to the clip for playback.
	 */
	private void encodingComplete(){
		eView.getBtn_stegoExport().setEnabled(true);
		eView.setStatus("Encoding Complete", EncodeView.STATUS_GOOD);
		if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
			eView.setStegoText(image_stego);
		}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
			try{
				eView.enableStegoAudioControl();
				if(workFile.fileFormat == WorkFile.FILE_FORMAT_MP3){
						eView.disablePassword();
						aP_stego = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_MP3, workFile, eView.getAudioStegoLbl_Time(), false);
						
					}else{
						eView.enablePassword();
						aP_stego = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_WAV, workFile, eView.getAudioStegoLbl_Time(), false);
						
					}
			} catch (LineUnavailableException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * <p>Gets the covertext from the user via a file browser, and sets the corresponding
	 * previews to enable the user to preview the chosen file (image or audio)
	 */
	private void getCovertext(){
		//EncodeController eC = this;
		Thread loaderThread = new Thread(new Runnable() {
		     public void run() {
		    	String dirName = new File(".").toString();
		    	try{
		    		JFileChooser fileChooser = new JFileChooser();
			 		
			 		fileChooser.setCurrentDirectory(new File(dirName));
			 		
			 		fileChooser.addChoosableFileFilter(new CoverFileFilter());
			 		fileChooser.setAcceptAllFileFilterUsed(false);

			 		if(fileChooser.showOpenDialog(eView) == JFileChooser.APPROVE_OPTION) {
			 			//Display loading bar
			 			LoadingMessage eM = new LoadingMessage("Loading File");
			 			eView.getBtn_coverBrowse().setEnabled(false);
			 			eView.getBtn_encodeStart().setEnabled(false);
			    		Thread eMT = new Thread(eM);
			    		eMT.start();
			 			File file = fileChooser.getSelectedFile();
			 			
			 			//Ensure audio playback has been stopped.
			 			if(aP_cover != null){
			 				aP_cover.stop();
			 			}
			 			if(aP_stego != null){
			 				aP_stego.stop();
			 			}
			 			try {
			 				workFile = new WorkFile(file);
			 				if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
			 					//Image File
			 					aP_cover = null;
			 					aP_stego = null;
			 					if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){
			 						eView.disablePassword();
			 					}else{
			 						eView.enablePassword();
			 					}
			 					eView.setCoverText(workFile.getCoverBufferedImage(), file);
			 					eView.imageFileSelected();
			 				}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
			 					//Audio file
			 					if(workFile.fileFormat == WorkFile.FILE_FORMAT_MP3){
			 						eView.disablePassword();
			 						aP_cover = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_MP3, workFile, eView.getAudioCoverLbl_Time(), true);
			 						
			 					}else{
			 						eView.enablePassword();
			 						aP_cover = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_WAV, workFile, eView.getAudioCoverLbl_Time(), true);
			 						
			 					}
		 						eView.audioFileSelected();
		 						eView.enableCoverAudioControl();
		 						eView.disableStegoAudioControl();
			 					eView.setCoverText(workFile.getCoverAIS(), file);
			 					eView.getAudioCoverLbl_Time();
			 				}
			 				configureBitsPerByte();
			 				checkReady(true);
			 				eView.getBtn_encodeStart().setEnabled(true);
			 				eView.getBtn_coverBrowse().setEnabled(true);
			 			} catch (IOException e) {
			 				JOptionPane.showMessageDialog(eView,
									"Error occured while loading selected File.",
									"Loading Unsuccessful - E1", JOptionPane.ERROR_MESSAGE);
			 				eView.setStatus("Loading Error", EncodeView.STATUS_BAD);
			 			} catch (Exception e) {
			 				JOptionPane.showMessageDialog(eView,
									"Error occured while loading selected File.",
									"Loading Unsuccessful - E2", JOptionPane.ERROR_MESSAGE);
			 				eView.setStatus("Loading Error", EncodeView.STATUS_BAD);
			 			}
			 			eM.stop();
			 			if(workFile.fileStatus == WorkFile.FILE_STATUS_BAD){
				 			eView.setStatus("Selected file cannot be used", EncodeView.STATUS_BAD);
				 		}
			 		}
		    	}catch(Exception e){
		    		eView.getBtn_coverBrowse().setEnabled(true);
		    		e.printStackTrace();
		    	}
		     }
		});  
		loaderThread.start();	
	}
	/**
	 * <p>Populates the degradation drop down with values corresponding to the file type 
	 */
	private void configureBitsPerByte(){
		if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){
			this.eView.getlbl_sld().setText("Image degradation(AC/DC coefficients used for message):");
			short[] items = new short[63 - JPEGCodec.JPEG_AC_EMBED_OFFSET -1];
			for(int i=0; i<items.length; i++){
				items[i] = (short)(i+1);
			}
			this.eView.updateCmb_bitsPerByte(items);
		}else{
			this.eView.getlbl_sld().setText("Image degradation(Message bits per covertext byte):");
			short[] items = {1,2,4,8};
			this.eView.updateCmb_bitsPerByte(items);
		}
	}
	/**
	 * <p>Allows the user to be able to save the generated stegotext via the use
	 * of a file browser.
	 */
	private void saveStegotext(){
		String dirName = new File(".").toString();
		String fileName = "StegotextName";
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File(dirName));
		fileChooser.setSelectedFile(new File(fileName));
		
		
		fileChooser.addChoosableFileFilter(new CoverFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showSaveDialog(eView) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String file_name = workFile.checkFileExtension(file.toString());
			file = new File(file_name);
			try {
				if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
					outputStegoImage(file);
				}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
					outputStegoAudio(file);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(eView,
						"Error occured while saving selected File.",
						"Saving Unsuccessful - E1", JOptionPane.ERROR_MESSAGE);
 				eView.setStatus("Saving Error", EncodeView.STATUS_BAD);
			}
			JOptionPane.showMessageDialog(eView, "Saved Successfully", "Saved", JOptionPane.PLAIN_MESSAGE);
		}	
	}
	/**
	 * <p>Writes the image file to disc
	 * @param file			image stegotext to be output
	 * @throws IOException	thrown by ImageIO
	 */
	private void outputStegoImage(File file) throws IOException{
		System.out.println("Saving : " + file.getName());
		if(file.getName().toLowerCase().endsWith(".png")){
			ImageIO.write(image_stego, "png", file);
		}else if(file.getName().toLowerCase().endsWith(".bmp")){
			ImageIO.write(image_stego, "bmp", file);
		}else if(file.getName().toLowerCase().endsWith(".jpeg")){
			FileOutputStream stream = new FileOutputStream(file);
			try {
			    stream.write(workFile.getStegoFileStream());
			} finally {
			    stream.close();
			}
			//ImageIO.write(image_stego, "jpeg", file);
		}else if(file.getName().toLowerCase().endsWith(".jpg")){
			FileOutputStream stream = new FileOutputStream(file);
			try {
			    stream.write(workFile.getStegoFileStream());
			} finally {
			    stream.close();
			}
			//ImageIO.write(image_stego, "jpg", file);
		}
	}
	/**
	 * <p>Writes the audio file to dics
	 * @param file			audio stegotext to be output
	 * @throws IOException	thrown by AudioSystem
	 */
	private void outputStegoAudio(File file) throws IOException{
		if(file.getName().toLowerCase().endsWith(".wav")){
			AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
			if (AudioSystem.isFileTypeSupported(fileType, workFile.getStegoAIS())) {
				AudioSystem.write(workFile.getStegoAIS(), AudioFileFormat.Type.WAVE, file);
			}else{
				System.out.println("File type not supported");
			}
		}else if(file.getName().toLowerCase().endsWith(".mp3")){
			FileOutputStream stream = new FileOutputStream(file);
			try {
			    stream.write(workFile.getStegoFileStream());
			} finally {
			    stream.close();
			}
		}
	}
	/**
	 * <p>Gets the contents of the hidden message textarea.
	 * @return	String of the textarea contents
	 */
	private String getMsgString(){
		return eView.getTxtArea_message().getText();
	}
	/**
	 * <p>Gets the amount of bytes the hidden message requires.
	 * @return Integer count of the bytes required
	 */
	private int getMsgByteCount(){
		return getMsgString().length();
	}
	/**
	 * <p>Gets the character count of the hidden message
	 * @return	Integer count of the characters in the hidden message
	 * 			textarea
	 */
	private int getMsgCharCount(){
		return getMsgString().getBytes().length;
	}
	/**
	 * <p>Updates the label which informs the user about the hidden message
	 * size (current size, max size allowed, and is it too large).
	 */
	private void updateCharCount(boolean updateCapacity){
		long remainingChar =-1;
		if(workFile != null){
			if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
				if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){
					if(updateCapacity){
						workFile.fileEBPB = (short)eView.getCmb_bitsPerByte().getSelectedItem();
						workFile.updateEmbeddingCapactiy();
					}
					remainingChar = Long.max(workFile.fileCapacity - getMsgByteCount(), 0);
				}else{
					if(updateCapacity){
						workFile.fileEBPB = (short)eView.getCmb_bitsPerByte().getSelectedItem();
						workFile.updateEmbeddingCapactiy();
					}
					int cipherMessageByteCount = (int)((int)Math.ceil((double)getMsgString().length() / (double)Cyptography.CIPHER_BLOCK_SIZE) * Cyptography.CIPHER_BLOCK_SIZE);
					remainingChar = Long.max(workFile.fileCapacity - cipherMessageByteCount, 0);
				}
			}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
				if(workFile.fileType == WorkFile.FILE_FORMAT_MP3){
					if(updateCapacity){
						workFile.fileEBPB = (short)eView.getCmb_bitsPerByte().getSelectedItem();
						workFile.updateEmbeddingCapactiy();
					}
				}else{
					if(updateCapacity){
						workFile.fileEBPB = (short)eView.getCmb_bitsPerByte().getSelectedItem();
						workFile.updateEmbeddingCapactiy();
					}
					int cipherMessageByteCount = (int)((int)Math.ceil((double)getMsgString().length() / (double)Cyptography.CIPHER_BLOCK_SIZE) * Cyptography.CIPHER_BLOCK_SIZE);
					remainingChar = Long.max(workFile.fileCapacity - cipherMessageByteCount, 0);
				}
			}
		}
		remainingChar = Long.max(remainingChar, 0);
		eView.setLbl_charCountText("Character Count: " + getMsgCharCount() + " | Remaining Characters: " + remainingChar);
	}
	/**
	 * <p>Sets the status label text, formating the passed percentage value
	 * into a sting.
	 * @param percent	percentage indicating the amount of encoding complete
	 */
	public void encodeProgress(int percent){
		eView.setStatus("Encoding[" + percent + "%]", EncodeView.STATUS_GOOD);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == eView.getImagePreview_cover()){
			eView.getImagePreview_cover().openFullScreenImage();
		}else if(e.getSource() == eView.getImagePreview_stego()){
			eView.getImagePreview_stego().openFullScreenImage();
		}
		
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
