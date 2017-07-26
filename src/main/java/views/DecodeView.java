package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * <p>This class is used for creating a view for Decoding.
 * @author Ashley Allott
 */
public class DecodeView extends JPanel{

	private static final long serialVersionUID = 1L;
	
	public static Color STATUS_NORMAL = Color.BLACK;
	public static Color STATUS_GOOD = Color.GREEN;
	public static Color STATUS_BAD = Color.RED;
	public static Color STATUS_MEDIUM = Color.ORANGE;
	
	private static int MAX_WIDTH = 800;
	private static int MAX_HEIGHT = 800;
	private static int MIN_WIDTH = 400;
	private static int MIN_HEIGHT = 400;
	private static int WIDTH = 800;
	private static int HEIGHT = 800;
	private static int STATUS_BAR_HEIGHT = 16;
	
	private AudioPreview audioPreview_stego;
	
	private JPanel stegoBrowser;
	private ImagePreview imagePreview_stego;
	private JButton btn_stegoBrowse;
	private JTextField txtFld_stegoPath;
	
	private JPanel messageOutput;
	private JTextArea txtArea_message;
	private JButton btn_msgCopy;
	private JButton btn_msgSave;
	
	private JPanel decodeControl;
	private JPasswordField passFld_passwordKey;
	private JButton btn_decodeStart;
	
	private JPanel statusBar;
	private JLabel lbl_statusBar;
	
