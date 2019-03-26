package com.androdome.wrapplet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.UIManager;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.demo.DOMSource;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class MainFrame extends JFrame{

	/**
	 * 
	 */

	JProgressBar progressBar = new JProgressBar();
	ArrayList<Component> componentBinding = new ArrayList<Component>();
	ArrayList<Node> boxBinding = new ArrayList<Node>();

	public static double JAVA_VERSION = getVersion();
	ConnectionHandler conHandler = new ConnectionHandler();
	static double getVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	JPanel panel = new JPanel();

	ScrollPane scrollPane = new ScrollPane();
	private static final long serialVersionUID = 1L;
	private Panel contentPane;
	JTextField navBar;
	JLabel lblProg = new JLabel("Done.");
	BrowserCanvas browser = null;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) {
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
		frame.init();
	}

	void clearComp() {
		Thread.currentThread().setContextClassLoader(null);
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

	private DefaultMutableTreeNode createBoxTree(Box root) {
		DefaultMutableTreeNode ret = new DefaultMutableTreeNode(root);
		if (root instanceof ElementBox)
		{
			ElementBox el = (ElementBox) root;
			for (int i = el.getStartChild(); i < el.getEndChild(); i++)
			{
				ret.add(createBoxTree(el.getSubBox(i)));
			}
		}
		return ret;
	}

	private void init() {

		try
		{
			URL url = new URL("about:welcome");

			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();

			// Parse the input document (replace this with your own parser)
			DOMSource parser = new DOMSource(is);
			Document doc = parser.parse();

			DOMAnalyzer da = new DOMAnalyzer(doc);
			da.attributesToStyles(); // convert the HTML presentation attributes
										// to
										// inline styles
			da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the standard
																// style sheet
			da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the
																// additional
																// style sheet
			da.getStyleSheets();

			browser = new BrowserCanvas(da.getRoot(), da, scrollPane.getSize(), url);
			browser.setLayout(null);
			// browser.getViewport();
			// browser.createLayout(new java.awt.Dimension(30,30));
			// scrollPane.setBorder(new EtchedBorder());

			scrollPane.add(browser);
			addComponentListener(new ComponentAdapter() {
				InvokeLaterThread invokeLater;

				public void componentResized(ComponentEvent componentEvent) {
					if (invokeLater != null)
					{
						invokeLater.cancel();
					}
					// browser.setSize();
					invokeLater = new InvokeLaterThread(200) {

						@Override
						public void onInvokeLater() {
							browser.createLayout(scrollPane.getSize());
							for (int i = 0; i < componentBinding.size(); i++)
							{
								Box box = browser.getViewport().getElementBoxByNode(boxBinding.get(i));
								componentBinding.get(i).setLocation(box.getAbsoluteContentX(), box.getAbsoluteContentY());
								componentBinding.get(i).setSize(box.getMinimalWidth(), box.getHeight());
								componentBinding.get(i).validate();

							}
						}
					};
					invokeLater.start();

				}
			});

			browser.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					DefaultMutableTreeNode node = HtmlUtils.locateBox(createBoxTree(browser.getViewport()), arg0.getX(), arg0.getY());
					if (node != null)
					{
						Box box = (Box) node.getUserObject();
						if (box.getParent() != null && box.getParent().getNode().getNodeName().equalsIgnoreCase("a"))
						{
							System.out.println("Yay!");
							NamedNodeMap attr = box.getParent().getNode().getAttributes();
							if (attr.getNamedItem("href") != null)
							{
								conHandler.navigate(MainFrame.this, browser.getBaseURL(), attr.getNamedItem("href").getNodeValue());
							}
						}
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

			});
		}
		catch (MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void iterateChildren(ElementBox box) {
		for (int i = box.getStartChild(); i < box.getEndChild(); i++)
		{
			System.out.println(box.getSubBox(i));
			if (box.getSubBox(i) instanceof ElementBox)
			{
				iterateChildren((ElementBox) box.getSubBox(i));
			}
		}
	}

	public void iterateNodes(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			System.out.println(nodes.item(i).getNodeName());
			iterateNodes(nodes.item(i));
		}
	}

	public void parseApplets(BrowserCanvas browser) throws MalformedURLException, DOMException {
		ArrayList<ElementBox> boxes = browser.getViewport().getElementsBoxByName("applet", false);
		if (boxes == null)
			return;
		for (int x = 0; x < boxes.size(); x++)
		{
			Box box = boxes.get(x);
			NodeList nodes = box.getNode().getChildNodes();
			HashMap<String, String> params = new HashMap<String, String>();
			for (int i = 0; i < nodes.getLength(); i++)
			{
				if (nodes.item(i).getNodeName().equalsIgnoreCase("param"))
				{
					params.put(nodes.item(i).getAttributes().getNamedItem("name").getNodeValue(), nodes.item(i).getAttributes().getNamedItem("value").getNodeValue());
				}
			}

			String cbVal = ".";
			if (box.getNode().getAttributes().getNamedItem("codebase") != null)
				cbVal = box.getNode().getAttributes().getNamedItem("codebase").getNodeValue();
			URL cbStarter = new URL(browser.getBaseURL(), cbVal);
			String cb = cbStarter.toString();
			JPanel appletContainer = new JPanel();
			appletContainer.setLayout(new BorderLayout());

			// int width =
			// Integer.parseInt(box.getNode().getAttributes().getNamedItem("width").getNodeValue());

			// int height =
			// Integer.parseInt(box.getNode().getAttributes().getNamedItem("height").getNodeValue());
			appletContainer.setLocation(box.getAbsoluteContentX(), box.getAbsoluteContentY());

			appletContainer.setSize(box.getMinimalWidth(), box.getHeight());
			URL[] arUrl;
			if (box.getNode().getAttributes().getNamedItem("archive") != null)
			{
				String[] ar = box.getNode().getAttributes().getNamedItem("archive").getNodeValue().replace(" ", "").split(",");
				arUrl = new URL[ar.length];
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
			}
			else arUrl = new URL[] { cbStarter };
			// System.out.println(cb);
			Node nameNode = box.getNode().getAttributes().getNamedItem("name");
			String name = "Unnamed Applet";
			if (nameNode != null)
				name = nameNode.getNodeValue();
			String code = box.getNode().getAttributes().getNamedItem("code").getNodeValue();
			if (code.endsWith(".class"))
				code = code.substring(0, code.length() - ".class".length());
			System.out.println(code);

			if (nameNode == null)
			{
				String pName = "";
				if (!code.contains("."))
					pName = code;
				else
				{
					pName = code.split("\\.")[code.split("\\.").length - 1];
				}
				name = pName;
			}
			Applet applet = getApplet(name, arUrl, code, params, cb);
			appletContainer.add(applet);
			this.componentBinding.add(appletContainer);
			this.boxBinding.add(box.getNode());
			browser.add(appletContainer);
		}
		browser.redrawBoxes();
		browser.revalidate();
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {

		try
		{
			setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/icon32.png")));
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setTitle("Internet Adventure");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 434);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnBookmarks = new JMenu("Bookmarks");
		menuBar.add(mnBookmarks);
		contentPane = new Panel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		Panel panel_1 = new Panel();
		contentPane.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		panel_1.add(toolBar, BorderLayout.CENTER);

		JButton btnHome;

		try
		{
			btnHome = new ImageButton(ImageIO.read(this.getClass().getResourceAsStream("/btn/home.png")), ImageIO.read(this.getClass().getResourceAsStream("/btn/homeDn.png")), ImageIO.read(this.getClass().getResourceAsStream("/btn/homeOvr.png")));

			Dimension d = new Dimension(32, 32);
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
		toolBar_1.setBorder(new EmptyBorder(2, 2, 2, 2));
		panel_1.add(toolBar_1, BorderLayout.SOUTH);

		JLabel lblAddress = new JLabel("Address:");
		toolBar_1.add(lblAddress);
		toolBar_1.addSeparator();
		navBar = new JTextField();
		toolBar_1.add(navBar);

		toolBar_1.addSeparator();
		navBar.setColumns(10);

		JButton btnNavigate;
		try
		{
			btnNavigate = new ImageButton(ImageIO.read(this.getClass().getResourceAsStream("/btn/navUp.png")), ImageIO.read(this.getClass().getResourceAsStream("/btn/navDn.png")), ImageIO.read(this.getClass().getResourceAsStream("/btn/navOvr.png")));
			btnNavigate.setSize(32, 32);
			Dimension d = new Dimension(22, 22);
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
				String url = navBar.getText().trim();
				conHandler.navigate(MainFrame.this, url);

			}

		});

		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(2, 2, 2, 2));
		contentPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));

		progressBar.setPreferredSize(new Dimension(120, 18));
		panel_2.add(progressBar, BorderLayout.EAST);

		panel_2.add(lblProg, BorderLayout.WEST);

		panel.setBorder(null);
		panel.setBackground(Color.WHITE);
		// scrollPane.setViewportView(panel);
		panel.setLayout(new BorderLayout(0, 0));
	}

	public Applet createApplet(String className, ClassLoader classLoader) throws UnsupportedClassVersionError {
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

	// TODO
	public Process getAppletSeperateJVM(final String name, final URL[] archives, final String className, HashMap<String, String> params, final String codeBase) {

		String jvm_location;
		if (System.getProperty("os.name").startsWith("Win"))
		{
			jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
		}
		else
		{
			jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		}
		String procArgs = "\"" + jvm_location + "\" -jar SeperatedApplet.jar";
		procArgs += " \"classname:" + className + "\"";
		procArgs += " \"name:" + name + "\"";
		procArgs += " \"codebase:" + codeBase + "\"";
		for (int i = 0; i < archives.length; i++)
		{
			procArgs += " \"archive:" + archives[i] + "\"";
		}
		Object[] keys = params.keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{
			procArgs += " \"param:" + keys[i] + "\"";
			procArgs += " \"value:" + params.get(keys[i]) + "\"";
		}
		try
		{
			Process proc = Runtime.getRuntime().exec(procArgs);
			return proc;
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return null;
		}
	}

	public Applet getApplet(final String name, final URL[] archives, final String className, HashMap<String, String> params, final String codeBase) {

		System.out.println(codeBase);
		final Launcher launcher = new Launcher();
		launcher.setMessage("Getting codebase");
		launcher.codebase = codeBase;
		launcher.setMessage("Getting parameters");
		launcher.setParams(params);
		launcher.setMessage("Waiting for permission");
		launcher.startThread();
		Thread th = new Thread() {
			public void run() {
				
				AppletAcceptDialog dialog = new AppletAcceptDialog(name, archives, className, codeBase);
				dialog.setVisible(true);
				while (dialog.dialogResult == 0)
					try
					{
						if (dialog.isSiteTrusted(codeBase))
						{
							dialog.dialogResult = AppletAcceptDialog.DIALOG_RUN;
							dialog.dispose();
						}
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
				launcher.setMessage("Updating classloader");
				launcher.setProgress(20);
				URLClassLoader loader = new URLClassLoader(archives, null);

				launcher.setProgress(40);

				launcher.setMessage("Swapping context");
				Thread.currentThread().setContextClassLoader(loader);
				launcher.setProgress(50);
				try
				{

					launcher.setMessage("Fetching applet");
					launcher.setProgress(60);
					Applet applet = createApplet(className, loader);
					launcher.setMessage("Swapping applet");
					launcher.setProgress(70);
					launcher.replace(applet);
				}
				catch (UnsupportedClassVersionError er)
				{
					String errorString = er.getMessage();
					launcher.setCancel(errorString);

					er.printStackTrace();
				}
			}
		};
		th.start();

		return launcher;
	}
}

abstract class InvokeLaterThread extends Thread {
	protected boolean isCancelled = false;
	int wait;

	public InvokeLaterThread(int millis) {
		wait = millis;
	}

	public void cancel() {
		isCancelled = true;
		this.interrupt();
	}

	public void run() {
		try
		{
			Thread.sleep(wait);
			if (!isCancelled)
				onInvokeLater();
		}
		catch (InterruptedException e)
		{
		}
	}

	public abstract void onInvokeLater();
}
