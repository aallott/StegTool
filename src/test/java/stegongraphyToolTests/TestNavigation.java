package stegongraphyToolTests;

import org.junit.Test;

import controller.MainController;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * <p>Class for performing JUnit tests checking the presence of GUI sections.
 * 
 * @author Ashley Allott
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNavigation {
    
    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {}

    @Before
    public void setUp() {
    	MainController frame = GuiActionRunner.execute(() -> new MainController());
    	window = new FrameFixture(frame.getMainFrame());
    	window.show(); 
    }
    
    @Test
    public void testA_checkSectionsPresent() {
    	System.out.println("TEST - Check Sections Present");
    	String[] expectedTabTitles = {"Encode","Decode","Analysis"};
    	String[] actualTabTitles = window.tabbedPane().tabTitles();
    	
    	System.out.println("Expected: " + Arrays.toString(expectedTabTitles));
    	System.out.println("Actual: " + Arrays.toString(actualTabTitles));
    	assertArrayEquals(expectedTabTitles, actualTabTitles);
    }
    
    @Test
    public void testB_checkEncodeFileBrowser() throws InterruptedException {
    	System.out.println("TEST - Check File Browser Present");
    		
    	JFileChooserFixture fB;
    	
    	window.tabbedPane().selectTab("Encode");
    	window.button("btn_coverBrowse").click();
    	fB = window.fileChooser();
    	fB.selectFile(new File("giraffe.bmp"));
    	fB.cancel();	
    	
    	window.tabbedPane().selectTab("Decode");
    	window.button("btn_stegoBrowse").click();
    	fB = window.fileChooser();
    	fB.selectFile(new File("giraffe.bmp"));
    	fB.cancel();
    	
    	window.tabbedPane().selectTab("Analysis");
    	window.button("btn_coverBrowse").click();
    	fB = window.fileChooser();
    	fB.selectFile(new File("giraffe.bmp"));
    	fB.cancel();
    }
    
    @Test
    public void testC_checkFileAppropriate() throws InterruptedException {
    	System.out.println("TEST - Check File Appropriateness Present");
    		
    	JFileChooserFixture fB;
    	
    	window.tabbedPane().selectTab("Encode");
    	window.button("btn_coverBrowse").click();
    	fB = window.fileChooser();
    	fB.selectFile(new File("giraffe.bmp"));
    	fB.approve();	
    	
    	long elapsedTimeMillis = 0;
    	boolean check = false;
    	while(elapsedTimeMillis < 10000){
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
    	
    	String feedback = window.label("lbl_charCountText").text();
    	
    	check = false;
    	if(feedback.contains("Character Count:")){
    		if(feedback.contains("Remaining Characters:")){
    			check = true;
    		}
    	}
    	assertEquals(true, check);
    }
    
    @After
    public void tearDown() {
    	window.cleanUp();
    }
}
