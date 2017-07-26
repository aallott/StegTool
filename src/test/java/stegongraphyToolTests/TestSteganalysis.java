package stegongraphyToolTests;

import static org.junit.Assert.assertEquals;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import controller.MainController;

/**
 *<p>Class for performing JUnit tests checking the steganalysis function
 *on covertexts and stegotexts.
 * 
 * @author Ashley Allott
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSteganalysis {
	
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
	public void testA_checkBitmap() throws InterruptedException{
		System.out.println("TEST - Bmp Steganalysis");
		boolean result = false;
		
		System.out.println("TEST - Covertext");
    	result = Utils.chiSquare(window, "giraffe.bmp", 10);
    	System.out.println("Expected: " + false);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(false, result);
		
		System.out.println("TEST - 1Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "bitmap_1bit.bmp", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 2Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "bitmap_2bit.bmp", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 4Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "bitmap_4bit.bmp", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 8Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "bitmap_8bit.bmp", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
	}
	
	@Test
	public void testB_checkPng() throws InterruptedException{
		System.out.println("TEST - Png Steganalysis");
		boolean result = false;
		
		System.out.println("TEST - Covertext");
    	result = Utils.chiSquare(window, "giraffe.png", 10);
    	System.out.println("Expected: " + false);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(false, result);
		
		System.out.println("TEST - 1Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "png_1bit.png", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 2Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "png_2bit.png", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 4Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "png_4bit.png", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
    	
    	System.out.println("TEST - 8Bit Embedded Stegotext");
    	result = Utils.chiSquare(window, "png_8bit.png", 10);
    	System.out.println("Expected: " + true);
    	System.out.println("Actual: " + result);
    	System.out.println();
    	assertEquals(true, result);
	}
	
	@After
    public void tearDown() {
    	window.cleanUp();
    }
}
