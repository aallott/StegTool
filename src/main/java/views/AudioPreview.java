package views;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

/**
 * <p>This class is used for previewing Audio.
 * @author Ashley Allott
 */
public class AudioPreview extends JPanel{
	
	private JButton btn_play;
	private JButton btn_pause;
	private JButton btn_stop;
	private JLabel lbl_time;
	
	public AudioPreview(){
		super(true);
		
		SpringLayout sL = new SpringLayout();
		this.setLayout(sL);
		btn_play = new JButton();
		btn_play.setText("Play");
		this.add(btn_play);
		
		btn_pause = new JButton();
		btn_pause.setText("Pause");
		this.add(btn_pause);
		
		btn_stop = new JButton();
		btn_stop.setText("Stop");
		this.add(btn_stop);
		
		lbl_time = new JLabel();
		lbl_time.setText("0s/0s" + "  ");
		lbl_time.setBackground(Color.WHITE);
		lbl_time.setOpaque(true);
		lbl_time.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lbl_time.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(lbl_time);
		
		sL.putConstraint(SpringLayout.NORTH, btn_play, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, btn_play, 0, SpringLayout.WEST, this);
		sL.putConstraint(SpringLayout.NORTH, btn_pause, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, btn_pause, 5, SpringLayout.EAST, btn_play);
		sL.putConstraint(SpringLayout.NORTH, btn_stop, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, btn_stop, 5, SpringLayout.EAST, btn_pause);
		sL.putConstraint(SpringLayout.NORTH, lbl_time, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.SOUTH, lbl_time, 0, SpringLayout.SOUTH, btn_stop);
		sL.putConstraint(SpringLayout.WEST, lbl_time, 5, SpringLayout.EAST, btn_stop);
		sL.putConstraint(SpringLayout.EAST, lbl_time, 0, SpringLayout.EAST, this);
		
		this.setVisible(true);
	}
	
	public void setBtn_PlayActionListener(ActionListener aL){
		btn_play.addActionListener(aL);
	}
	public void setBtn_PauseActionListener(ActionListener aL){
		btn_pause.addActionListener(aL);
	}
	public void setBtn_StopActionListener(ActionListener aL){
		btn_stop.addActionListener(aL);
	}
	public void setLbl_timeText(String text){
		lbl_time.setText(text + "  ");
	}
	public JButton getBtn_Play(){
		return btn_play;
	}
	public JButton getBtn_Pause(){
		return btn_pause;
	}
	public JButton getBtn_Stop(){
		return btn_stop;
	}
	public JLabel getLbl_Time(){
		return lbl_time;
	}
	@Override
	public void setEnabled(boolean enable){
		super.setEnabled(enable);
		btn_play.setEnabled(enable);
		btn_pause.setEnabled(enable);
		btn_stop.setEnabled(enable);
	}
	
}
