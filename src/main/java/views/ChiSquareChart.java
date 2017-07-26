package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <p>This class is used for displaying a Chi Square Chart.
 * @author Ashley Allott
 */
public class ChiSquareChart extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private boolean isFullscreen;

	private static final int PADDING = 10;
	private static final int X_AXIS_PADDING = 40;
	private static final int Y_AXIS_PADDING = 30;
	
	public static final Color BAR_RED = new Color(200, 10, 10, 120);
	public static final Color BAR_GREEN = new Color(10, 200, 10, 120);
	public static final Color BAR_BLUE = new Color(10, 10, 200, 120);
	
	private Color barColor;
	private Color bgColor = Color.BLACK;
	private Color fgColor = Color.WHITE;
	
	private int xAxisPadding = 40;
	private double[] chiData;
	private double[] lsbData;
	
	public ChiSquareChart(){
		super(true);
		this.barColor = BAR_BLUE;
	}
	public ChiSquareChart(double[] data){
		super(true);
		this.chiData = data;
		this.barColor = BAR_BLUE;
	}
	public ChiSquareChart(double[] data, Color barColor){
		super(true);
		this.chiData = data;
		this.barColor = barColor;
	}
	public ChiSquareChart(ChiSquareChart other){
		super(true);
		this.chiData = other.chiData;
		this.lsbData = other.lsbData;
		this.barColor = other.barColor;
	}
	
	public void setData(double[] chiData, double[] lsbData){
		this.chiData = chiData;
		this.lsbData = lsbData;
		this.repaint();
	}
	public void setBarColor(Color barColor){
		this.barColor = barColor;
	}
	
	public void paint(Graphics g){
		update(g);
	}
	
	public void update(Graphics g){
		if(chiData != null){
			g.setColor(bgColor);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			//Draw Axis
			this.drawYAxis(g);
			//this.populateChart(g);
			this.drawXAxis(g);
			this.populateChart(g);
		}else{
			g.clearRect(0, 0, 0, 0);
		}
	}

	private void populateChart(Graphics g){
		int textXMin = xAxisPadding;
		int textXMax = this.getWidth() - (PADDING + xAxisPadding);
		int pixelsX = textXMax - textXMin;
		
		for(int i=0; i<lsbData.length; i++){
			if(pixelsX < lsbData.length){
				int iBounded = ((textXMax - 0) * (i - 0) / (lsbData.length - 0)) + 0;
				plotGraphPoint(lsbData[iBounded], iBounded, Color.GREEN, g);
			}else{
				plotGraphPoint(lsbData[i], i, Color.GREEN, g);
			}
		}
		
		for(int i=0; i<chiData.length; i++){
			if(pixelsX < chiData.length){
				int iBounded = ((textXMax - 0) * (i - 0) / (chiData.length - 0)) + 0;
				plotGraphPoint(chiData[iBounded], iBounded, Color.RED, g);
			}else{
				plotGraphPoint(chiData[i], i, Color.RED, g);
			}
		}
		
	}
	
	private void plotGraphPoint(double value, int pointNumber, Color c, Graphics g){
		Color c1 = g.getColor();
		
		int yMin =  this.getHeight()-Y_AXIS_PADDING;
		int yMax = PADDING;
		
		double pixelHeight = ((yMax - yMin) * (value - 0) / (1 - 0)) + yMin;
	
		int xPos = (xAxisPadding+pointNumber);
		
		g.setColor(c);	
		g.drawLine(xPos, (int)pixelHeight, xPos, (int)pixelHeight);
		g.setColor(c1);
	}
	
	private void drawXAxis(Graphics g){
		Color c = g.getColor();
		
		g.setColor(fgColor);	
		g.drawLine(xAxisPadding, this.getHeight()-Y_AXIS_PADDING, this.getWidth()-PADDING, this.getHeight()-Y_AXIS_PADDING);
		g.setFont(getAxisFont());
		
		g.setColor(c);
	}
	private void drawYAxis(Graphics g){
		int min = 0;
		int max = 1;
		xAxisPadding = Math.max(X_AXIS_PADDING, (PADDING + 5 + this.getAxisFontWidth(""+max, g)));
		
		Color c = g.getColor();
		
		g.setColor(fgColor);	
		g.drawLine(xAxisPadding, this.getHeight()-Y_AXIS_PADDING, xAxisPadding, PADDING);
		g.setFont(getAxisFont());
		
		
		int textX = PADDING;
		int textYMin =  this.getHeight()-Y_AXIS_PADDING;
		int textYMax = PADDING;
		
		g.drawString(""+(min), textX, (textYMin) + (this.getAxisFontHeight("", g)/2));
		g.drawLine(xAxisPadding-5, textYMin, xAxisPadding, textYMin);
		
		g.drawString(""+max, textX, textYMax + (this.getAxisFontHeight("", g)/2));
		g.drawLine(xAxisPadding-5, textYMax, xAxisPadding, textYMax);
		  
		g.setColor(c);
	}
	private Font getAxisFont(){
		Font font = new Font("Arial", Font.PLAIN, 10);
		return font;
	}
	private int getAxisFontWidth(String text, Graphics g){
		FontMetrics fm = g.getFontMetrics(getAxisFont());
		g.setFont(getAxisFont());
		Rectangle2D bounds = fm.getStringBounds(text, g);
		int fx = (int)bounds.getWidth();
		return fx;
	}
	private int getAxisFontHeight(String text, Graphics g){
		FontMetrics fm = g.getFontMetrics(getAxisFont());
		g.setFont(getAxisFont());
		Rectangle2D bounds = fm.getStringBounds(text, g);
		int fy = (int)bounds.getHeight();
		return fy;
	}
	public void openFullScreenImage(){
		if(!isFullscreen){
			if(chiData != null){
				ChiSquareChart chart = new ChiSquareChart(this);
				JFrame imageFrame = new JFrame();
				chart.setPreferredSize(new Dimension(500, 300));
				imageFrame.setVisible(true);
				imageFrame.add(chart);
				imageFrame.pack();
				imageFrame.addWindowListener(new WindowAdapter(){
				    public void windowClosing(WindowEvent e){
				    	imageFrame.remove(chart);
				    	isFullscreen = false;
				    }
				});
				isFullscreen = true;
			}
		}
	}
}
