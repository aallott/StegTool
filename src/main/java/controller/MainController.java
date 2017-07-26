package controller;

import views.MainFrame;

/**
 * <p>This class handles the main frame of the application, creating instances of each controller
 * for each application section
 * 
 * @author Ashley Allott
 */
public class MainController{
	
	private EncodeController eController;
	private DecodeController dController;
	private SteganalysisController sController;
	private MainFrame mainFrame;
	
	
	public MainController(){
		eController = new EncodeController(mainFrame);
		dController = new DecodeController(mainFrame);
		sController = new SteganalysisController(mainFrame);
		mainFrame = new MainFrame(eController.getEncodeView(), dController.getDecodeView(), sController.getSteganaylsisView());
	}
	public MainFrame getMainFrame(){
		return mainFrame;
	}
	
}
