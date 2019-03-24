package com.androdome.wrapplet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.UIManager;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.demo.DOMSource;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	ArrayList<Component> componentBinding = new ArrayList<Component>();
	ArrayList<Box> boxBinding = new ArrayList<Box>();
	
	public static double JAVA_VERSION = getVersion();

	static double getVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	JPanel panel = new JPanel();

	JScrollPane scrollPane = new JScrollPane();
	private static final long serialVersionUID = 1L;
	private Panel contentPane;
	private JTextField textField;

	BrowserCanvas browser = null;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws IOException, SAXException {
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final MainFrame frame = new MainFrame();
		frame.setVisible(true);
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("al_title", "appletloadertest");
		params.put("al_main", "com.mojang.minecraft.MinecraftApplet");
		params.put("al_jars", "version/0.0.11a_02.jar, lwjgl.jar.pack.lzma, jinput.jar.pack.lzma, lwjgl_util.jar.pack.lzma");
		params.put("al_windows", "windows_natives.jar.lzma");
		params.put("al_linux", "linux_natives.jar.lzma");
		params.put("al_mac", "macosx_natives.jar.lzma");
		params.put("al_solaris", "solaris_natives.jar.lzma");
		params.put("al_version", "0.0.11a_02");
		params.put("boxbgcolor", "#000000");
		params.put("boxfgcolor", "#ffffff");
		params.put("separate_jvm", "true");

		// Open the network connection
		frame.init();
		// frame.getApplet("LWJGL Applet", new URL[]{new
		// URL("http://androdome.com/MPR/Applet/lwjgl_util_applet.jar"), new
		// URL("http://androdome.com/MPR/Applet/lzma.jar")},
		// "org.lwjgl.util.applet.AppletLoader", params,
		// "http://androdome.com/MPR/Applet/");
	}
	
	private void navigateError(String string, String errHtml) {
		try{
		clearComp();
		URL url = new URL(string);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();
		String page = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String read;
		while((read = reader.readLine()) != null)
			page += read;
		reader.close();
		page = page.replace("$er", errHtml);
		// Parse the input document (replace this with your own parser)
		DOMSource parser = new DOMSource(new ByteArrayInputStream(page.getBytes("UTF-8")));
		Document doc = parser.parse();

		DOMAnalyzer da = new DOMAnalyzer(doc, url);
		da.attributesToStyles(); // convert the HTML presentation attributes to
									// inline styles
		da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the standard
															// style sheet
		da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the additional
															// style sheet
		da.getStyleSheets(); // load the author style sheets
		// scrollPane.removeAll();
		browser.navigate(da.getRoot(), da, new java.awt.Dimension(scrollPane.getWidth(), scrollPane.getHeight()), url);
		parseApplets(browser);
		// scrollPane.setViewportView(browser);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			try
			{
				navigate(new URL("about:econfailed"));
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SAXException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void navigate(URL url) throws SAXException, IOException {
		clearComp();
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		// Parse the input document (replace this with your own parser)
		DOMSource parser = new DOMSource(is);
		Document doc = parser.parse();

		DOMAnalyzer da = new DOMAnalyzer(doc, url);
		da.attributesToStyles(); // convert the HTML presentation attributes to
									// inline styles
		da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the standard
															// style sheet
		da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the additional
															// style sheet
		da.getStyleSheets(); // load the author style sheets
		// scrollPane.removeAll();
		browser.navigate(da.getRoot(), da, new java.awt.Dimension(scrollPane.getWidth(), scrollPane.getHeight()), url);
		parseApplets(browser);
		// scrollPane.setViewportView(browser);
	}

	private void clearComp() {
		Component[] comps = browser.getComponents();
		for (int i = 0; i < comps.length; i++)
		{
			if (comps[i] instanceof Applet)
			{
				Applet app = (Applet) comps[i];
				app.stop();
				app.destroy();
			}
		}
		this.componentBinding.clear();
		this.boxBinding.clear();
		browser.removeAll();
		System.gc();
	}

	private void init() throws SAXException, IOException {
		URL.setURLStreamHandlerFactory(new ConfigurableStreamHandlerFactory("about", new Handler()));
		URL url = new URL("about:welcome");
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		// Parse the input document (replace this with your own parser)
		DOMSource parser = new DOMSource(is);
		Document doc = parser.parse();

		DOMAnalyzer da = new DOMAnalyzer(doc);
		da.attributesToStyles(); // convert the HTML presentation attributes to
									// inline styles
		da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the standard
															// style sheet
		da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the additional
															// style sheet
		da.getStyleSheets();

		browser = new BrowserCanvas(da.getRoot(), da, scrollPane.getViewport().getSize(), url);
		browser.setLayout(null);
		// browser.getViewport();
		//browser.createLayout(new java.awt.Dimension(30,30));
		scrollPane.setViewportView(browser);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				//browser.setSize();
				browser.updateLayout(scrollPane.getSize());
				for(int i = 0; i < componentBinding.size(); i++)
				{
					componentBinding.get(i).setLocation(boxBinding.get(i).getAbsoluteContentX(), boxBinding.get(i).getAbsoluteContentY());
					componentBinding.get(i).setSize(boxBinding.get(i).getMinimalWidth(),boxBinding.get(i).getHeight());
					componentBinding.get(i).validate();
					
				}
			}
		});
		parseApplets(browser);
	}
	
	public void parseApplets(BrowserCanvas browser) {
		Box box = browser.getViewport().getElementBoxByName("applet", false);
		if (box == null)
			return;
		NodeList nodes = box.getNode().getChildNodes();
		HashMap<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			if (nodes.item(i).getNodeName().equalsIgnoreCase("param"))
			{
				params.put(nodes.item(i).getAttributes().getNamedItem("name").getNodeValue(), nodes.item(i).getAttributes().getNamedItem("value").getNodeValue());
			}
		}

		String cbStarter = browser.getBaseURL().toString().substring(0, browser.getBaseURL().toString().lastIndexOf('/') + 1);
		String cb = cbStarter;
		JPanel appletContainer = new JPanel();
		appletContainer.setLayout(new BorderLayout());

		//int width = Integer.parseInt(box.getNode().getAttributes().getNamedItem("width").getNodeValue());

		//int height = Integer.parseInt(box.getNode().getAttributes().getNamedItem("height").getNodeValue());
		appletContainer.setLocation(box.getAbsoluteContentX(), box.getAbsoluteContentY());

		appletContainer.setSize(box.getMinimalWidth(), box.getHeight());
		
		String[] ar = box.getNode().getAttributes().getNamedItem("archive").getNodeValue().replace(" ", "").split(",");
		URL[] arUrl = new URL[ar.length];
		for (int i = 0; i < ar.length; i++)
		{
			try
			{
				arUrl[i] = new URL(cb + ar[i]);
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// System.out.println(cb);
		Node nameNode = box.getNode().getAttributes().getNamedItem("name");
		String name = "Unnamed Applet";
		if (nameNode != null)
			name = nameNode.getNodeValue();
		Applet applet = getApplet(name, arUrl, box.getNode().getAttributes().getNamedItem("code").getNodeValue(), params, cb);
		appletContainer.add(applet);
		this.componentBinding.add(appletContainer);
		this.boxBinding.add(box);
		browser.add(appletContainer);
		browser.redrawBoxes();
		browser.revalidate();
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {

		ArrayList<Image> icons = new ArrayList<Image>();
		try
		{
			icons.add(ImageIO.read(this.getClass().getResourceAsStream("/icon16.png")));
			icons.add(ImageIO.read(this.getClass().getResourceAsStream("/icon32.png")));
			icons.add(ImageIO.read(this.getClass().getResourceAsStream("/icon64.png")));
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setIconImages(icons);

		setTitle("Internet Adventure");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 434);
		contentPane = new Panel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		Panel panel_1 = new Panel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		panel_1.add(toolBar, BorderLayout.NORTH);

		JButton btnHome;
		
		try
		{
			btnHome = new ImageButton(ImageIO.read(this.getClass().getResourceAsStream("/btn/home.png")),
					ImageIO.read(this.getClass().getResourceAsStream("/btn/homeDn.png")),
					ImageIO.read(this.getClass().getResourceAsStream("/btn/homeOvr.png")));

			Dimension d = new Dimension(32,32);
			btnHome.setSize(d);
			btnHome.setPreferredSize(d);
			btnHome.setMinimumSize(d);
			btnHome.setMaximumSize(d);
		}
		catch (Exception e3)
		{
			btnHome = new JButton("Home");
			e3.printStackTrace();
		}
		
		toolBar.add(btnHome);
		
		JToolBar toolBar_1 = new JToolBar();
		panel_1.add(toolBar_1, BorderLayout.SOUTH);
		
				JLabel lblAddress = new JLabel("Address:");
				toolBar_1.add(lblAddress);
				toolBar_1.addSeparator();
						textField = new JTextField();
						toolBar_1.add(textField);

						toolBar_1.addSeparator();
						textField.setColumns(10);
						
								JButton btnNavigate;
								try
								{
									btnNavigate = new ImageButton(ImageIO.read(this.getClass().getResourceAsStream("/btn/navUp.png")),
											ImageIO.read(this.getClass().getResourceAsStream("/btn/navDn.png")),
											ImageIO.read(this.getClass().getResourceAsStream("/btn/navOvr.png")));
									btnNavigate.setSize(32, 32);
									Dimension d = new Dimension(22,22);
									btnNavigate.setPreferredSize(d);
									btnNavigate.setMinimumSize(d);
									btnNavigate.setMaximumSize(d);
								}
								catch (Exception e3)
								{
									btnNavigate = new JButton("Navigate");
									e3.printStackTrace();
								}
								toolBar_1.add(btnNavigate);
								btnNavigate.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										try
										{
											URL url = new URL(textField.getText().trim());
											navigate(url);
										}
										catch (MalformedURLException e1)
										{
											StringWriter sw = new StringWriter();
											PrintWriter pw = new PrintWriter(sw);
											e1.printStackTrace(pw);
											String sStackTrace = sw.toString(); // stack trace as a string
											System.out.println(sStackTrace);
											navigateError("about:eurlmalform", "<br><br>The address <a href='"+textField.getText().trim().replace("'", "\\'")+"'>" + textField.getText().trim() + 
													"</a><br>does not match any supported protocol."
													+ "<br>It may have been mistyped."
													+ "<br>Please ensure that the protocol is supported "
													+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
													+ "Java stack trace:"
													+"<br />&nbsp;<br />&nbsp;<br />"
													+HtmlUtils.stringToHTMLString(sStackTrace));
											//JOptionPane.showMessageDialog(null, "URL Is malformed:\n" + e1, "URL Malformed", JOptionPane.ERROR_MESSAGE);
										}
										catch (SAXException e1)
										{
											// TODO Auto-generated catch block
											//JOptionPane.showMessageDialog(null, "Failed to parse:\n" + e1, "Parse Error", JOptionPane.ERROR_MESSAGE);
										}
										catch (IOException e1)
										{
											try
											{
												navigate(new URL("about:econfailed"));
											}
											catch (Exception e2)
											{}
										}

									}

									
								});

		contentPane.add(scrollPane, BorderLayout.CENTER);

		panel.setBorder(null);
		panel.setBackground(Color.WHITE);
		// scrollPane.setViewportView(panel);
		panel.setLayout(new BorderLayout(0, 0));
	}

	public Applet createApplet(String className, ClassLoader classLoader) {
		try
		{
			Class<?> appletClass = classLoader.loadClass(className);

			return (Applet) appletClass.newInstance();
		}
		catch (Exception ex)
		{
			return null;

		}

	}

	public Applet getApplet(final String name, final URL[] archives, final String className, HashMap<String, String> params, final String codeBase) {

		System.out.println(codeBase);
		final Launcher launcher = new Launcher();
		launcher.codebase = codeBase;
		launcher.customParameters = params;
		launcher.startThread();
		new Thread() {
			public void run() {
				URLClassLoader loader = new URLClassLoader(archives);
				
				AppletAcceptDialog dialog = new AppletAcceptDialog(name, archives, className, codeBase);
				dialog.setVisible(true);
				while (dialog.dialogResult == 0)
					try
					{
						Thread.sleep(100L);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if (dialog.dialogResult == AppletAcceptDialog.DIALOG_CANCEL)
				{
					launcher.setCancel();
					return;
				}
				Thread.currentThread().setContextClassLoader(loader);
				Applet applet = createApplet(className, loader);
				launcher.replace(applet);
			}
		}.start();

		return launcher;
	}

}
