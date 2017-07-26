package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.crypto.BadPaddingException;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javazoom.jl.decoder.JavaLayerException;
import manipulation.CoverFileFilter;
import manipulation.WorkFile;
import manipulation.image.ImageEncoder;
import manipulation.sound.AudioEncoder;
import manipulation.sound.AudioPlayer;
import views.DecodeView;
import views.EncodeView;
import views.MainFrame;

/**
 * <p>This class handles the back end processing for the decode part of the application.
 * @author Ashley Allott
 */
public class DecodeController implements ActionListener, KeyListener, MouseListener{
	
	private static final int FILE_TYPE_IMAGE = 0;
	private static final int FILE_TYPE_AUDIO = 1;
	private static final int FILE_TYPE_UNKOWN = 2;
	
	private MainFrame mainFrame;
	private DecodeView dView;
	
	private static int CURRENT_FILE_TYPE;
	
	private WorkFile workFile;
	
	private AudioPlayer aP_stego;
	
	private String str_msg;
	
	private Thread DecoderThread;
	
	/**
	 * <p>Constructor, creates a new EncodeController instance
	 * @param mainFrame		The MainFrame from which the object was created
	 */
	public DecodeController(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		dView = new DecodeView();
		dView.setBtn_stegoBrowseActionListener(this);
		dView.setBtn_decodeStartActionListener(this);
		dView.setBtn_msgCopyActionListener(this);
		dView.setAudioStegoBtn_PlayActionLisener(this);
		dView.setAudioStegoBtn_PauseActionLisener(this);
		dView.setAudioStegoBtn_StopActionLisener(this);
		dView.setBtn_msgSaveActionLisener(this);
		dView.setImagePreview_stegoMouseListener(this);
		checkReady();
	}
	
