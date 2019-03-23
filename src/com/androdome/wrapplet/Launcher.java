package com.androdome.wrapplet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

public class Launcher extends Applet implements AppletStub {
	private static final long serialVersionUID = 1L;
	public Map<String, String> customParameters = new HashMap<String, String>();
	private Applet applet;
	String codebase;
	boolean cancelled = false;
	private BufferedImage[] loadicon = null;
	private BufferedImage loadjava = null;
	int icoindx = 0;

	public Launcher() {
		setLayout(new BorderLayout(0, 0));
		try
		{
			loadicon = new BufferedImage[24];
			for (int i = 0; i < 24; i++)
			{
				this.loadicon[i] = ImageIO.read(Launcher.class.getResourceAsStream("/" + (i + 1) + ".png"));
				
			}
			this.loadjava = ImageIO.read(Launcher.class.getResourceAsStream("/wrapplet2.png"));

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.validate();
	}

	public AppletContext getAppletContext() {
		return new AppletContext() {

			public AudioClip getAudioClip(URL url) {
				return null;
			}

			public Image getImage(URL url) {
				// TODO Auto-generated method stub
				try
				{
					System.out.println(url);
					return ImageIO.read(url);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			public Applet getApplet(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			public Enumeration<Applet> getApplets() {
				// TODO Auto-generated method stub
				return null;
			}

			public void showDocument(URL url) {
				// TODO Auto-generated method stub
				
			}

			public void showDocument(URL url, String target) {
				// TODO Auto-generated method stub
				
			}

			public void showStatus(String status) {
				// TODO Auto-generated method stub
				
			}

			public void setStream(String key, InputStream stream) throws IOException {
				// TODO Auto-generated method stub
				
			}

			public InputStream getStream(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			public Iterator<String> getStreamKeys() {
				// TODO Auto-generated method stub
				return null;
			}

		

		};
	}

	public void init() {
		if (applet != null)
		{
			applet.init();
			return;
		}
		else System.exit(-15235729);
	}

	public void start() {
		if (applet != null)
		{
			applet.start();
			return;
		}
	}

	public void stop() {
		if (applet != null)
		{
			applet.stop();
			return;
		}
	}

	public void destroy() {
		if (applet != null)
		{
			applet.destroy();
			return;
		}
	}

	public void replace(Applet applet) {
		this.applet = applet;

		applet.setStub(this);
		applet.setSize(getWidth(), getHeight());

		setLayout(new BorderLayout());
		add(applet, "Center");

		applet.init();
		applet.start();
		validate();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g.create();
		g.setColor(this.getBackground());
		if(!cancelled)
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int size1 = 300;
			int size2 = 200;
			g2d.drawImage(this.loadicon[icoindx], this.getWidth()/2-size1/2, this.getHeight()/2-size1/2, size1, size1, this);
			g2d.drawImage(this.loadjava, this.getWidth()/2-size2/2, this.getHeight()/2-size2/2, size2, size2, this);
		}
		else g.drawRect(0, 0, getWidth(), getHeight());
	}

	public void startThread() {
		new Thread() {
			public void run() {
				try
				{
					while(applet == null && cancelled == false)
					{
					Thread.sleep(81L);
					icoindx++;
					if (icoindx > 23)
						icoindx = 0;
					repaint();
					}
				}
				catch(Exception ex){}
				
			}
		}.start();
	}

	/*
	 * public void paint(Graphics g2) { if (applet != null) {
	 * 
	 * applet.paint(g2); return; } }
	 */
	public URL getCodeBase() {
		try
		{
			return new URL(this.codebase);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String getParameter(String name) {
		String custom = (String) customParameters.get(name);
		if (custom != null)
		{
			return custom;
		}
		try
		{
			return super.getParameter(name);
		}
		catch (Exception e)
		{
			customParameters.put(name, null);
		}
		return null;
	}

	public void appletResize(int width, int height) {
	}

	public URL getDocumentBase() {
		try
		{
			return new URL(this.codebase);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void setCancel() {
		cancelled = true;
		Label label = new Label("Applet loading cancelled");
		label.setForeground(Color.red);
		this.add(label, BorderLayout.CENTER);
		this.validateTree();
		this.repaint();
		this.validate();
	}
}
