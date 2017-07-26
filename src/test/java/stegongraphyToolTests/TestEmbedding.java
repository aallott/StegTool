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
 * <p>Class for performing JUnit tests checking the embedding and recovery
 * of hidden messages.
 * 
 * <p>Tests are performed on multiple degradation values for each file format which
 * supports such control, and tests on inclusion of a password-key:
 * 
 * <br>Bitmap (1, 2, 4, 8)
 * <br>PNG (1, 2, 4, 8)
 * <br>WAV (1, 2, 4, 8)
 * <br>JPEG (1, 2, 16, 54)
 * <br>MP3 (N/A)
 * 
 * @author Ashley Allott
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEmbedding {
	
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
    public void testA_checkBitmapEmbedding() throws InterruptedException {
    	System.out.println("TEST - BMP Embedding");
    	
    	String embeddedMesage;
    	String recoveredMesage;
    	
    	System.out.println("TEST - 1Bit Embedding");
    	embeddedMesage = Utils.embed(window, 0, "", "giraffe.bmp", "bitmap_1bit.bmp", 10);
    	recoveredMesage = Utils.recover(window, "", "bitmap_1bit.bmp", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 2Bit Embedding");
    	embeddedMesage = Utils.embed(window, 1, "", "giraffe.bmp", "bitmap_2bit.bmp", 10);
    	recoveredMesage = Utils.recover(window, "", "bitmap_2bit.bmp", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 4Bit Embedding");
    	embeddedMesage = Utils.embed(window, 2, "", "giraffe.bmp", "bitmap_4bit.bmp", 10);
    	recoveredMesage = Utils.recover(window, "", "bitmap_4bit.bmp", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 8Bit Embedding");
    	embeddedMesage = Utils.embed(window, 3, "", "giraffe.bmp", "bitmap_8bit.bmp", 10);
    	recoveredMesage = Utils.recover(window, "", "bitmap_8bit.bmp", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 1Bit Password Embedding");
    	String password = Utils.genString(10);
    	embeddedMesage = Utils.embed(window, 0, password, "giraffe.bmp", "bitmap_1bit_pass.bmp", 10);
    	recoveredMesage = Utils.recover(window, password, "bitmap_1bit_pass.bmp", 10);
    	System.out.println("Password: " + password);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    }
    
    @Test
    public void testB_checkPngEmbedding() throws InterruptedException {
    	System.out.println("TEST - PNG Embedding");
    	
    	String embeddedMesage;
    	String recoveredMesage;
    	
    	System.out.println("TEST - 1Bit Embedding");
    	embeddedMesage = Utils.embed(window, 0, "", "giraffe.png", "png_1bit.png", 10);
    	recoveredMesage = Utils.recover(window, "", "png_1bit.png", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 2Bit Embedding");
    	embeddedMesage = Utils.embed(window, 1, "", "giraffe.png", "png_2bit.png", 10);
    	recoveredMesage = Utils.recover(window, "", "png_2bit.png", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 4Bit Embedding");
    	embeddedMesage = Utils.embed(window, 2, "", "giraffe.png", "png_4bit.png", 10);
    	recoveredMesage = Utils.recover(window, "", "png_4bit.png", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 8Bit Embedding");
    	embeddedMesage = Utils.embed(window, 3, "", "giraffe.png", "png_8bit.png", 10);
    	recoveredMesage = Utils.recover(window, "", "png_8bit.png", 10);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 1Bit Password Embedding");
    	String password = Utils.genString(10);
    	embeddedMesage = Utils.embed(window, 0, password, "giraffe.png", "png_1bit_pass.png", 10);
    	recoveredMesage = Utils.recover(window, password, "png_1bit_pass.png", 10);
    	System.out.println("Password: " + password);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    }
    
    @Test
    public void testC_checkJpgEmbedding() throws InterruptedException {
    	System.out.println("TEST - JPEG Embedding");
    	
    	String embeddedMesage;
    	String recoveredMesage;
    	
    	System.out.println("TEST - 1 AC Value Embedding");
    	embeddedMesage = Utils.embed(window, 0, "", "giraffe.jpg", "jpg_1bit.jpg", 30);
    	recoveredMesage = Utils.recover(window, "", "jpg_1bit.jpg", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 2 AC Value Embedding");
    	embeddedMesage = Utils.embed(window, 1, "", "giraffe.jpg", "jpg_2bit.jpg", 30);
    	recoveredMesage = Utils.recover(window, "", "jpg_2bit.jpg", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 16 AC Value Embedding");
    	embeddedMesage = Utils.embed(window, 15, "", "giraffe.jpg", "jpg_4bit.jpg", 30);
    	recoveredMesage = Utils.recover(window, "", "jpg_4bit.jpg", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 54 AC Value Embedding");
    	embeddedMesage = Utils.embed(window, 53, "", "giraffe.jpg", "jpg_8bit.jpg", 30);
    	recoveredMesage = Utils.recover(window, "", "jpg_8bit.jpg", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    }

    @Test
    public void testD_checkWavEmbedding() throws InterruptedException {
    	System.out.println("TEST - WAV Embedding");
    	
    	String embeddedMesage;
    	String recoveredMesage;
    	
    	System.out.println("TEST - 1Bit Embedding");
    	embeddedMesage = Utils.embed(window, 0, "", "warp.wav", "wav_1bit.wav", 30);
    	recoveredMesage = Utils.recover(window, "", "wav_1bit.wav", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 2Bit Embedding");
     	embeddedMesage = Utils.embed(window, 1, "", "warp.wav", "wav_1bit.wav", 30);
    	recoveredMesage = Utils.recover(window, "", "wav_1bit.wav", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 4Bit Embedding");
     	embeddedMesage = Utils.embed(window, 2, "", "warp.wav", "wav_1bit.wav", 30);
    	recoveredMesage = Utils.recover(window, "", "wav_1bit.wav", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 8Bit Embedding");
     	embeddedMesage = Utils.embed(window, 3, "", "warp.wav", "wav_1bit.wav", 30);
    	recoveredMesage = Utils.recover(window, "", "wav_1bit.wav", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    	System.out.println("TEST - 1Bit Password Embedding");
    	String password = Utils.genString(10);
    	embeddedMesage = Utils.embed(window, 0, password, "warp.wav", "wav_1bit_pass.wav", 10);
    	recoveredMesage = Utils.recover(window, password, "wav_1bit_pass.wav", 10);
    	System.out.println("Password: " + password);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    	
    }
    
    @Test
    public void testE_checkMp3Embedding() throws InterruptedException {
    	System.out.println("TEST - MP3 Embedding");
    	
    	String embeddedMesage;
    	String recoveredMesage;
    	
    	System.out.println("TEST - Embedding");
    	embeddedMesage = Utils.embed(window, 0, "", "gunshot.mp3", "mp3_3bit.mp3", 30);
    	recoveredMesage = Utils.recover(window, "", "mp3_3bit.mp3", 30);
    	System.out.println("Embedded: " + embeddedMesage);
    	System.out.println("Recovered: " + recoveredMesage);
    	System.out.println();
    	assertEquals(embeddedMesage, recoveredMesage);
    }
    
    @After
    public void tearDown() {
    	window.cleanUp();
    }
}
