package views;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.*;

/**
 * <p>This class is used for displaying Images.
 * @author Ashley Allott
 */
public class ImagePreview extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private boolean isFullscreen;
	
	private static int PADDING = 4;
	private static int TEXT_PADDING = 12;
	private boolean keepAspectRatio = true;
	private String caption = null;
	
	private BufferedImage image;
	
	public ImagePreview(){
		super(true);
		this.isFullscreen = false;
	}
	public ImagePreview(String caption){
		super(true);
		this.caption = caption;
		this.isFullscreen = false;
	}
	public ImagePreview(ImagePreview other) {
		super(true);
		this.caption = other.caption;
		this.isFullscreen = other.isFullscreen;
		this.image = other.image;
	}
	
	public void setCaption(String caption){
		this.caption = caption;
	}
	public String getCaption(){
		return caption;
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
	}
	public BufferedImage getImage(){
		return image;
	}
	
	public void paint(Graphics g){
		update(g);
	}
	
	public void update(Graphics g){
		int height, width;
		double resizeFactor = -1;
		if(image!=null){
			if(keepAspectRatio){
				if(image.getHeight() > this.getHeight()){
					height = this.getHeight();
					resizeFactor = (double)this.getHeight() / (double)image.getHeight();
				}else{
					height = image.getHeight();
					resizeFactor = 1.0;
				}
				if((image.getWidth()*resizeFactor) > this.getWidth()){
					width = this.getWidth();
					resizeFactor = (double)this.getWidth() / (double)image.getWidth();
					height = (int)(image.getHeight() * resizeFactor);
				}else{
					width = (int)(image.getWidth() * resizeFactor);
				}
			}else{
				if(image.getHeight() > this.getHeight()){
					height = this.getHeight();
				}else{
					height = image.getHeight();
				}
				if((image.getWidth()) > this.getWidth()){
					width = this.getWidth();
				}else{
					width = image.getWidth();
				}
			}
		}else{
			height = this.getHeight();
			width = this.getWidth();
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(0+PADDING , 0+TEXT_PADDING, this.getWidth() -(2*PADDING), this.getHeight()-(PADDING + TEXT_PADDING));
		if(caption != null){
			g.setColor(Color.WHITE);
			g.drawString(caption, 1, 10);
		}
		if(image!=null){
			g.drawImage(image, 0+PADDING, 0+TEXT_PADDING, width-(2*PADDING), height-(PADDING + TEXT_PADDING), null);
		}
	}
	
	public void openFullScreenImage(){
		if(!isFullscreen){
			if(image != null){
				ImagePreview iP = new ImagePreview(this);
				JFrame imageFrame = new JFrame();
				iP.setPreferredSize(new Dimension(iP.image.getWidth(), iP.image.getHeight()));
				imageFrame.setTitle(iP.caption);
				imageFrame.setVisible(true);
				imageFrame.add(iP);
				imageFrame.pack();
				imageFrame.addWindowListener(new WindowAdapter(){
				    public void windowClosing(WindowEvent e){
				    	imageFrame.remove(iP);
				    	isFullscreen = false;
				    }
				});
				isFullscreen = true;
			}
		}
	}
}
