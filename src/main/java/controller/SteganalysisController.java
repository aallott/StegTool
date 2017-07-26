package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import analysis.ChiSquare;
import analysis.Histogram;
import manipulation.CoverFileFilter;
import manipulation.WorkFile;
import manipulation.sound.AudioPlayer;
import views.MainFrame;
import views.SteganalysisView;

/**
 * <p>This class handles the back end processing for the analysis part of the application.
 * @author Ashley Allott
 */
public class SteganalysisController implements ActionListener, MouseListener{
	
	private MainFrame mainFrame;
	private SteganalysisView sView;
	private WorkFile workFile;
	private Histogram hist;
	
	/**
	 * <p>Constructor, creates a new Steganalysis Controller instance
	 * @param mainFrame		The MainFrame from which the object was created
	 */
	public SteganalysisController(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		sView = new SteganalysisView();
		sView.setBtn_coverBrowseActionListener(this);
		sView.setBtn_genHistogramActionListener(this);
		sView.setBtn_performChiSquareActionListener(this);
		sView.setImagePreview_stegoMouseListener(this);
		sView.setRedChartMouseListener(this);
		sView.setGreenChartMouseListener(this);
		sView.setBlueChartMouseListener(this);
		sView.setChiChartMouseListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sView.getBtn_coverBrowse()){
			getStegotext();
		}else if(e.getSource() == sView.getBtn_genHistogram()){
			//Generate histograms
			hist = new Histogram(workFile);
			hist.calValues();
				 					
			sView.setChartData(SteganalysisView.HISTOGRAM_DATA_RED, hist.getData(Histogram.HISTOGRAM_DATA_RED));
			sView.setChartData(SteganalysisView.HISTOGRAM_DATA_GREEN, hist.getData(Histogram.HISTOGRAM_DATA_GREEN));
			sView.setChartData(SteganalysisView.HISTOGRAM_DATA_BLUE, hist.getData(Histogram.HISTOGRAM_DATA_BLUE));
			sView.setTabPane(SteganalysisView.TAB_TITLE_HISTOGRAM_RED);
			
			sView.setStatus("Histogram Generated!", SteganalysisView.STATUS_GOOD);
		}else if(e.getSource() == sView.getBtn_performChiSquare()){
			//Perform Chi-Square Attack
			try{
				ChiSquare chi = new ChiSquare();
				boolean check = chi.performTest(workFile);
				double[] chiResults = chi.getChiResults();
				double[] lsbAverage = chi.getAverageLSB();
				
				sView.getChiChart().setData(chiResults, lsbAverage);
				sView.setTabPane(SteganalysisView.TAB_TITLE_CHI_SQUARE);
				
				if(check){
					JOptionPane.showMessageDialog(sView, "The supplied image is suspected of containing a hidden message (Estimated size of " + chi.getMessageSizeEstimate() + " bytes).");
				}else{
					JOptionPane.showMessageDialog(sView, "The supplied image isn't suspicious.");
				}
				
				sView.setStatus("Chi Square Generated!", SteganalysisView.STATUS_GOOD);
			}catch(IOException e1) {
				e1.printStackTrace();
			}
		}
	}	
	
	/**
	 * <p>Gets the suspected stego text from the user via a file browser, and sets the corresponding
	 * previews to enable the user to preview the chosen file (image or audio)
	 */
	private void getStegotext(){
		//EncodeController eC = this;
		Thread loaderThread = new Thread(new Runnable() {
		     public void run() {
		    	String dirName = new File(".").toString();
		 		JFileChooser fileChooser = new JFileChooser();
		 		
		 		fileChooser.setCurrentDirectory(new File(dirName));
		 		
		 		fileChooser.addChoosableFileFilter(new CoverFileFilter());
		 		fileChooser.setAcceptAllFileFilterUsed(false);

		 		if(fileChooser.showOpenDialog(sView) == JFileChooser.APPROVE_OPTION) {
		 			sView.setStatus("Loading selected file...", SteganalysisView.STATUS_NORMAL);
		 			sView.getBtn_genHistogram().setEnabled(false);
		 			sView.getBtn_performChiSquare().setEnabled(false);
		 			sView.setChartData(SteganalysisView.HISTOGRAM_DATA_RED, null);
 					sView.setChartData(SteganalysisView.HISTOGRAM_DATA_GREEN, null);
 					sView.setChartData(SteganalysisView.HISTOGRAM_DATA_BLUE, null);
		 			File file = fileChooser.getSelectedFile();
		 			try {
		 				workFile = new WorkFile(file);
		 				if(workFile.fileType == WorkFile.FILE_TYPE_IMAGE){
		 					if(workFile.fileFormat == WorkFile.FILE_FORMAT_JPEG){
		 						sView.getBtn_performChiSquare().setEnabled(false);
		 					}else{
		 						sView.getBtn_performChiSquare().setEnabled(true);
		 					}
		 					sView.getBtn_genHistogram().setEnabled(true);
		 					sView.setStegoText(workFile.getCoverBufferedImage(), file);
		 					sView.setChartData(SteganalysisView.HISTOGRAM_DATA_RED, null);
		 					sView.setChartData(SteganalysisView.HISTOGRAM_DATA_GREEN, null);
		 					sView.setChartData(SteganalysisView.HISTOGRAM_DATA_BLUE, null);
		 					sView.setStatus("File Loaded", SteganalysisView.STATUS_GOOD);
		 				}else if(workFile.fileType == WorkFile.FILE_TYPE_AUDIO){
		 					sView.setStatus("Analysis currently only supports image files", SteganalysisView.STATUS_BAD);
		 				}
		 			} catch (IOException e) {
		 				JOptionPane.showMessageDialog(null, "Error occured while loading selected File");
		 				e.printStackTrace();
		 			} catch (Exception e) {
		 				JOptionPane.showMessageDialog(null, "Error occured while loading selected File");
		 				e.printStackTrace();
		 			}
		 		}	
		 		if(workFile.fileStatus == WorkFile.FILE_STATUS_BAD){
		 			sView.setStatus("Selected file cannot be used", SteganalysisView.STATUS_BAD);
		 		}
		     }
		});  
		loaderThread.start();	
	}
	
	/**
	 * <p>Get the SteganalysisView of which the controller manipulates.
	 * @return	SteganalysisView object the controller interacts with
	 */
	public SteganalysisView getSteganaylsisView(){
		return sView;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == sView.getImagePreview_stego()){
			sView.getImagePreview_stego().openFullScreenImage();
		}else if(e.getSource() == sView.getRedChart()){
			sView.getRedChart().openFullScreenImage();
		}else if(e.getSource() == sView.getGreenChart()){
			sView.getGreenChart().openFullScreenImage();
		}else if(e.getSource() == sView.getBlueChart()){
			sView.getBlueChart().openFullScreenImage();
		}else if(e.getSource() == sView.getChiChart()){
			sView.getChiChart().openFullScreenImage();
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
