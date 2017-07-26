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
 * <p>This class is used for displaying a Histogram Chart.
 * @author Ashley Allott
 */
public class HistogramChart extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private boolean isFullscreen;
	
	private static final int PADDING = 10;
	private static final int X_AXIS_PADDING = 40;
	private static final int Y_AXIS_PADDING = 30;
	
	public static final Color BAR_RED = new Color(200, 10, 10, 120);
	public static final Color BAR_GREEN = new Color(10, 200, 10, 120);
	public static final Color BAR_BLUE = new Color(10, 10, 200, 120);
	
	private Color barColor;
	
	private int xAxisPadding = 40;
	private int yAxisPadding = 30;
	
	private int[] data;
	
	private int yAxisMinValue = 0;
	private int yAxisMaxValue = 0;
	private int yAxisSpacing = 0;
	private int yAxisValues = 0;
	
	private int yAxisPixelMin = 0;
	private int yAxisPixelMax = 0;
	private double yAxisPixelSpacing = 0;
	
	public HistogramChart(){
		super(true);
		this.barColor = BAR_BLUE;
	}
	public HistogramChart(int[] data){
		super(true);
		this.data = data;
		this.barColor = BAR_BLUE;
	}
	public HistogramChart(int[] data, Color barColor){
		super(true);
		this.data = data;
		this.barColor = barColor;
	}
	public HistogramChart(HistogramChart other){
		super(true);
		this.data = other.data;
		this.barColor = other.barColor;
	}
	
	public void setData(int[] data){
		this.data = data;
	}
	public void setBarColor(Color barColor){
		this.barColor = barColor;
	}
	
	public void paint(Graphics g){
		update(g);
	}
	
	public void update(Graphics g){
		if(data != null){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			//Draw Axis
			g.setColor(Color.BLACK);	
			this.drawYAxis(g);
			this.populateChart(g);
			this.drawXAxis(g);
		}else{
			g.clearRect(0, 0, 0, 0);
		}
	}

	int lastPos = 0;
	private void populateChart(Graphics g){
		int textXMin = xAxisPadding;
		int textXMax = this.getWidth() - PADDING;
		int pixelsX = textXMax - textXMin;
		int barWidth = (int)((double)pixelsX / (double)data.length);
		
		lastPos = 0;
		for(int i=0; i<data.length; i++){
			lastPos = (textXMin+(barWidth*i));
			drawBar((textXMin+(barWidth*i)), this.getHeight()-Y_AXIS_PADDING , barWidth, data[i], g);
		}
		
	}
	
	private void drawBar(int x, int y, int width, int heightValue, Graphics g){
		int height = getBarHeight(heightValue);
		Color c = g.getColor();
		g.setColor(barColor);
		g.fillRect(x, y-height, width, height);
		g.setColor(c);
	}
	
	private int getBarHeight(int value){
		for(int i=0; i<yAxisValues; i++){
			int valueMin = (yAxisMinValue+(i*yAxisSpacing));
			int valueMax = (yAxisMinValue+((i+1)*yAxisSpacing));

			if(value == valueMin){
				return (int) (i*yAxisPixelSpacing);
			}else if(value == valueMax){
				return (int) ((i+1)*yAxisPixelSpacing);
			}else if((value > (valueMin)) && (value < (valueMax))){	
				int pixelsMin = (int) ((i*yAxisPixelSpacing));
				int pixelsMax = (int) (((i+1)*yAxisPixelSpacing));

				double pixelHeight = (double)((double)value/(double)valueMax) * (double)pixelsMax;
				return (int)pixelHeight;
			}
		}
		if(value <= this.yAxisMaxValue){
			return (yAxisPixelMin - yAxisPixelMax);
		}
		return -1;
	}
	
	private void drawXAxis(Graphics g){
		Color c = g.getColor();
		
		g.setColor(Color.BLACK);	
		g.drawLine(xAxisPadding, this.getHeight()-Y_AXIS_PADDING, this.getWidth()-PADDING, this.getHeight()-Y_AXIS_PADDING);
		g.setFont(getAxisFont());
		
		
		int textXMin = xAxisPadding;
		int textXMax = this.getWidth() - PADDING;
		int pixelsX = textXMax - textXMin;
		int barWidth = pixelsX / data.length;
		
		int textX = (xAxisPadding + this.getWidth()/2 - getAxisFontWidth("X AXIS", g));
		int textY = (this.getHeight() - Y_AXIS_PADDING + getAxisFontHeight("X AXIS", g) + 10);
		
		g.drawLine(textXMin, this.getHeight()-Y_AXIS_PADDING, textXMin, this.getHeight()-Y_AXIS_PADDING+5);
		g.drawString("0", textXMin-(getAxisFontWidth("0", g)/2), textY);
		
		if(lastPos != 0){
			g.drawLine(lastPos, this.getHeight()-Y_AXIS_PADDING, lastPos, this.getHeight()-Y_AXIS_PADDING+5);
			g.drawString(""+data.length, lastPos-(getAxisFontWidth(""+data.length, g)/2), textY);
		}else{
			g.drawLine(this.getWidth()-PADDING, this.getHeight()-Y_AXIS_PADDING, this.getWidth()-PADDING, this.getHeight()-Y_AXIS_PADDING+5);
			g.drawString(""+data.length, textXMax-(getAxisFontWidth(""+data.length, g)/2), textY);
		}
		
		g.setColor(c);
	}
	private void drawYAxis(Graphics g){
	
		int min = 0;
		int max = 0;
		for(int i=0; i<data.length; i++){
			min = Math.min(min, data[i]);
			max = Math.max(max, data[i]);
		}
		xAxisPadding = Math.max(X_AXIS_PADDING, (PADDING + 5 + this.getAxisFontWidth(""+max, g)));
		
		Color c = g.getColor();
		
		g.setColor(Color.BLACK);	
		g.drawLine(xAxisPadding, this.getHeight()-Y_AXIS_PADDING, xAxisPadding, PADDING);
		g.setFont(getAxisFont());
		
		
		yAxisMinValue = min;
		yAxisMaxValue = max;
		
		int valueToSpace = Math.min(data.length, 10);
		yAxisValues = valueToSpace;
		
		int gap = max - min;
		int spacing = gap / (valueToSpace);
		yAxisSpacing = spacing;
		
		int textX = PADDING;
		int textYMin =  this.getHeight()-Y_AXIS_PADDING;
		yAxisPixelMin = textYMin;
		int textYMax = PADDING;
		yAxisPixelMax = textYMax;
		int textYSpacing = (textYMin - textYMax) / (valueToSpace);
		yAxisPixelSpacing = textYSpacing;
		
		for(int i=0; i<valueToSpace; i++){
			//Draw Label
			g.drawString(""+(min+(spacing*i)), textX, (textYMin-(textYSpacing*i)) + (this.getAxisFontHeight("", g)/2));
			//Draw axis line
			g.drawLine(xAxisPadding-5, textYMin-(textYSpacing*i), xAxisPadding, textYMin-(textYSpacing*i));
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(xAxisPadding, textYMin-(textYSpacing*i), this.getWidth()-PADDING, textYMin-(textYSpacing*i));
			g.setColor(Color.BLACK);
		}
		//Draw Label
		g.drawString(""+max, textX, textYMax + (this.getAxisFontHeight("", g)/2));
		//Draw axis line
		g.drawLine(xAxisPadding-5, textYMax, xAxisPadding, textYMax);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(xAxisPadding, textYMax, this.getWidth()-PADDING, textYMax);
		g.setColor(Color.BLACK);
		  
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
			if(data != null){
				HistogramChart chart = new HistogramChart(this);
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
