package com.androdome.iadventure.appletutils;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.androdome.iadventure.HtmlUtils;
import com.androdome.iadventure.MainFrame;

public class AppletManager {

	public static Applet createApplet(String className, ClassLoader classLoader) throws UnsupportedClassVersionError {
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
	public static Process getAppletSeperateJVM(final String name, final URL[] archives, final String className, HashMap<String, String> params, final String codeBase) {

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
	
	public static Applet getApplet(final String name, final URL[] archives, final String className, HashMap<String, String> params, final String codeBase, final ExtendedAppletContext context) {

		System.out.println(codeBase);
		final Wrapplet wrapplet = new Wrapplet();
		wrapplet.setAppletContext(context);
		wrapplet.setMessage("Getting codebase");
		wrapplet.codebase = codeBase;
		wrapplet.setMessage("Getting parameters");
		wrapplet.setParams(params);
		wrapplet.setMessage("Waiting for permission");
		wrapplet.startThread();
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
						e.printStackTrace();
					}
				if (dialog.dialogResult == AppletAcceptDialog.DIALOG_CANCEL)
				{
					wrapplet.setCancel();
					return;
				}
				wrapplet.setMessage("Updating classloader");
				wrapplet.setProgress(20);
				URLClassLoader loader = new URLClassLoader(archives, null);

				wrapplet.setProgress(40);

				wrapplet.setMessage("Swapping context");
				Thread.currentThread().setContextClassLoader(loader);
				wrapplet.setProgress(50);
				try
				{

					wrapplet.setMessage("Fetching applet");
					wrapplet.setProgress(60);
					Applet applet = createApplet(className, loader);
					wrapplet.setMessage("Swapping applet");
					wrapplet.setProgress(70);
					context.putApplet(name, applet);
					wrapplet.setApplet(applet);
				}
				catch (UnsupportedClassVersionError er)
				{
					String errorString = er.getMessage();
					wrapplet.setCancel(errorString);

					er.printStackTrace();
				}
			}
		};
		th.start();

		return wrapplet;
	}
	
	
	
	public static void parseApplets(BrowserCanvas browser, MainFrame frame){
		frame.appletContext = new ExtendedAppletContext();
		ArrayList<ElementBox> boxes = browser.getViewport().getElementsBoxByName("applet", false);
		if (boxes == null)
			return;
		for (int x = 0; x < boxes.size(); x++)
		{
			try{
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
			URL cbStarter = browser.getBaseURL().toURI().resolve(cbVal).toURL();
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
			Applet applet = getApplet(name, arUrl, code, params, cb, frame.appletContext);
			appletContainer.add(applet);
			frame.addComponentNodeBinding(appletContainer, box.getNode());
			browser.add(appletContainer);
			}
			catch (Exception e1)
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e1.printStackTrace(pw);
				String sStackTrace = sw.toString();
				Box box = boxes.get(x);
				JPanel errorBox = new JPanel();
				errorBox.setLayout(new BorderLayout());
				errorBox.setLocation(box.getAbsoluteContentX(), box.getAbsoluteContentY());
				errorBox.setSize(box.getMinimalWidth(), box.getHeight());
				JLabel errorMsg = new JLabel("<html>An error has occured while trying to parse this applet:<br>"+
						HtmlUtils.stringToHTMLString(sStackTrace)+ "</html>");
				errorMsg.setHorizontalAlignment(JLabel.CENTER);
				errorBox.add(errorMsg);
				frame.addComponentNodeBinding(errorBox, box.getNode());
				browser.add(errorBox);
				e1.printStackTrace();
			}
		}
		browser.redrawBoxes();
		browser.revalidate();
	}

}