	public DecodeView(){
		super(true);
		SpringLayout sL = new SpringLayout();
		this.setLayout(sL);
		this.add(stegoBrowser());
		this.add(messageOutput());
		this.add(decodeControl());
		this.add(statusBar());
		
		sL.putConstraint(SpringLayout.NORTH, stegoBrowser, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, stegoBrowser, 0, SpringLayout.WEST, this);
		
		sL.putConstraint(SpringLayout.NORTH, decodeControl, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, decodeControl, 0, SpringLayout.EAST, stegoBrowser);
		sL.putConstraint(SpringLayout.EAST, decodeControl, 0, SpringLayout.EAST, this);
		
		sL.putConstraint(SpringLayout.NORTH, messageOutput, 0, SpringLayout.SOUTH, stegoBrowser);
		sL.putConstraint(SpringLayout.WEST, messageOutput, 0, SpringLayout.WEST, this);
		sL.putConstraint(SpringLayout.EAST, messageOutput, 0, SpringLayout.EAST, this);
		
		
		sL.putConstraint(SpringLayout.EAST, statusBar, 0, SpringLayout.EAST, this);
		sL.putConstraint(SpringLayout.WEST, statusBar, 0, SpringLayout.WEST, this);
		sL.putConstraint(SpringLayout.SOUTH, statusBar, 0, SpringLayout.SOUTH, this);
		
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT + STATUS_BAR_HEIGHT));
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	}
	public void resise(){
		int width = Math.max(this.getWidth(), MainFrame.WIDTH);
		int height = Math.max(this.getHeight(), MainFrame.HEIGHT)-MainFrame.STATUS_BAR_HEIGHT;
		if(this.getHeight() > MainFrame.HEIGHT){
			stegoBrowser.setPreferredSize(new Dimension(width/2, height/2));
			decodeControl.setPreferredSize(new Dimension(width/2, height/2));
			messageOutput.setPreferredSize(new Dimension(width, height/2));
		}else{
			stegoBrowser.setPreferredSize(new Dimension(width/2, height/4));
			decodeControl.setPreferredSize(new Dimension(width/2, height/4));
			messageOutput.setPreferredSize(new Dimension(width, height/2));
		}
	}
	private JPanel stegoBrowser(){
		stegoBrowser= new JPanel();
		stegoBrowser.setBorder(BorderFactory.createTitledBorder("Select Stegotext (image with hidden message):"));
		SpringLayout sL = new SpringLayout();
		stegoBrowser.setLayout(sL);
		
		txtFld_stegoPath = new JTextField();
		txtFld_stegoPath.setColumns(25);
		txtFld_stegoPath.setEditable(false);
		stegoBrowser.add(txtFld_stegoPath);
				
		btn_stegoBrowse = new JButton("Browse");
		btn_stegoBrowse.setName("btn_stegoBrowse");
		stegoBrowser.add(btn_stegoBrowse);
		
		imagePreview_stego = new ImagePreview("Covertext:");
		audioPreview_stego = new AudioPreview();
		
		sL.putConstraint(SpringLayout.NORTH, imagePreview_stego, 10, SpringLayout.SOUTH, txtFld_stegoPath);
		sL.putConstraint(SpringLayout.EAST, imagePreview_stego, -10, SpringLayout.EAST, stegoBrowser);
		sL.putConstraint(SpringLayout.WEST, imagePreview_stego, 10, SpringLayout.WEST, stegoBrowser);
		sL.putConstraint(SpringLayout.SOUTH, imagePreview_stego, -10, SpringLayout.SOUTH, stegoBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, audioPreview_stego, 10, SpringLayout.SOUTH, txtFld_stegoPath);
		sL.putConstraint(SpringLayout.EAST, audioPreview_stego, -10, SpringLayout.EAST, stegoBrowser);
		sL.putConstraint(SpringLayout.WEST, audioPreview_stego, 10, SpringLayout.WEST, stegoBrowser);
		sL.putConstraint(SpringLayout.SOUTH, audioPreview_stego, -10, SpringLayout.SOUTH, stegoBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, txtFld_stegoPath, 10, SpringLayout.NORTH, stegoBrowser);
		sL.putConstraint(SpringLayout.WEST, txtFld_stegoPath, 10, SpringLayout.WEST, stegoBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, btn_stegoBrowse, 10, SpringLayout.NORTH, stegoBrowser);
		sL.putConstraint(SpringLayout.SOUTH, btn_stegoBrowse, -1, SpringLayout.SOUTH, txtFld_stegoPath);
		sL.putConstraint(SpringLayout.WEST, btn_stegoBrowse, 10, SpringLayout.EAST, txtFld_stegoPath);
		
		stegoBrowser.setPreferredSize(new Dimension(WIDTH / 2,HEIGHT / 2));
		stegoBrowser.setVisible(true);
		return stegoBrowser;
	}
	public void imageFileSelected(){
		stegoBrowser.remove(audioPreview_stego);
		stegoBrowser.add(imagePreview_stego);
		
		SpringLayout stego_sL = (SpringLayout)stegoBrowser.getLayout();
		
		stego_sL.putConstraint(SpringLayout.NORTH, imagePreview_stego, 10, SpringLayout.SOUTH, txtFld_stegoPath);
		stego_sL.putConstraint(SpringLayout.EAST, imagePreview_stego, -10, SpringLayout.EAST, stegoBrowser);
		stego_sL.putConstraint(SpringLayout.WEST, imagePreview_stego, 10, SpringLayout.WEST, stegoBrowser);
		stego_sL.putConstraint(SpringLayout.SOUTH, imagePreview_stego, -10, SpringLayout.SOUTH, stegoBrowser);
		
		
		stegoBrowser.revalidate();
		stegoBrowser.repaint();
	}
	public void audioFileSelected(){
		stegoBrowser.remove(imagePreview_stego);
		stegoBrowser.add(audioPreview_stego);
		
		SpringLayout stego_sL = (SpringLayout)stegoBrowser.getLayout();
		
		stego_sL.putConstraint(SpringLayout.NORTH, audioPreview_stego, 10, SpringLayout.SOUTH, txtFld_stegoPath);
		stego_sL.putConstraint(SpringLayout.EAST, audioPreview_stego, -10, SpringLayout.EAST, stegoBrowser);
		stego_sL.putConstraint(SpringLayout.WEST, audioPreview_stego, 10, SpringLayout.WEST, stegoBrowser);
		stego_sL.putConstraint(SpringLayout.SOUTH, audioPreview_stego, -10, SpringLayout.SOUTH, stegoBrowser);
		
		
		stegoBrowser.revalidate();
		stegoBrowser.repaint();
	}
	private JPanel messageOutput(){
		messageOutput = new JPanel();
		messageOutput.setBorder(BorderFactory.createTitledBorder("Recovered Hidden Message:"));
		SpringLayout sL = new SpringLayout();
		messageOutput.setLayout(sL);
		
		txtArea_message = new JTextArea();
		txtArea_message.setWrapStyleWord(true);
		txtArea_message.setName("txtArea_message");
		JScrollPane scrollPane_msg = new JScrollPane(txtArea_message);
		messageOutput.add(scrollPane_msg);
		
		btn_msgCopy = new JButton();
		btn_msgCopy.setText("Copy to clipboard");
		messageOutput.add(btn_msgCopy);
		btn_msgCopy.setName("btn_msgCopy");
		btn_msgCopy.setEnabled(false);
		
		btn_msgSave = new JButton();
		btn_msgSave.setText("Save as .txt");
		messageOutput.add(btn_msgSave);
		btn_msgSave.setEnabled(false);
		
		sL.putConstraint(SpringLayout.NORTH, scrollPane_msg, 10, SpringLayout.NORTH, messageOutput);
		sL.putConstraint(SpringLayout.EAST, scrollPane_msg, -10, SpringLayout.EAST, messageOutput);
		sL.putConstraint(SpringLayout.WEST, scrollPane_msg, 10, SpringLayout.WEST, messageOutput);
		sL.putConstraint(SpringLayout.SOUTH, scrollPane_msg, -10, SpringLayout.NORTH, btn_msgCopy);
		
		sL.putConstraint(SpringLayout.WEST, btn_msgCopy, 10, SpringLayout.WEST, messageOutput);
		sL.putConstraint(SpringLayout.SOUTH, btn_msgCopy, -10, SpringLayout.SOUTH, messageOutput);
		
		sL.putConstraint(SpringLayout.WEST, btn_msgSave, 10, SpringLayout.EAST, btn_msgCopy);
		sL.putConstraint(SpringLayout.SOUTH, btn_msgSave, -10, SpringLayout.SOUTH, messageOutput);

		messageOutput.setPreferredSize(new Dimension(WIDTH, HEIGHT / 4));
		messageOutput.setVisible(true);
		return messageOutput;
	}
	private JPanel decodeControl(){
		decodeControl = new JPanel();
		decodeControl.setBorder(BorderFactory.createTitledBorder("Decode Control:"));
		SpringLayout sL = new SpringLayout();
		decodeControl.setLayout(sL);
		
		JLabel lbl_passKey = new JLabel();
		lbl_passKey.setText("Password for decyption:");
		decodeControl.add(lbl_passKey);
		
		passFld_passwordKey = new JPasswordField();
		passFld_passwordKey.setName("passFld_passwordKey");
		passFld_passwordKey.setEchoChar('*');
		decodeControl.add(passFld_passwordKey);
		
		btn_decodeStart = new JButton();
		btn_decodeStart.setText("Decode");
		btn_decodeStart.setName("btn_decodeStart");
		btn_decodeStart.setEnabled(false);
		decodeControl.add(btn_decodeStart);
		
		
		sL.putConstraint(SpringLayout.WEST, lbl_passKey, 10, SpringLayout.WEST, decodeControl);
		sL.putConstraint(SpringLayout.WEST, lbl_passKey, 10, SpringLayout.NORTH, decodeControl);
		sL.putConstraint(SpringLayout.WEST, passFld_passwordKey, 0, SpringLayout.WEST, lbl_passKey);
		sL.putConstraint(SpringLayout.NORTH, passFld_passwordKey, 10, SpringLayout.SOUTH, lbl_passKey);
		sL.putConstraint(SpringLayout.EAST, passFld_passwordKey, 150, SpringLayout.WEST, passFld_passwordKey);
		
		sL.putConstraint(SpringLayout.NORTH, btn_decodeStart, 10, SpringLayout.SOUTH, passFld_passwordKey);
		sL.putConstraint(SpringLayout.WEST, btn_decodeStart, 10, SpringLayout.WEST, decodeControl);
		
		decodeControl.setPreferredSize(new Dimension(WIDTH / 2,HEIGHT / 2));
		decodeControl.setVisible(true);
		return decodeControl;
	}
	
	private JPanel statusBar(){
		
		statusBar = new JPanel();
		
		SpringLayout sL = new SpringLayout();
		statusBar.setLayout(sL);
		
		lbl_statusBar = new JLabel();
		lbl_statusBar.setText("Status: ");
		lbl_statusBar.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		statusBar.add(lbl_statusBar);
		
		sL.putConstraint(SpringLayout.EAST, lbl_statusBar, 0, SpringLayout.EAST, statusBar);
		
		statusBar.setPreferredSize(new Dimension(WIDTH,STATUS_BAR_HEIGHT));
		
		return statusBar;
	}
	public ImagePreview getImagePreview_stego(){
		return imagePreview_stego;
	}
	public void setImagePreview_stegoMouseListener(MouseListener mL){
		imagePreview_stego.addMouseListener(mL);
	}
	public void setStegoText(BufferedImage stegotext, File file){
		imagePreview_stego.setImage(stegotext);
		imagePreview_stego.setCaption(file.getName());
		txtFld_stegoPath.setText(file.getPath());
		imagePreview_stego.repaint();
	}
	public void setStegoText(AudioInputStream audio, File file){
		txtFld_stegoPath.setText(file.getPath());
		imagePreview_stego.repaint();
	}
	public void setStatus(String text, Color c){
		lbl_statusBar.setText("Status: " + text);
		lbl_statusBar.setForeground(c);
	}
	public JButton getBtn_DecodeStart() {
		return btn_decodeStart;
	}
	public JButton getBtn_stegoBrowse(){
		return btn_stegoBrowse;
	}
	public JButton getBtn_msgCopy(){
		return btn_msgCopy;
	}
	public JTextArea getTxtArea_message(){
		return txtArea_message;
	}
	public void setBtn_stegoBrowseActionListener(ActionListener al){
		btn_stegoBrowse.addActionListener(al);
	}
	public void setBtn_decodeStartActionListener(ActionListener al){
		btn_decodeStart.addActionListener(al);
	}
	public void setBtn_msgCopyActionListener(ActionListener al){
		btn_msgCopy.addActionListener(al);
	}
	public void setMessageText(String text){
		this.txtArea_message.setText(text);
	}
	public void setAudioStegoBtn_PlayActionLisener(ActionListener aL){
		audioPreview_stego.setBtn_PlayActionListener(aL);
	}
	public void setAudioStegoBtn_PauseActionLisener(ActionListener aL){
		audioPreview_stego.setBtn_PauseActionListener(aL);
	}
	public void setAudioStegoBtn_StopActionLisener(ActionListener aL){
		audioPreview_stego.setBtn_StopActionListener(aL);
	}
	public JButton getAudioStegoBtn_Play(){
		return audioPreview_stego.getBtn_Play();
	}
	public JButton getAudioStegoBtn_Pause(){
		return audioPreview_stego.getBtn_Pause();
	}
	public JButton getAudioStegoBtn_Stop(){
		return audioPreview_stego.getBtn_Stop();
	}
	public JLabel getAudioStegoLbl_Time(){
		return audioPreview_stego.getLbl_Time();
	}
	public JButton getBtn_msgSave(){
		return btn_msgSave;
	}
	public void setBtn_msgSaveActionLisener(ActionListener aL){
		btn_msgSave.addActionListener(aL);
	}
	public String getPassword(){
		return new String(passFld_passwordKey.getPassword());
	}
	public void showPassword(){
		passFld_passwordKey.setEchoChar((char)0);
	}
	public void hidePassword(){
		passFld_passwordKey.setEchoChar('*');
	}
	public void enablePassword(){
		passFld_passwordKey.setEditable(true);
	}
	public void disablePassword(){
		passFld_passwordKey.setEditable(false);
	}
}
