package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import controller.MainController;
import manipulation.Utils;
import manipulation.WorkFile;

/**
 * <p>This class is used for creating a frame for the Application.
 * @author Ashley Allott
 */
public class MainFrame extends JFrame implements WindowListener, ComponentListener{
	
	private static final long serialVersionUID = 1L;
	
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	public static int STATUS_BAR_HEIGHT = 16;
	
	
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	
	private EncodeView eView;;
	private DecodeView dView;
	private SteganalysisView sView;
	
	private JMenuBar menuBar;
	private JMenu mFile;
	private JMenuItem mFile_New;
	private JMenuItem mFile_Import;
	private JMenuItem mFile_Export;
	private JMenuItem mFile_Exit;
	private JMenu mEdit;
	private JMenuItem mEdit_;
	private JMenu mTools;
	private JMenuItem mTools_;
	
	private JTabbedPane tabPane;
	
	public MainFrame(EncodeView eView, DecodeView dView, SteganalysisView sView){
		super();
		this.eView = eView;
		this.dView = dView;
		this.sView = sView;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.addComponentListener(this);
		construct_gui();
	}	
	
	@Override
	public void componentResized(ComponentEvent e) {
		eView.resise();
		dView.resise();
		sView.resise();
	}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
	
	private void construct_gui(){
		this.setTitle("Steganography Tool");
		
		//construct_menu();
		//this.setJMenuBar(menuBar);
		
		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(eView);
		
		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(dView);
		
		panel3 = new JPanel();
		panel3.setLayout(new BorderLayout());
		panel3.add(sView);
		
		tabPane = new JTabbedPane();
		tabPane.addTab("Encode", panel1);
		tabPane.addTab("Decode", panel2);
		tabPane.addTab("Analysis", panel3);

		this.add(tabPane);
		
		this.pack();
		this.validate();
		this.setVisible(true);
	}
	
	private void construct_menu(){
		menuBar = new JMenuBar();
		mFile = new JMenu();
		mFile.setText("File");
			mFile_New = new JMenuItem();
			mFile_New.setText("New");
			mFile.add(mFile_New);
			mFile_Import = new JMenuItem();
			mFile_Import.setText("Import");
			mFile.add(mFile_Import);
			mFile_Export = new JMenuItem();
			mFile_Export.setText("Export");
			mFile.add(mFile_Export);
			mFile_Exit = new JMenuItem();
			mFile_Exit.setText("Exit");
			mFile.add(mFile_Exit);
		menuBar.add(mFile);
		mEdit = new JMenu();
		mEdit.setText("Edit");
			mEdit_ = new JMenuItem();
			mEdit_.setText("...");
			mEdit.add(mEdit_);
		menuBar.add(mEdit);
		mTools = new JMenu();
		mTools.setText("Tools");
			mTools_ = new JMenuItem();
			mTools_.setText("...");
			mTools.add(mTools_);
		menuBar.add(mTools);
	}
	
	private void checkExit(){
		int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Close?",  JOptionPane.YES_NO_OPTION);
		if(reply == JOptionPane.YES_OPTION){
			Utils.removeTempFolder();
			System.exit(0);
		}
	}
	@Override
	public void windowOpened(WindowEvent e) {	
	}
	@Override
	public void windowClosing(WindowEvent e) {
		checkExit();	
	}
	@Override
	public void windowClosed(WindowEvent e) {	
	}
	@Override
	public void windowIconified(WindowEvent e) {	
	}
	@Override
	public void windowDeiconified(WindowEvent e) {	
	}
	@Override
	public void windowActivated(WindowEvent e) {	
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
