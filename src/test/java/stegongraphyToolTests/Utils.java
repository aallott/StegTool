package stegongraphyToolTests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;

/**
 * <p>Class provides a collection of common functions to be used by other test classes.
 * 
 * @author Ashley Allott
 */
public class Utils {
	public Utils(){}

	/**
	 * <p>Generates a random string of the specified length.
	 * 
	 * @param length	the length of the string to be generates
	 * @return			the generated random string
	 */
	public static String genString(int length){
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZacdefghijklmnopqrstuvwxyz0123456789";
		
		StringBuilder b = new StringBuilder();
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			b.append(characters.charAt((char)r.nextInt(characters.length())));
		}
		
		return b.toString();
	}
	
	/**
	 * <p>Extracts the integer character count value from the application.
	 * 
	 * <p><The supplied string is expecting in the format: 
	 * <br>Character Count: [x] | Remaining Characters: [y]
	 * <br>Of which the method extracts the value [y].
	 * 
	 * @param str	the string containing character count information
	 * 				<br>Format: Character Count: [x] | Remaining Characters: [y]
	 * @return		the character count integer value
	 */
	public static int getCharacterCount(String str){
		str = str.substring(str.lastIndexOf(":"));
		str = str.replaceAll("[^\\d.]", "");
    	int characterCount = Integer.valueOf(str);
    	return characterCount;
	}
	
	/**
	 * <p>AssertJ test which gets the result of a Chi-Square attack on a file with
	 * the name 'covertextName'.
	 * 
	 * <p>If the supplied file is suspected of containing a hidden message, then
	 * the method will return true, else returns false.
	 * 
	 * @param window			the FrameFixture AssertJ object containing the MainFrame object
	 * @param covertextName		the name of the file to be attacked using the Chi-Square Method
	 * @param maxTimeAllowed	the maximum amount of time (seconds) the test is permitted to take
	 * @return					the result of the Chi-Square attack
	 * @throws InterruptedException
	 */
	public static boolean chiSquare(FrameFixture window, String covertextName, int maxTimeAllowed) throws InterruptedException{
		System.out.println("Analysing Message");
		int maxTimeAllowedMillis = maxTimeAllowed * 1000;
    	int elapsedTimeMillis = 0;
		
    	GenericTypeMatcher<JTabbedPane> tabPaneMatcher = new GenericTypeMatcher<JTabbedPane>(JTabbedPane.class){
			@Override
			protected boolean isMatching(JTabbedPane component) {
				String tabTitle = component.getTitleAt(0);
				if(tabTitle.equals("Encode")){
					return true;
				}else{
					return false;
				}
			}
		};
    	
    	window.tabbedPane(tabPaneMatcher).selectTab("Analysis");
    	
    	window.button("btn_coverBrowse").click();
    	JFileChooserFixture fB = window.fileChooser();
    	fB.setCurrentDirectory(new File("test"));
    	fB.fileNameTextBox().setText(covertextName);
    	fB.approve();
    	
    	boolean check = false;
    	while(elapsedTimeMillis < maxTimeAllowedMillis){
    		check = window.button("btn_performChiSquare").isEnabled();
    		if(check){
    			check = true;
    			break;
    		}else{
    			elapsedTimeMillis += 100;
    			Thread.sleep(100);
    		}
    	}
    	assertEquals(true, check);
    	
    	
    	window.button("btn_performChiSquare").click();
    	
    	long timeTaken = (elapsedTimeMillis) / 1000;
    	System.out.println("Analysis Complete, Time taken: ~" + timeTaken + "s");
    	
    	DialogFixture dF = window.dialog();
    	
    	GenericTypeMatcher<JLabel> labelMatcher = new GenericTypeMatcher<JLabel>(JLabel.class){
			@Override
			protected boolean isMatching(JLabel component) {
				String labelContent = component.getText();
				if(labelContent == null){
					return false;
				}
				if(labelContent.contains("The supplied image")){
					return true;
				}else{
					return false;
				}
			}
		};
    	
    	String output =  dF.label(labelMatcher).text();
    	boolean result = false;
    	if(output.contains("is suspected of containing a hidden message")){
    		result = true;
    	}else if(output.contains("isn't suspicious")){
    		result = false;
    	}
    	dF.close();
    	
    	return result;
	}
	
	/**
	 * <p>AssertJ test which embeds a random message within an given 'covertextName' file
	 * and saves a 'stegotextName' file, a copy of covertext with an embedded message.
	 * 
	 * <p>The generate random message is returned from the method.
	 * 
	 * @param window			the FrameFixture AssertJ object containing the MainFrame object
	 * @param degradationIndex	the index of the value to select from the degradation drop-down
	 * @param password			the password key to be used for encryption
	 * @param covertextName		the name of the covertext file located on system (./test/'covertext')
	 * @param stegotextName		the name of the stegotext to be save on system (./test/'stegotext')
	 * @param maxTimeAllowed	the maximum amount of time (seconds) the test is permitted to take 
	 * @return					the generated random message which has been embedded
	 * @throws InterruptedException
	 */
    public static String embed(FrameFixture window, int degradationIndex, String password, String covertextName, String stegotextName, int maxTimeAllowed) throws InterruptedException{
    	System.out.println("Embedding Message");
    	int maxTimeAllowedMillis = maxTimeAllowed * 1000;
    	int elapsedTimeMillis = 0;
    	
    	window.tabbedPane().selectTab("Encode");
    	window.button("btn_coverBrowse").click();
    	JFileChooserFixture fB = window.fileChooser();
    	fB.setCurrentDirectory(new File("test"));
    	fB.fileNameTextBox().setText(covertextName);
    	fB.approve();
    	
    	boolean check = false;
    	while(elapsedTimeMillis < maxTimeAllowedMillis){
    		check = window.button("btn_encodeStart").isEnabled();
    		if(check){
    			check = true;
    			break;
    		}else{
    			elapsedTimeMillis += 100;
    			Thread.sleep(100);
    		}
    	}
    	assertEquals(true, check);
    	
    	window.comboBox("cmb_bitsPerByte").selectItem(degradationIndex);
    	
    	int characterCount = Utils.getCharacterCount(window.label("lbl_charCountText").text());
    	String message = Utils.genString(Math.min(characterCount, 10));
    	
    	window.textBox("txtArea_message").setText(message.substring(0, message.length()-1));
    	window.textBox("txtArea_message").enterText(""+message.charAt(message.length()-1));
    	window.textBox("passFld_passwordKey").setText(password);
    	window.textBox("passFld_passwordKey").enterText("");
    	
    	window.button("btn_encodeStart").click();
    	
    	check = false;
    	while(elapsedTimeMillis < maxTimeAllowedMillis){
    		check = window.button("btn_stegoExport").isEnabled();
    		if(check){
    			check = true;
    			break;
    		}else{
    			elapsedTimeMillis += 100;
    			Thread.sleep(100);
    		}
    	}
    	assertEquals(true, check);
    	
    	long timeTaken = (elapsedTimeMillis) / 1000;
    	System.out.println("Encoding Complete, Time taken: ~" + timeTaken + "s");
    	
    	window.button("btn_stegoExport").click();
    	fB = window.fileChooser();
    	fB.setCurrentDirectory(new File("test"));
    	fB.fileNameTextBox().setText(stegotextName);
    	fB.approve();
    	
    	DialogFixture dF = window.dialog();
    	dF.close();
    	
    	return message;
    }
    
    /**
     * <p>AssertJ test which recovers a embedded hidden message from a given 'stegotextName' 
     * file, returning the extracted message.
     * 
     * @param window			the FrameFixture AssertJ object containing the MainFrame object
     * @param password			the password key to be used for decryption
     * @param stegotextName		the name of the stegotext file located on system (./test/'stegotextName')
     * @param maxTimeAllowed	the maximum amount of time (seconds) the process is permitted to take 
     * @return					the extracted message
     * @throws InterruptedException
     */
    public static String recover(FrameFixture window, String password, String stegotextName, int maxTimeAllowed) throws InterruptedException{
    	System.out.println("Recovering Message");
    	int maxTimeAllowedMillis = maxTimeAllowed * 1000;
    	int elapsedTimeMillis = 0;
    	
    	window.tabbedPane().selectTab("Decode");
    	window.button("btn_stegoBrowse").click();
    	
    	JFileChooserFixture fB = window.fileChooser();
    	fB.setCurrentDirectory(new File("test"));
    	fB.fileNameTextBox().setText(stegotextName);
    	fB.approve();
    	
    	boolean check = false;
    	while(elapsedTimeMillis < maxTimeAllowedMillis){
    		check = window.button("btn_decodeStart").isEnabled();
    		if(check){
    			check = true;
    			break;
    		}else{
    			elapsedTimeMillis += 100;
    			Thread.sleep(100);
    		}
    	}
    	assertEquals(true, check);
    	
    	window.textBox("passFld_passwordKey").setText(password);
    	window.textBox("passFld_passwordKey").enterText("");
    	
    	window.button("btn_decodeStart").click();
    	
    	check = false;
    	while(elapsedTimeMillis < maxTimeAllowedMillis){
    		check = window.button("btn_msgCopy").isEnabled();
    		if(check){
    			check = true;
    			break;
    		}else{
    			elapsedTimeMillis += 100;
    			Thread.sleep(100);
    		}
    	}
    	assertEquals(true, check);
    	
    	long timeTaken = (elapsedTimeMillis) / 1000;
    	System.out.println("Decoding Complete, Time taken: ~" + timeTaken + "s");
    	  	
    	String message = window.textBox("txtArea_message").text();  	
    	return message;
    }
}
