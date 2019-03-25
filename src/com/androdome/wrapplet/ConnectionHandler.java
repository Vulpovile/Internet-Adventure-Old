package com.androdome.wrapplet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.demo.DOMSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConnectionHandler
{

	public static void navigateError(MainFrame frame, String string,
			String errHtml) {
		try
		{
			frame.clearComp();
			URL url = new URL(string);
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			String page = "";
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String read;
			while ((read = reader.readLine()) != null)
				page += read;
			reader.close();
			page = page.replace("$er", errHtml);
			// Parse the input document (replace this with your own parser)
			DOMSource parser = new DOMSource(new ByteArrayInputStream(page
					.getBytes("UTF-8")));
			Document doc = parser.parse();

			DOMAnalyzer da = new DOMAnalyzer(doc, url);
			da.attributesToStyles(); // convert the HTML presentation attributes
										// to
										// inline styles
			da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the standard
																// style sheet
			da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the
																// additional
																// style sheet
			da.getStyleSheets(); // load the author style sheets
			// scrollPane.removeAll();
			frame.browser.navigate(da.getRoot(), da, new java.awt.Dimension(
					frame.scrollPane.getWidth(), frame.scrollPane.getHeight()),
					url);
			frame.parseApplets(frame.browser);
			frame.browser.redrawBoxes();
			frame.browser.repaint();
			frame.browser.revalidate();
			frame.validate();
			frame.repaint();
			// scrollPane.setViewportView(browser);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			navigate(frame, "about:econfailed");
		}
	}

	public static InputStream getSiteData(MainFrame frame, URL url)
			throws IOException, SSLHandshakeException {
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream in = new BufferedInputStream(con.getInputStream());
		frame.progressBar.setMaximum(Math.max(con.getContentLength(), in.available()));

		ByteArrayOutputStream bytearr = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		frame.progressBar.setValue(0);
		frame.lblProg.setText("Transferring from " + url.toString() + "...");
		while ((in.read(buffer)) > 0)
		{
			System.out.print("no");
			frame.progressBar.setValue(frame.progressBar.getValue()+1024);
			frame.progressBar.repaint();
			frame.progressBar.revalidate();
			bytearr.write(buffer);
		}
		in.close();
		frame.progressBar.setValue(frame.progressBar.getMaximum());
		frame.progressBar.repaint();
		return new ByteArrayInputStream(bytearr.toByteArray());
	}

	public static URL make_url(final String a_url) throws MalformedURLException {
		try
		{
			return new URL(a_url);
		}
		catch (final MalformedURLException e)
		{
		}
		return new URL("http://" + a_url);
	}

	public static void navigate(final MainFrame frame, final String location) {
		new Thread() {
			public void run() {
				frame.lblProg.setText("Connecting...");
				URL url;
				try
				{
					url = make_url(location);
					if (url == null) throw new MalformedURLException();

					frame.clearComp();
					InputStream is = getSiteData(frame, url);
					frame.lblProg.setText("Parsing...");

					// Parse the input document (replace this with your own
					// parser)
					DOMSource parser = new DOMSource(is);
					Document doc = parser.parse();

					DOMAnalyzer da = new DOMAnalyzer(doc, url);
					da.attributesToStyles(); // convert the HTML presentation
												// attributes to
												// inline styles
					da.addStyleSheet(null, CSSNorm.stdStyleSheet()); // use the
																		// standard
																		// style
																		// sheet
					da.addStyleSheet(null, CSSNorm.userStyleSheet()); // use the
																		// additional
																		// style
																		// sheet
					da.getStyleSheets(); // load the author style sheets
					// scrollPane.removeAll();

					frame.lblProg.setText("Drawing...");
					frame.browser.navigate(da.getRoot(), da,
							new java.awt.Dimension(frame.scrollPane.getWidth(),
									frame.scrollPane.getHeight()), url);

					frame.lblProg.setText("Parsing Applets...");
					frame.parseApplets(frame.browser);
					// frame.parseComponents(frame.browser);
					frame.browser.redrawBoxes();
					frame.browser.repaint();
					frame.browser.revalidate();
					frame.validate();
					frame.repaint();
					frame.lblProg.setText("Done.");
				}
				catch (MalformedURLException e)
				{
					handleExcec(frame, location, e);
				}
				// scrollPane.setViewportView(browser);
				catch (UnknownHostException e)
				{

					frame.lblProg.setText("Unknown Host");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString(); // stack trace as a
														// string
					navigateError(
							frame,
							"about:eunknownhost",
							"<br><br>The host <a href='"
									+ HtmlUtils.stringToHTMLString(location)
									+ "'>"
									+ HtmlUtils.stringToHTMLString(location)
									+ "</a><br> could not be connected to."
									+ "<br>It may have been mistyped, or does not exist"
									+ "<br>Please ensure that the host is valid "
									+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
									+ "Java stack trace:"
									+ "<br />&nbsp;<br />&nbsp;<br />"
									+ HtmlUtils.stringToHTMLString(sStackTrace));
				}
				catch (SSLHandshakeException e)
				{

					frame.lblProg.setText("SSL Error");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString(); // stack trace as a
														// string
					navigateError(
							frame,
							"about:esecurityexception",
							"<br><br>The host <a href='"
									+ HtmlUtils.stringToHTMLString(location)
									+ "'>"
									+ HtmlUtils.stringToHTMLString(location)
									+ "</a><br> rejected the SSL Handshake."
									+ "<br>It may be a bad site, or your JVM doesn't have up to date"
									+ "<br>encryption protocols. "
									+ "<br>Connection cannot continue.<br />&nbsp;<br />"
									+ "Java stack trace:"
									+ "<br />&nbsp;<br />&nbsp;<br />"
									+ HtmlUtils.stringToHTMLString(sStackTrace));
				}
				catch (IOException e)
				{

					frame.lblProg.setText("Connection Error");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString(); // stack trace as a
														// string
					navigateError(
							frame,
							"about:econfailed",
							"<br><br>The host <a href='"
									+ HtmlUtils.stringToHTMLString(location)
									+ "'>"
									+ HtmlUtils.stringToHTMLString(location)
									+ "</a><br> rejected the connection."
									+ "<br>It may be a bad site, or you have tried to access"
									+ "<br>a document you don't have permission to."
									+ "<br>Please ensure that the host is valid "
									+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
									+ "Java stack trace:"
									+ "<br />&nbsp;<br />&nbsp;<br />"
									+ HtmlUtils.stringToHTMLString(sStackTrace));
				}
				catch (Exception e)
				{

					frame.lblProg.setText("Unknown Error");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					String sStackTrace = sw.toString(); // stack trace as a
														// string
					navigateError(
							frame,
							"about:eunknwon",
							"<br><br>There was an unknown error when trying to connect to <br>the host <a href='"
									+ HtmlUtils.stringToHTMLString(location)
									+ "'>"
									+ HtmlUtils.stringToHTMLString(location)
									+ "</a>"
									+ "<br>Please ensure that the host is valid "
									+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
									+ "Java stack trace:"
									+ "<br />&nbsp;<br />&nbsp;<br />"
									+ HtmlUtils.stringToHTMLString(sStackTrace));
				}
			}
		}.start();
	}

	public ConnectionHandler() {
		// TODO Auto-generated constructor stub
	}

	public static void navigate(MainFrame frame, URL baseURL, String nodeValue) {
		try
		{
			URL url = new URL(baseURL, nodeValue);
			navigate(frame, url.toString());
		}
		catch (MalformedURLException e)
		{
			handleExcec(frame, nodeValue, e);
		}
	}

	private static void handleExcec(MainFrame frame, String location,
			MalformedURLException e) {
		frame.lblProg.setText("Error");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString(); // stack trace as a string
		navigateError(
				frame,
				"about:eurlmalform",
				"<br><br>The address <a href='"
						+ HtmlUtils.stringToHTMLString(location)
						+ "'>"
						+ HtmlUtils.stringToHTMLString(location)
						+ "</a><br>does not match any supported protocol."
						+ "<br>It may have been mistyped."
						+ "<br>Please ensure that the protocol is supported "
						+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
						+ "Java stack trace:"
						+ "<br />&nbsp;<br />&nbsp;<br />"
						+ HtmlUtils.stringToHTMLString(sStackTrace));
	}

}
