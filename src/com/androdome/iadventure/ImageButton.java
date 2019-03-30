package com.androdome.iadventure;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class ImageButton extends JButton implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage currImg = null, imgUp = null, imgDn = null, imgOvr = null,
			imgDs = null;

	public ImageButton(BufferedImage up, BufferedImage down, BufferedImage ovr, BufferedImage ds) {
		currImg = up;
		imgUp = up;
		imgDn = down;
		imgOvr = ovr;
		imgDs = ds;
		addMouseListener(this);
	}

	public ImageButton(String str) {
		try
		{
			imgUp = ImageIO.read(this.getClass().getResourceAsStream(str + "Up.png"));
			imgDn = ImageIO.read(this.getClass().getResourceAsStream(str + "Dn.png"));
			imgOvr = ImageIO.read(this.getClass().getResourceAsStream(str + "Ovr.png"));
			imgDs = ImageIO.read(this.getClass().getResourceAsStream(str + "Ds.png"));
			currImg = imgUp;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(imgOvr != null)
		currImg = imgOvr;

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(imgOvr != null)
		currImg = imgOvr;

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(imgUp != null)
		currImg = imgUp;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(imgDn != null)
		currImg = imgDn;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(imgUp != null)
		currImg = imgUp;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setColor(this.getParent().getBackground());
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if(this.isEnabled())
		{
			if(currImg != null)
				g2d.drawImage(currImg, 0, 0, this.getWidth(), this.getHeight(), this);
			else super.paintComponent(g);
		}
		else
		{
			if(imgDs != null)
				g2d.drawImage(imgDs, 0, 0, this.getWidth(), this.getHeight(), this);
			else super.paintComponent(g);
		}
	}

}
