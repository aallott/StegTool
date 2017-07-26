package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * <p>This class is used for creating a view for Encoding.
 * @author Ashley Allott
 */
public class EncodeView extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public static Color STATUS_NORMAL = Color.BLACK;
	public static Color STATUS_GOOD = Color.GREEN;
	public static Color STATUS_BAD = Color.RED;
	public static Color STATUS_MEDIUM = Color.ORANGE;
	
	private static int WIDTH = 800;
	private static int HEIGHT = 800;
	private static int STATUS_BAR_HEIGHT = 16;
	
	private ImagePreview imagePreview_cover;
	private AudioPreview audioPreview_cover;
	private JPanel coverBrowser;
	private JButton btn_coverBrowse;
	private JTextField txtFld_coverPath;
	
	private ImagePreview imagePreview_stego;
	private AudioPreview audioPreview_stego;
	private JPanel stegoExport;
	private JButton btn_stegoExport;
	
	private JPanel messageInput;
	private JTextArea txtArea_message;
	private JLabel lbl_charCountText;
	
	private JPanel encodeControl;
	private JButton btn_encodeStart;
	//private JSlider sld_bitsPerByte;
	private JLabel lbl_sld;
	private JComboBox<Short> cmb_bitsPerByte;
	private JPasswordField passFld_passwordKey;
	
	private JPanel statusBar;
	private JLabel lbl_statusBar;
	
	public EncodeView(){
		super(true);
		SpringLayout sL = new SpringLayout();
		this.setLayout(sL);
		this.add(coverBrowser());
		this.add(stegoExport());
		this.add(messageInput());
		this.add(encodeControl());
		this.add(statusBar());
		
		this.setStatus("Awaiting Input", STATUS_NORMAL);
		
		sL.putConstraint(SpringLayout.NORTH, coverBrowser, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, coverBrowser, 0, SpringLayout.WEST, this);
		sL.putConstraint(SpringLayout.NORTH, stegoExport, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, stegoExport, 0, SpringLayout.EAST, coverBrowser);
		sL.putConstraint(SpringLayout.NORTH, messageInput, 0, SpringLayout.SOUTH, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, messageInput, 0, SpringLayout.WEST, coverBrowser);
		sL.putConstraint(SpringLayout.NORTH, encodeControl, 0, SpringLayout.SOUTH, messageInput);
		sL.putConstraint(SpringLayout.WEST, encodeControl, 0, SpringLayout.WEST, coverBrowser);
		
		sL.putConstraint(SpringLayout.EAST, statusBar, 0, SpringLayout.EAST, this);
		sL.putConstraint(SpringLayout.WEST, statusBar, 0, SpringLayout.WEST, this);
		sL.putConstraint(SpringLayout.SOUTH, statusBar, 0, SpringLayout.SOUTH, this);
		
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT + STATUS_BAR_HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH,HEIGHT + STATUS_BAR_HEIGHT));
		//this.setBackground(Color.RED);
	}
	
	public void resise(){
		int width = Math.max(this.getWidth(), MainFrame.WIDTH);
		int height = Math.max(this.getHeight(), MainFrame.HEIGHT)-MainFrame.STATUS_BAR_HEIGHT;
		if(this.getHeight() > MainFrame.HEIGHT){
			coverBrowser.setPreferredSize(new Dimension(width/2, height/2));
			stegoExport.setPreferredSize(new Dimension(width/2, height/2));
			messageInput.setPreferredSize(new Dimension(width, height/4));
			encodeControl.setPreferredSize(new Dimension(width, height/4));
		}else{
			coverBrowser.setPreferredSize(new Dimension(width/2, height/4));
			stegoExport.setPreferredSize(new Dimension(width/2, height/4));
			messageInput.setPreferredSize(new Dimension(width, height/4));
			encodeControl.setPreferredSize(new Dimension(width, height/4));
		}
	}
	
	private JPanel coverBrowser(){
		coverBrowser = new JPanel();
		coverBrowser.setBorder(BorderFactory.createTitledBorder("Select Stegotext (file for hiding message):"));
		SpringLayout sL = new SpringLayout();
		coverBrowser.setLayout(sL);
		
		txtFld_coverPath = new JTextField();
		txtFld_coverPath.setColumns(25);
		txtFld_coverPath.setEditable(false);
		coverBrowser.add(txtFld_coverPath);
				
		btn_coverBrowse = new JButton("Browse");
		btn_coverBrowse.setName("btn_coverBrowse");
		coverBrowser.add(btn_coverBrowse);
		
		imagePreview_cover = new ImagePreview("Covertext:");
		audioPreview_cover = new AudioPreview();
		
		sL.putConstraint(SpringLayout.NORTH, imagePreview_cover, 10, SpringLayout.SOUTH, txtFld_coverPath);
		sL.putConstraint(SpringLayout.EAST, imagePreview_cover, -10, SpringLayout.EAST, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, imagePreview_cover, 10, SpringLayout.WEST, coverBrowser);
		sL.putConstraint(SpringLayout.SOUTH, imagePreview_cover, -10, SpringLayout.SOUTH, coverBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, audioPreview_cover, 10, SpringLayout.SOUTH, txtFld_coverPath);
		sL.putConstraint(SpringLayout.EAST, audioPreview_cover, -10, SpringLayout.EAST, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, audioPreview_cover, 10, SpringLayout.WEST, coverBrowser);
		sL.putConstraint(SpringLayout.SOUTH, audioPreview_cover, -10, SpringLayout.SOUTH, coverBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, txtFld_coverPath, 10, SpringLayout.NORTH, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, txtFld_coverPath, 10, SpringLayout.WEST, coverBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, btn_coverBrowse, 10, SpringLayout.NORTH, coverBrowser);
		sL.putConstraint(SpringLayout.SOUTH, btn_coverBrowse, -1, SpringLayout.SOUTH, txtFld_coverPath);
		sL.putConstraint(SpringLayout.WEST, btn_coverBrowse, 10, SpringLayout.EAST, txtFld_coverPath);
		
		coverBrowser.setPreferredSize(new Dimension(WIDTH / 2, HEIGHT / 2));
		coverBrowser.setVisible(true);
		
		return coverBrowser;
	}
	private JPanel stegoExport(){
		stegoExport = new JPanel();
		stegoExport.setBorder(BorderFactory.createTitledBorder("Stegotext View (file with hidden message):"));
		
		SpringLayout sL = new SpringLayout();
		stegoExport.setLayout(sL);
		
		btn_stegoExport = new JButton("Save");
		btn_stegoExport.setName("btn_stegoExport");
		btn_stegoExport.setEnabled(false);
		stegoExport.add(btn_stegoExport);
		
		imagePreview_stego = new ImagePreview("Stegotext:");
		audioPreview_stego = new AudioPreview();
		
		sL.putConstraint(SpringLayout.NORTH, imagePreview_stego, 10, SpringLayout.SOUTH, btn_stegoExport);
		sL.putConstraint(SpringLayout.EAST, imagePreview_stego, -10, SpringLayout.EAST, stegoExport);
		sL.putConstraint(SpringLayout.WEST, imagePreview_stego, 10, SpringLayout.WEST, stegoExport);
		sL.putConstraint(SpringLayout.SOUTH, imagePreview_stego, -10, SpringLayout.SOUTH, stegoExport);
		
		sL.putConstraint(SpringLayout.NORTH, audioPreview_stego, 10, SpringLayout.SOUTH, btn_stegoExport);
		sL.putConstraint(SpringLayout.EAST, audioPreview_stego, -10, SpringLayout.EAST, stegoExport);
		sL.putConstraint(SpringLayout.WEST, audioPreview_stego, 10, SpringLayout.WEST, stegoExport);
		sL.putConstraint(SpringLayout.SOUTH, audioPreview_stego, -10, SpringLayout.SOUTH, stegoExport);
		
		sL.putConstraint(SpringLayout.NORTH, btn_stegoExport, 10, SpringLayout.NORTH, stegoExport);
		sL.putConstraint(SpringLayout.WEST, btn_stegoExport, 10, SpringLayout.WEST, stegoExport);
		sL.putConstraint(SpringLayout.SOUTH, btn_stegoExport, 3, SpringLayout.SOUTH, btn_coverBrowse);
		
		stegoExport.setPreferredSize(new Dimension(WIDTH / 2,HEIGHT / 2));
		stegoExport.setVisible(true);
		
		return stegoExport;
	}
	public void imageFileSelected(){
		coverBrowser.remove(audioPreview_cover);
		stegoExport.remove(audioPreview_stego);
		coverBrowser.add(imagePreview_cover);
		stegoExport.add(imagePreview_stego);
		
		SpringLayout cover_sL = (SpringLayout)coverBrowser.getLayout();
		SpringLayout stego_sL = (SpringLayout)stegoExport.getLayout();
		
		cover_sL.putConstraint(SpringLayout.NORTH, imagePreview_cover, 10, SpringLayout.SOUTH, txtFld_coverPath);
		cover_sL.putConstraint(SpringLayout.EAST, imagePreview_cover, -10, SpringLayout.EAST, coverBrowser);
		cover_sL.putConstraint(SpringLayout.WEST, imagePreview_cover, 10, SpringLayout.WEST, coverBrowser);
		cover_sL.putConstraint(SpringLayout.SOUTH, imagePreview_cover, -10, SpringLayout.SOUTH, coverBrowser);
		
		stego_sL.putConstraint(SpringLayout.NORTH, imagePreview_stego, 10, SpringLayout.SOUTH, btn_stegoExport);
		stego_sL.putConstraint(SpringLayout.EAST, imagePreview_stego, -10, SpringLayout.EAST, stegoExport);
		stego_sL.putConstraint(SpringLayout.WEST, imagePreview_stego, 10, SpringLayout.WEST, stegoExport);
		stego_sL.putConstraint(SpringLayout.SOUTH, imagePreview_stego, -10, SpringLayout.SOUTH, stegoExport);
		
		coverBrowser.revalidate();
		stegoExport.revalidate();
		coverBrowser.repaint();
		stegoExport.repaint();
	}
	public void audioFileSelected(){
		coverBrowser.remove(imagePreview_cover);
		stegoExport.remove(imagePreview_stego);
		coverBrowser.add(audioPreview_cover);
		stegoExport.add(audioPreview_stego);
		
		SpringLayout cover_sL = (SpringLayout)coverBrowser.getLayout();
		SpringLayout stego_sL = (SpringLayout)stegoExport.getLayout();
		
		cover_sL.putConstraint(SpringLayout.NORTH, audioPreview_cover, 10, SpringLayout.SOUTH, txtFld_coverPath);
		cover_sL.putConstraint(SpringLayout.EAST, audioPreview_cover, -10, SpringLayout.EAST, coverBrowser);
		cover_sL.putConstraint(SpringLayout.WEST, audioPreview_cover, 10, SpringLayout.WEST, coverBrowser);
		cover_sL.putConstraint(SpringLayout.SOUTH, audioPreview_cover, -10, SpringLayout.SOUTH, coverBrowser);
		
		stego_sL.putConstraint(SpringLayout.NORTH, audioPreview_stego, 10, SpringLayout.SOUTH, btn_stegoExport);
		stego_sL.putConstraint(SpringLayout.EAST, audioPreview_stego, -10, SpringLayout.EAST, stegoExport);
		stego_sL.putConstraint(SpringLayout.WEST, audioPreview_stego, 10, SpringLayout.WEST, stegoExport);
		stego_sL.putConstraint(SpringLayout.SOUTH, audioPreview_stego, -10, SpringLayout.SOUTH, stegoExport);
		
		coverBrowser.revalidate();
		stegoExport.revalidate();
		coverBrowser.repaint();
		stegoExport.repaint();
	}
	private JPanel messageInput(){
		messageInput = new JPanel();
		messageInput.setBorder(BorderFactory.createTitledBorder("Hidden Message:"));
		
		SpringLayout sL = new SpringLayout();
		messageInput.setLayout(sL);
		
		txtArea_message = new JTextArea();
		txtArea_message.setName("txtArea_message");
		txtArea_message.setWrapStyleWord(true);
		JScrollPane scrollPane_msg = new JScrollPane(txtArea_message);
		messageInput.add(scrollPane_msg);
		
		lbl_charCountText = new JLabel(" ");
		lbl_charCountText.setName("lbl_charCountText");
		messageInput.add(lbl_charCountText);
		
		sL.putConstraint(SpringLayout.NORTH, scrollPane_msg, 10, SpringLayout.NORTH, messageInput);
		sL.putConstraint(SpringLayout.EAST, scrollPane_msg, -10, SpringLayout.EAST, messageInput);
		sL.putConstraint(SpringLayout.WEST, scrollPane_msg, 10, SpringLayout.WEST, messageInput);
		sL.putConstraint(SpringLayout.SOUTH, scrollPane_msg, 0, SpringLayout.NORTH, lbl_charCountText);
		
		sL.putConstraint(SpringLayout.EAST, lbl_charCountText, -10, SpringLayout.EAST, messageInput);
		sL.putConstraint(SpringLayout.SOUTH, lbl_charCountText, -10, SpringLayout.SOUTH, messageInput);
		
		messageInput.setPreferredSize(new Dimension(WIDTH,HEIGHT / 4));
		return messageInput;
	}
	private JPanel encodeControl(){
		encodeControl = new JPanel();
		encodeControl.setBorder(BorderFactory.createTitledBorder("Encode Control:"));
		
		SpringLayout sL = new SpringLayout();
		encodeControl.setLayout(sL);
		
		btn_encodeStart = new JButton();
		btn_encodeStart.setText("Start Encoding");
		btn_encodeStart.setName("btn_encodeStart");
		btn_encodeStart.setEnabled(false);
		encodeControl.add(btn_encodeStart);
		
		lbl_sld = new JLabel();
		lbl_sld.setText("Image degradation(Message bits per covertext byte):");
		encodeControl.add(lbl_sld);
		
		/*sld_bitsPerByte = new JSlider();
		sld_bitsPerByte.setMinimum(1);
		sld_bitsPerByte.setMaximum(8);
		sld_bitsPerByte.setValue(1);
		sld_bitsPerByte.setPaintTicks(true);
		sld_bitsPerByte.setMajorTickSpacing(1);
		sld_bitsPerByte.setPaintLabels(true);
		sld_bitsPerByte.setLabelTable(sld_bitsPerByte.createStandardLabels(1));
		encodeControl.add(sld_bitsPerByte);
		//ADD SLIDER FUNCTIONALITY
		sld_bitsPerByte.setEnabled(false);*/
		
		short[] items = {1,2,4,8};
		updateCmb(items);
		encodeControl.add(cmb_bitsPerByte);
		cmb_bitsPerByte.setName("cmb_bitsPerByte");
		
		JLabel lbl_key = new JLabel();
		lbl_key.setText("Encyption Password Key: ");
		encodeControl.add(lbl_key);
		
		passFld_passwordKey = new JPasswordField();
		passFld_passwordKey.setEchoChar('*');
		passFld_passwordKey.setName("passFld_passwordKey");
		encodeControl.add(passFld_passwordKey);
		
		sL.putConstraint(SpringLayout.NORTH, lbl_sld, 10, SpringLayout.NORTH, encodeControl);
		sL.putConstraint(SpringLayout.WEST, lbl_sld, 10, SpringLayout.WEST, encodeControl);
		sL.putConstraint(SpringLayout.WEST, cmb_bitsPerByte, 0, SpringLayout.WEST, lbl_sld);
		sL.putConstraint(SpringLayout.NORTH, cmb_bitsPerByte, 10, SpringLayout.SOUTH, lbl_sld);
		
		sL.putConstraint(SpringLayout.NORTH, lbl_key, 10, SpringLayout.NORTH, encodeControl);
		sL.putConstraint(SpringLayout.WEST, lbl_key, 30, SpringLayout.EAST, lbl_sld);
		sL.putConstraint(SpringLayout.NORTH, passFld_passwordKey, 10, SpringLayout.SOUTH, lbl_key);
		sL.putConstraint(SpringLayout.WEST, passFld_passwordKey, 0, SpringLayout.WEST, lbl_key);
		sL.putConstraint(SpringLayout.EAST, passFld_passwordKey, 150, SpringLayout.WEST, passFld_passwordKey);
		
		
		sL.putConstraint(SpringLayout.WEST, btn_encodeStart, 10, SpringLayout.WEST, encodeControl);
		sL.putConstraint(SpringLayout.NORTH, btn_encodeStart, 20, SpringLayout.SOUTH, cmb_bitsPerByte);
		
		encodeControl.setPreferredSize(new Dimension(WIDTH,HEIGHT / 4));
		return encodeControl;
	}
	private void updateCmb(short[] items){
		if(cmb_bitsPerByte == null){
			cmb_bitsPerByte = new JComboBox<Short>();
		}
		ActionListener[] aL = cmb_bitsPerByte.getActionListeners();
		for(ActionListener a: aL){
			cmb_bitsPerByte.removeActionListener(a);
		}
		
		cmb_bitsPerByte.removeAllItems();
		for(int i=0; i<items.length; i++){
			cmb_bitsPerByte.addItem(items[i]);
		}
		
		for(ActionListener a: aL){
			cmb_bitsPerByte.addActionListener(a);
		}
	}
	public void updateCmb_bitsPerByte(short[] items){ 
		updateCmb(items);
	}
	
	private JPanel statusBar(){
		
		statusBar = new JPanel();
		
		SpringLayout sL = new SpringLayout();
		statusBar.setLayout(sL);
		
		lbl_statusBar = new JLabel();
		lbl_statusBar.setText("Status: ");
		lbl_statusBar.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		//lbl_statusBar.setPreferredSize(new Dimension(800, 16));
		statusBar.add(lbl_statusBar);
		
		sL.putConstraint(SpringLayout.EAST, lbl_statusBar, 0, SpringLayout.EAST, statusBar);
		
		statusBar.setPreferredSize(new Dimension(WIDTH,STATUS_BAR_HEIGHT));
		
		return statusBar;
	}
	public void setCoverText(BufferedImage covertext, File file){
		imagePreview_cover.setImage(covertext);
		imagePreview_cover.setCaption(file.getName());
		setTxtFld_coverPathText(file.getPath());
		imagePreview_cover.repaint();
	}
	public void setCoverText(AudioInputStream covertext, File file){
		setTxtFld_coverPathText(file.getPath());
		imagePreview_cover.repaint();
	}
	public void setStegoText(BufferedImage stegotext){
		imagePreview_stego.setImage(stegotext);
		imagePreview_stego.setCaption("Completed Stegotext:");
		btn_stegoExport.setEnabled(true);
		imagePreview_stego.repaint();
	}
	public void resizePanel(int width, int height){
		/*if(width > MAX_WIDTH || height > MAX_HEIGHT){
			width = MAX_WIDTH;
			height = MAX_HEIGHT;
		}else if(width < MIN_WIDTH || height < MIN_HEIGHT){
			width = MIN_WIDTH;
			height = MIN_HEIGHT;
		}*/
		this.setPreferredSize(new Dimension(width, height + STATUS_BAR_HEIGHT));
		this.coverBrowser.setPreferredSize(new Dimension(width / 2, height / 2));
		this.stegoExport.setPreferredSize(new Dimension(width / 2, height / 2));
		this.messageInput.setPreferredSize(new Dimension(width, height / 4));
		this.encodeControl.setPreferredSize(new Dimension(width, height / 4));
		this.statusBar.setPreferredSize(new Dimension(width, STATUS_BAR_HEIGHT));
		//this.revalidate();
	}
	public ImagePreview getImagePreview_cover(){
		return imagePreview_cover;
	}
	public void setImagePreview_coverMouseListener(MouseListener mL){
		imagePreview_cover.addMouseListener(mL);
	}
	public ImagePreview getImagePreview_stego(){
		return imagePreview_stego;
	}
	public void setImagePreview_stegoMouseListener(MouseListener mL){
		imagePreview_stego.addMouseListener(mL);
	}
	
	public JLabel getlbl_sld(){
		return lbl_sld;
	}
	public JComboBox getCmb_bitsPerByte(){
		return cmb_bitsPerByte;
	}
	public JButton getBtn_coverBrowse(){
		return btn_coverBrowse;
	}
	public JButton getBtn_encodeStart(){
		return btn_encodeStart;
	}
	public JButton getBtn_stegoExport(){
		return btn_stegoExport;
	}
	public JTextField getTxtFld_coverPath(){
		return txtFld_coverPath;
	}
	public JTextArea getTxtArea_message(){
		return txtArea_message;
	}
	public void setLbl_charCountText(String text){
		lbl_charCountText.setText(text);
	}
	public void setStatus(String text, Color c){
		lbl_statusBar.setText("Status: " + text);
		lbl_statusBar.setForeground(c);
	}
	public void setBtn_coverBrowseActionListener(ActionListener aL){
		btn_coverBrowse.addActionListener(aL);
	}
	public void setBtn_encodeStartActionListener(ActionListener aL){
		btn_encodeStart.addActionListener(aL);
	}
	public void setBtn_stegoExportActionListener(ActionListener aL){
		btn_stegoExport.addActionListener(aL);
	}
	public void setCmb_bitsPerByteActionListener(ActionListener aL){
		cmb_bitsPerByte.addActionListener(aL);
	}
	public void setTxtArea_messageKeyListener(KeyListener kl){
		txtArea_message.addKeyListener(kl);
	}
	public void setTxtFld_coverPathText(String text){
		txtFld_coverPath.setText(text);
	}
	public void setAudioCoverBtn_PlayActionLisener(ActionListener aL){
		audioPreview_cover.setBtn_PlayActionListener(aL);
	}
	public void setAudioCoverBtn_PauseActionLisener(ActionListener aL){
		audioPreview_cover.setBtn_PauseActionListener(aL);
	}
	public void setAudioCoverBtn_StopActionLisener(ActionListener aL){
		audioPreview_cover.setBtn_StopActionListener(aL);
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
	public JButton getAudioCoverBtn_Play(){
		return audioPreview_cover.getBtn_Play();
	}
	public JButton getAudioCoverBtn_Pause(){
		return audioPreview_cover.getBtn_Pause();
	}
	public JButton getAudioCoverBtn_Stop(){
		return audioPreview_cover.getBtn_Stop();
	}
	public JLabel getAudioCoverLbl_Time(){
		return audioPreview_cover.getLbl_Time();
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
	public void enableCoverAudioControl(){
		audioPreview_cover.setEnabled(true);
	}
	public void enableStegoAudioControl(){
		audioPreview_stego.setEnabled(true);
	}
	public void disableCoverAudioControl(){
		audioPreview_cover.setEnabled(false);
	}
	public void disableStegoAudioControl(){
		audioPreview_stego.setEnabled(false);
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
