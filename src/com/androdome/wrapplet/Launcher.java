package com.androdome.wrapplet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
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
	private Map<String, String> customParameters = new HashMap<String, String>();
	private Applet applet;
	String codebase;
	boolean cancelled = false;
	private static Image loadicon = null;
	private static Image loadjava = null;
	int icoindx = 0;
	
	

	public void setParams(Map<String, String> param)
	{
		this.customParameters = param;
		if(customParameters.get("boxbgcolor") != null)
		{
			try{
				this.setBackground(Color.decode(customParameters.get("boxbgcolor")));
			}catch(Throwable ex){}
		}
		if(customParameters.get("boxfgcolor") != null)
		{
			try{
				this.setForeground(Color.decode(customParameters.get("boxfgcolor")));
			}catch(Throwable ex){}
		}
	}
	static
	{
		try
		{
			//loadicon = new Image[24];
			//for (int i = 0; i < 24; i++)
			//{
				loadicon = ImageIO.read(Launcher.class.getResourceAsStream("/appletloader/loading/circ.png"));

			//}
			loadjava = ImageIO.read(Launcher.class.getResourceAsStream("/appletloader/loading/wrapplet2.png"));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Launcher() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0, 0));
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
		setMessage("Initializing applet");
		setProgress(80);
		this.applet = applet;

		setMessage("Setting stub");
		setProgress(90);
		if(applet == null)
			this.setCancel("Applet returned null");
		applet.setStub(this);
		applet.setSize(getWidth(), getHeight());

		setLayout(new BorderLayout());
		add(applet, "Center");

		setMessage("Starting");
		setProgress(100);
		applet.init();
		applet.start();
		validate();
	}

	public void update(Graphics g) {
		paint(g);
	}
	private int prog = 0;
	String message = "Waiting for permission";
	
	public void setProgress(int prog)
	{
		this.prog = prog;
		this.repaint();
	}
	public void setMessage(String message)
	{
		this.message = message;
		this.repaint();
	}
	
	Image image;
	public void paint(Graphics g) {
		Graphics2D g2d;
		if (image == null) {
	        image = createImage(this.getWidth(), this.getHeight());
	        
	    }
		g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(this.getBackground());
		if (!cancelled)
		{
			g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int size1 = (int) (Math.min(this.getWidth(), this.getHeight()-40) * 0.5F);
			int size2 = (int) (size1 * (2.00000D / 3.00000D));
			g2d.translate(this.getWidth() / 2, this.getHeight() / 2-40);
			g2d.setColor(this.getForeground());
			g2d.drawString(message, -g2d.getFontMetrics().stringWidth(message)/2, size1/2+32);
			g2d.drawRect(-size1/2, size1/2+42, size1, 10);
			g2d.fillRect(-size1/2, size1/2+42, (int) ((size1/100.0)*prog), 10);
			g2d.setColor(this.getBackground());
			g2d.rotate(Math.toRadians(this.icoindx));
			g2d.drawImage(Launcher.loadicon, -size1 / 2, -size1 / 2, size1, size1, this);
			g2d.rotate(Math.toRadians(-this.icoindx));
			drawLines(g2d, 16, size1);
			g2d.drawImage(Launcher.loadjava, -size2 / 2, -size2 / 2, size2, size2, this);
			
			g.drawImage(image, 0,0,this.getWidth(), this.getHeight(), this);
		}
		else g.drawRect(0, 0, getWidth(), getHeight());
	}
	
	public void drawLines(Graphics2D g2d, int count, int size1)
	{
		double thicc = size1*0.06;
		double inc = (double)360/(double)count;
		for(int i = 0; i < count; i++)
		{
			
			g2d.clearRect((int)-thicc/2,-size1/2,(int)thicc,size1);
			g2d.rotate(Math.toRadians(inc));
		}
		thicc = size1*0.08;
		g2d.fillOval((int)(-size1/2+thicc),(int)(-size1/2+thicc), (int) (size1-thicc*2),(int)(size1-thicc*2));
		//g2d.rotate(Math.toRadians(inc));
	}

	public void startThread() {
		new Thread() {
			public void run() {
				try
				{
					while (applet == null && cancelled == false)
					{
						Thread.sleep(30L);
						icoindx+=3;
						if (icoindx >= 360)
							icoindx = 0;
						repaint();
					}
				}
				catch (Exception ex)
				{
				}

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
		setCancel("Applet loading cancelled");
	}

	public void setCancel(String string) {
		cancelled = true;
		Label label = new Label(string);
		label.setAlignment(Label.CENTER);
		label.setForeground(Color.red);
		this.add(label);
		this.repaint();
		this.validate();
		
	}
}
