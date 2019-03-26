package com.androdome.iadventure;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

public class ImageButton extends JButton implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage currImg, imgUp, imgDown, imgOvr;
	public ImageButton(BufferedImage up, BufferedImage down, BufferedImage ovr) {
		currImg = up;
		imgUp = up;
		imgDown = down;
		imgOvr = ovr;
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		currImg = imgOvr;
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		currImg = imgOvr;
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		currImg = imgUp;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		currImg = imgDown;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		currImg = imgUp;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(this.getParent().getBackground());
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		g.drawImage(currImg, 0,0,this.getWidth(),this.getHeight(),this);
	}

}
