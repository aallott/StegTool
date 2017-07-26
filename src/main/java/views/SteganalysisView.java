package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import analysis.Histogram;

/**
 * <p>This class is used for creating a view for Steganalysis.
 * @author Ashley Allott
 */
public class SteganalysisView extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public static final int HISTOGRAM_DATA_RED = 1;
	public static final int HISTOGRAM_DATA_GREEN = 2;
	public static final int HISTOGRAM_DATA_BLUE = 3;
	
	public static final String TAB_TITLE_HISTOGRAM_RED = "Red Histogram";
	public static final String TAB_TITLE_HISTOGRAM_GREEN = "Green Histogram";
	public static final String TAB_TITLE_HISTOGRAM_BLUE = "Blue Histogram";
	public static final String TAB_TITLE_CHI_SQUARE = "Chi Square Chart";
	
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
	
	private ImagePreview imagePreview_stego;
	
	private JPanel coverBrowser;
	private JTextField txtFld_coverPath;
	private JButton btn_coverBrowse;
	
	private JPanel anaylsisControl;
	private JButton btn_genHistogram;
	private JButton btn_performChiSquare;
	
	private JPanel chartContainer;
	
	private JPanel statusBar;
	private JLabel lbl_statusBar;
	
	private JTabbedPane tabPane;
	
	private HistogramChart red;
	private HistogramChart green;
	private HistogramChart blue;
	private ChiSquareChart chiChart;
	
	public SteganalysisView(){
		super(true);
		SpringLayout sL = new SpringLayout();
		this.setLayout(sL);
		this.add(coverBrowser());
		this.add(anaylsisControl());
		this.add(chartContainer());
		this.add(statusBar());
		
		this.setStatus("Awaiting Input", STATUS_NORMAL);
		
		sL.putConstraint(SpringLayout.NORTH, coverBrowser, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, coverBrowser, 0, SpringLayout.WEST, this);
		
		sL.putConstraint(SpringLayout.NORTH, anaylsisControl, 0, SpringLayout.NORTH, this);
		sL.putConstraint(SpringLayout.WEST, anaylsisControl, 0, SpringLayout.EAST, coverBrowser);
		sL.putConstraint(SpringLayout.EAST, anaylsisControl, 0, SpringLayout.EAST, this);
		
		sL.putConstraint(SpringLayout.NORTH, chartContainer, 0, SpringLayout.SOUTH, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, chartContainer, 0, SpringLayout.WEST, this);
		
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
			coverBrowser.setPreferredSize(new Dimension(width/2, height/4));
			anaylsisControl.setPreferredSize(new Dimension(width/2, height/4));
			chartContainer.setPreferredSize(new Dimension(width,height-(height/4)));
		}else{
			coverBrowser.setPreferredSize(new Dimension(width/2, height/4));
			anaylsisControl.setPreferredSize(new Dimension(width/2, height/4));
			chartContainer.setPreferredSize(new Dimension(width,height-(height/2)));
		}
	}
	
	private JPanel coverBrowser(){
		coverBrowser = new JPanel();
		coverBrowser.setBorder(BorderFactory.createTitledBorder("Select Stegotext (image to analyse):"));
		SpringLayout sL = new SpringLayout();
		coverBrowser.setLayout(sL);
		
		txtFld_coverPath = new JTextField();
		txtFld_coverPath.setColumns(25);
		txtFld_coverPath.setEditable(false);
		coverBrowser.add(txtFld_coverPath);
				
		btn_coverBrowse = new JButton("Browse");
		btn_coverBrowse.setName("btn_coverBrowse");
		coverBrowser.add(btn_coverBrowse);
		
		imagePreview_stego = new ImagePreview("Stegotext:");
		
		sL.putConstraint(SpringLayout.NORTH, txtFld_coverPath, 10, SpringLayout.NORTH, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, txtFld_coverPath, 10, SpringLayout.WEST, coverBrowser);
		
		sL.putConstraint(SpringLayout.NORTH, btn_coverBrowse, 10, SpringLayout.NORTH, coverBrowser);
		sL.putConstraint(SpringLayout.SOUTH, btn_coverBrowse, -1, SpringLayout.SOUTH, txtFld_coverPath);
		sL.putConstraint(SpringLayout.WEST, btn_coverBrowse, 10, SpringLayout.EAST, txtFld_coverPath);
		
		coverBrowser.setPreferredSize(new Dimension(WIDTH / 2, HEIGHT / 4));
		coverBrowser.setVisible(true);
		
		return coverBrowser;
	}
	private JPanel anaylsisControl(){
		anaylsisControl = new JPanel();
		anaylsisControl.setBorder(BorderFactory.createTitledBorder("Analsis Control:"));
		
		SpringLayout sL = new SpringLayout();
		anaylsisControl.setLayout(sL);
		
		btn_genHistogram = new JButton("Gen Histogram");
		btn_genHistogram.setName("btn_genHistogram");
		btn_genHistogram.setEnabled(false);
		anaylsisControl.add(btn_genHistogram);
		
		btn_performChiSquare = new JButton("Perform Chi Square");
		btn_performChiSquare.setName("btn_performChiSquare");
		btn_performChiSquare.setEnabled(false);
		anaylsisControl.add(btn_performChiSquare);
		
		sL.putConstraint(SpringLayout.NORTH, btn_genHistogram, 10, SpringLayout.NORTH, anaylsisControl);
		sL.putConstraint(SpringLayout.WEST, btn_genHistogram, 10, SpringLayout.WEST, anaylsisControl);
		
		sL.putConstraint(SpringLayout.NORTH, btn_performChiSquare, 10, SpringLayout.NORTH, anaylsisControl);
		sL.putConstraint(SpringLayout.WEST, btn_performChiSquare, 10, SpringLayout.EAST, btn_genHistogram);
		
		
		anaylsisControl.setPreferredSize(new Dimension(WIDTH / 2,HEIGHT / 4));
		anaylsisControl.setVisible(true);
		
		return anaylsisControl;
	}
	
	private JPanel chartContainer(){
		chartContainer = new JPanel();
		chartContainer.setBorder(BorderFactory.createTitledBorder("Charts:"));
		SpringLayout sL = new SpringLayout();
		chartContainer.setLayout(sL);
		
		red = new HistogramChart();
		red.setBarColor(HistogramChart.BAR_RED);
		green = new HistogramChart();
		green.setBarColor(HistogramChart.BAR_GREEN);
		blue = new HistogramChart();
		blue.setBarColor(HistogramChart.BAR_BLUE);
		
		chiChart = new ChiSquareChart();
		
		tabPane = new JTabbedPane();
		tabPane.addTab(TAB_TITLE_HISTOGRAM_RED, red);
		tabPane.addTab(TAB_TITLE_HISTOGRAM_GREEN, green);
		tabPane.addTab(TAB_TITLE_HISTOGRAM_BLUE, blue);
		tabPane.addTab(TAB_TITLE_CHI_SQUARE, chiChart);
		chartContainer.add(tabPane);
		
		sL.putConstraint(SpringLayout.NORTH, tabPane, 10, SpringLayout.NORTH, chartContainer);
		sL.putConstraint(SpringLayout.EAST, tabPane, -10, SpringLayout.EAST, chartContainer);
		sL.putConstraint(SpringLayout.WEST, tabPane, 10, SpringLayout.WEST, chartContainer);
		sL.putConstraint(SpringLayout.SOUTH, tabPane, -10, SpringLayout.SOUTH, chartContainer);
		
		chartContainer.setPreferredSize(new Dimension(WIDTH,HEIGHT-(HEIGHT/4)));
		chartContainer.setVisible(true);
		return chartContainer;
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
	
	public void setChartData(int type, int[] data){
		if(type == Histogram.HISTOGRAM_DATA_RED){
			red.setData(data);
			red.repaint();
		}else if(type == Histogram.HISTOGRAM_DATA_GREEN){
			green.setData(data);
			green.repaint();
		}else if(type == Histogram.HISTOGRAM_DATA_BLUE){
			blue.setData(data);
			blue.repaint();
		}
	}
	public void setStegoText(BufferedImage stegotext, File file){
		imagePreview_stego.setImage(stegotext);
		imagePreview_stego.setCaption(file.getName());
		imagePreview_stego.repaint();
		
		coverBrowser.add(imagePreview_stego);
		
		SpringLayout sL = (SpringLayout)coverBrowser.getLayout();
		
		sL.putConstraint(SpringLayout.NORTH, imagePreview_stego, 10, SpringLayout.SOUTH, txtFld_coverPath);
		sL.putConstraint(SpringLayout.EAST, imagePreview_stego, -10, SpringLayout.EAST, coverBrowser);
		sL.putConstraint(SpringLayout.WEST, imagePreview_stego, 10, SpringLayout.WEST, coverBrowser);
		sL.putConstraint(SpringLayout.SOUTH, imagePreview_stego, -10, SpringLayout.SOUTH, coverBrowser);
		this.repaint();
	}
	public void setTabPane(String title){
		for(int i=0; i<tabPane.getTabCount(); i++){
			if(tabPane.getTitleAt(i).equals(title)){
				tabPane.setSelectedIndex(i);
				break;
			}
		}
	}
	public ImagePreview getImagePreview_stego(){
		return imagePreview_stego;
	}
	public void setImagePreview_stegoMouseListener(MouseListener mL){
		imagePreview_stego.addMouseListener(mL);
	}
	public HistogramChart getRedChart(){
		return red;
	}
	public HistogramChart getGreenChart(){
		return green;
	}
	public HistogramChart getBlueChart(){
		return blue;
	}
	public ChiSquareChart getChiChart(){
		return chiChart;
	}
	public void setRedChartMouseListener(MouseListener mL){
		red.addMouseListener(mL);
	}
	public void setGreenChartMouseListener(MouseListener mL){
		green.addMouseListener(mL);
	}
	public void setBlueChartMouseListener(MouseListener mL){
		blue.addMouseListener(mL);
	}
	public void setChiChartMouseListener(MouseListener mL){
		chiChart.addMouseListener(mL);
	}
	public void setBtn_coverBrowseActionListener(ActionListener aL){
		this.btn_coverBrowse.addActionListener(aL);
	}
	public JButton getBtn_coverBrowse(){
		return this.btn_coverBrowse;
	}
	public void setBtn_genHistogramActionListener(ActionListener aL){
		this.btn_genHistogram.addActionListener(aL);
	}
	public JButton getBtn_genHistogram(){
		return this.btn_genHistogram;
	}
	public void setBtn_performChiSquareActionListener(ActionListener aL){
		this.btn_performChiSquare.addActionListener(aL);
	}
	public JButton getBtn_performChiSquare(){
		return this.btn_performChiSquare;
	}
	public void setStatus(String text, Color c){
		lbl_statusBar.setText("Status: " + text);
		lbl_statusBar.setForeground(c);
	}
}
