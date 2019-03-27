package com.androdome.iadventure;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Panel;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.androdome.iadventure.appletutils.ExtendedAppletContext;

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
	ArrayList<Node> nodeBinding = new ArrayList<Node>();
	public ExtendedAppletContext appletContext = null;
	
	public synchronized void addComponentNodeBinding(Component comp, Node node)
	{
		componentBinding.add(comp);
		nodeBinding.add(node);
	}

	public static double JAVA_VERSION = getVersion();
	ConnectionHandler conHandler = new ConnectionHandler();
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
		if(appletContext != null)
			appletContext.dispose();
		appletContext = null;
		this.componentBinding.clear();
		this.nodeBinding.clear();
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
			this.navBar.setText(url.toString());
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

			scrollPane.setViewportView(browser);
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
								Box box = browser.getViewport().getElementBoxByNode(nodeBinding.get(i));
								componentBinding.get(i).setLocation(box.getAbsoluteContentX(), box.getAbsoluteContentY());
								componentBinding.get(i).setSize(box.getMinimalWidth(), box.getHeight());
								componentBinding.get(i).validate();

							}
						}
					};
					invokeLater.start();

				}
			});

			browser.addMouseMotionListener(
					new MouseMotionListener(){

						@Override
						public void mouseDragged(MouseEvent arg0) {
							
						}

						@Override
						public void mouseMoved(MouseEvent arg0) {
							DefaultMutableTreeNode node = HtmlUtils.locateBox(createBoxTree(browser.getViewport()), arg0.getX(), arg0.getY());
							if (node != null)
							{
								Box box = (Box) node.getUserObject();
								if (box.getParent() != null && box.getParent().getNode().getNodeName().equalsIgnoreCase("a"))
								{
									NamedNodeMap attr = box.getParent().getNode().getAttributes();
									if (attr.getNamedItem("href") != null)
									{
										browser.setCursor(new Cursor(Cursor.HAND_CURSOR));
							        } 
								}
								else {
						            browser.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}
							}
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

				}

				@Override
				public void mouseExited(MouseEvent arg0) {

				}

				@Override
				public void mousePressed(MouseEvent arg0) {

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {

				}

			});
		}
		catch (Exception e1)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);
			String sStackTrace = sw.toString();
			pw.close();
			JOptionPane.showMessageDialog(null, "Internet Adventure has crashed!\n\nCrash information:\n\n"+sStackTrace+"\n\nThis will also be in crashinfo.txt", "Crash", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			try
			{
				PrintWriter fw = new PrintWriter(new FileWriter("crashinfo.txt"));
				e1.printStackTrace(fw);
				fw.close();
				System.exit(-1);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			btnHome = new ImageButton("/btn/home");

			Dimension d = new Dimension(32, 32);
			btnHome.setSize(d);
			btnHome.setPreferredSize(d);
			btnHome.setMinimumSize(d);
			btnHome.setMaximumSize(d);
			btnHome.addMouseListener(new MouseListener(){


				@Override
				public void mouseClicked(MouseEvent arg0)
				{
					if(arg0.isShiftDown())
					{
						if(JOptionPane.showConfirmDialog(MainFrame.this, "Do you want to set " + browser.getBaseURL().toString() + " as your home page?", "Set home page", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
						{
							System.out.println("SHE SAID YES!!!");
							PropertyManager.setProperty("home", browser.getBaseURL().toString());
						}
					}
					else conHandler.navigate(MainFrame.this, "about:home");
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
			btnNavigate = new ImageButton("/btn/nav");
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