	/**
	 * <p>Get the decodeView of which the controller manipulates.
	 * @return	DecodeView object the controller interacts with
	 */
	public DecodeView getDecodeView(){
		return dView;
	}
	/**
	 * <p>Checks whether or not the application is in the correct state to be 
	 * able to decode stegotext. Checking if a stegotext has been provided.
	 * 
	 * @return	boolean indicating if the application can successfully encode the 
	 * 			message in the covertext.
	 */
	private boolean checkReady(){
		if(CURRENT_FILE_TYPE == FILE_TYPE_IMAGE){
			if(workFile != null){
				dView.getBtn_DecodeStart().setEnabled(true);
				dView.setStatus("Ready to decode", EncodeView.STATUS_GOOD);
				dView.getTxtArea_message().setText("");
				dView.getBtn_msgCopy().setEnabled(false);
				return true;
			}else{
				dView.setStatus("Awaiting Stegotext", EncodeView.STATUS_NORMAL);
			}
			dView.getBtn_DecodeStart().setEnabled(false);
			return false;
		}else if(CURRENT_FILE_TYPE == FILE_TYPE_AUDIO){
			if(workFile != null){
				dView.getBtn_DecodeStart().setEnabled(true);
				dView.setStatus("Ready to decode", EncodeView.STATUS_GOOD);
				dView.getTxtArea_message().setText("");
				dView.getBtn_msgCopy().setEnabled(false);
				return true;
			}else{
				dView.setStatus("Awaiting Stegotext", EncodeView.STATUS_NORMAL);
			}
			dView.getBtn_DecodeStart().setEnabled(false);
		}
		return false;
	}
	/**
	 * <p>Gets the stegotext from the user via a file browser, and sets the corresponding
	 * previews to enable the user to preview the chosen file (image or audio)
	 */
	private void getStegotext(){
		Thread loaderThread = new Thread(new Runnable() {
			public void run() {
				LoadingMessage lM = new LoadingMessage("Loading File");
	 			dView.getBtn_stegoBrowse().setEnabled(false);
	 			dView.getTxtArea_message().setText("");
	 			dView.getBtn_msgCopy().setEnabled(false);
	 			dView.getBtn_msgSave().setEnabled(false);
	 			dView.getBtn_DecodeStart().setEnabled(false);
	 			
	    		Thread lMT = new Thread(lM);
	    		lMT.start();
	    		
				String dirName = new File(".").toString();
				JFileChooser fileChooser = new JFileChooser();
				
				fileChooser.setCurrentDirectory(new File(dirName));
				
				fileChooser.addChoosableFileFilter(new CoverFileFilter());
				fileChooser.setAcceptAllFileFilterUsed(false);

				if (fileChooser.showOpenDialog(dView) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						workFile = new WorkFile(file);
						if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
							if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){
		 						dView.disablePassword();
		 					}else{
		 						dView.enablePassword();
		 					}
							//image selected
							dView.setStegoText(workFile.getCoverBufferedImage(), file);
							dView.imageFileSelected();
						}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
		 					if(workFile.fileFormat == WorkFile.FILE_FORMAT_MP3){
		 						aP_stego = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_MP3, workFile, dView.getAudioStegoLbl_Time(), true);
		 						dView.disablePassword();
		 					}else{
		 						aP_stego = new AudioPlayer(AudioPlayer.AUDIO_PLAYER_WAV, workFile, dView.getAudioStegoLbl_Time(), true);
		 						dView.enablePassword();
		 					}
	 						dView.audioFileSelected();
		 					dView.setStegoText(workFile.getCoverAIS(), file);
						}
						checkReady();
						dView.getBtn_stegoBrowse().setEnabled(true);
						dView.getBtn_DecodeStart().setEnabled(true);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Error occured while loading selected File");
						e.printStackTrace();
					} catch (LineUnavailableException e) {
						JOptionPane.showMessageDialog(null, "Error occured while loading selected File");
						e.printStackTrace();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error occured while loading selected File");
						e.printStackTrace();
					}
				}
				lM.stop();
				dView.getBtn_stegoBrowse().setEnabled(true);
			}
		});
		loaderThread.start();	
	}
	/**
	 * <p>Allows the user to be able to save the generated stegotext via the use
	 * of a file browser.
	 */
	private void saveMsgFile(){
		String dirName = new File(".").toString();
		String fileName = "MessageContents";
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File(dirName));
		fileChooser.setSelectedFile(new File(fileName));
		
		
		fileChooser.addChoosableFileFilter(new CoverFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showSaveDialog(dView) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String file_name = checkTextFileExtension(file.toString());
			file = new File(file_name);
			try {
				if(CURRENT_FILE_TYPE == FILE_TYPE_IMAGE){
					outputTextFile(file);
				}else if(CURRENT_FILE_TYPE == FILE_TYPE_AUDIO){
					outputTextFile(file);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error occured while saving selected File");
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Saved Successfully", "Saved", JOptionPane.PLAIN_MESSAGE);
		}	
	}
	/**
	 * <p>Checks the given filename to see if it has the correct file suffix
	 * for a text file, if not adds the correct suffix.
	 * 
	 * @param fileName	filename to be checked
	 * @return			checked filename, with the correct suffix
	 */
	private String checkTextFileExtension(String fileName){
		if(fileName.toLowerCase().endsWith("txt")){
			return fileName;
		}else{
			return fileName += ".txt";
		}
	}
	/**
	 * <p>Writes the message contents file to disc
	 * @param file			text file to write the message to
	 * @throws IOException	thrown by BufferedWriter
	 */
	private void outputTextFile(File file) throws IOException{
		if(file.getName().toLowerCase().endsWith(".txt")){
			BufferedReader bufferedReader = new BufferedReader(new StringReader(str_msg));
			PrintWriter printWriter = new PrintWriter(new FileWriter(file));
			bufferedReader.lines().forEach(line -> printWriter.println(line));
			printWriter.close();
			bufferedReader.close();
		}
	}
	/**
	 * <p>Begins the decoding progress, recovering the hidden message from the given 
	 * stegotext, if it is present.
	 * 
	 * <p>The decoder is called from a new Thread as to prevent the program from becoming
	 * non-responsive.
	 * 
	 */
	private void decode(){		
		DecoderThread = new Thread(new Runnable() {
			public void run() {
				LoadingMessage lM = new LoadingMessage("Decoding");
	    		Thread lMT = new Thread(lM);
	    		lMT.start();
				try {
					if (workFile.fileType == WorkFile.FILE_TYPE_IMAGE) {
						ImageEncoder imgEncoder = new ImageEncoder();
						if (workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG) {
							str_msg = new String(workFile.jpegCodec.decode_msg(dView.getPassword()));
						} else {
							str_msg = new String(imgEncoder.decode_msg(workFile, dView.getPassword()));
						}
					} else if (workFile.fileType == WorkFile.FILE_TYPE_AUDIO) {
						if(workFile.fileFormat == WorkFile.FILE_FORMAT_MP3){
	 						str_msg = new String(workFile.mp3Codec.decode_msg(dView.getPassword()));
		    			}else{
		    				AudioEncoder audioEncoder = new AudioEncoder();
							str_msg = new String(audioEncoder.decode_msg(workFile, dView.getPassword()));
		    			}
					}
					lM.stop();
					decodingComplete();
				}catch (BadPaddingException e1) {
					JOptionPane.showMessageDialog(dView,
							"Error occured with decoding - the supplied audio is of the incorrect format, or the supplied password key is not correct.",
							"Deocding Unsuccessful - E1", JOptionPane.ERROR_MESSAGE);
					dView.setStatus("Decoding Error", EncodeView.STATUS_BAD);
				} catch (NullPointerException e2) {
					JOptionPane.showMessageDialog(dView,
							"Error occured with decoding - the supplied audio is of the incorrect format, or the supplied password key is not correct.",
							"Deocding Unsuccessful - E2", JOptionPane.ERROR_MESSAGE);
					dView.setStatus("Decoding Error", EncodeView.STATUS_BAD);
				} catch (ArithmeticException e3) {
					JOptionPane.showMessageDialog(dView,
							"Error occured with decoding - the supplied audio is of the incorrect format, or the supplied password key is not correct.",
							"Deocding Unsuccessful - E3", JOptionPane.ERROR_MESSAGE);
					dView.setStatus("Decoding Error", EncodeView.STATUS_BAD);
					e3.printStackTrace();
				}catch (Exception e4) {
					JOptionPane.showMessageDialog(dView,
							"Error occured with decoding - the supplied audio is of the incorrect format, or the supplied password key is not correct.",
							"Deocding Unsuccessful - E4", JOptionPane.ERROR_MESSAGE);
					dView.setStatus("Decoding Error", EncodeView.STATUS_BAD);
				}catch (Error e5){
					JOptionPane.showMessageDialog(dView,
							"Error occured with decoding - the supplied audio is of the incorrect format, or the supplied password key is not correct.",
							"Deocding Unsuccessful - E4", JOptionPane.ERROR_MESSAGE);
					dView.setStatus("Decoding Error", EncodeView.STATUS_BAD);
				}
				lM.stop();
			}
		});
		DecoderThread.start();	
	}
	
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
						dView.setStatus(message + " /***", EncodeView.STATUS_NORMAL);
						barState = 1;
					}else if(barState == 1){
						dView.setStatus(message + " */**", EncodeView.STATUS_NORMAL);
						barState = 2;
					}else if(barState == 2){
						dView.setStatus(message + " **/*", EncodeView.STATUS_NORMAL);
						barState = 3;
					}else if(barState == 3){
						dView.setStatus(message + " ***/", EncodeView.STATUS_NORMAL);
						barState = 0;
					}else if(barState == 4){
						dView.setStatus(message + " ****", EncodeView.STATUS_NORMAL);
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
	 * <p>Called by the decoder thread once the decoding has complete.
	 * 
	 * <p>Sets hidden message textarea's contents to the recovered message.
	 */
	private void decodingComplete(){
		dView.setStatus("Decoding Complete", EncodeView.STATUS_GOOD);
		dView.setMessageText(str_msg);
		dView.getBtn_msgCopy().setEnabled(true);
		dView.getBtn_msgSave().setEnabled(true);
	}
	/**
	 * <p>Sets the status label text, formating the passed percentage value
	 * into a sting.
	 * @param percent	percentage indicating the progress of the decoder
	 */
	public void decodeProgress(int percent){
		dView.setStatus("Status: Decoding[" + percent + "%]", EncodeView.STATUS_GOOD);
	}
	/**
	 * Copies the contents of the hidden message textarea to the systems clipboard.
	 */
	private void copyMessageToClipboard(){
		StringSelection selection = new StringSelection(dView.getTxtArea_message().getText());
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {	
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	/**
	 * <p>Handles ActionEvents of the the corresponding DecodeView, providing
	 * button functionality.
	 * 
	 * ActionEvent	passed by the object performing the action 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == dView.getBtn_stegoBrowse()){
			getStegotext();
		}else if(e.getSource() == dView.getBtn_DecodeStart()){
			decode();
		}else if(e.getSource() == dView.getBtn_msgCopy()){
			copyMessageToClipboard();
		}else if(e.getSource() == dView.getAudioStegoBtn_Play()){
			try {
				aP_stego.play();
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource() == dView.getAudioStegoBtn_Pause()){
			aP_stego.pause();
		}else if(e.getSource() == dView.getAudioStegoBtn_Stop()){
			aP_stego.stop();
		}else if(e.getSource() == dView.getBtn_msgSave()){
			if(!str_msg.equals("")){
				saveMsgFile();
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == dView.getImagePreview_stego()){
			dView.getImagePreview_stego().openFullScreenImage();
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
