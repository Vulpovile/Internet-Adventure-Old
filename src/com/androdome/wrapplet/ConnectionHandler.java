package com.androdome.wrapplet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.demo.DOMSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConnectionHandler {

	
	public static void navigateError(MainFrame frame, String string, String errHtml) {
		try{
		frame.clearComp();
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
		frame.browser.navigate(da.getRoot(), da, new java.awt.Dimension(frame.scrollPane.getWidth(), frame.scrollPane.getHeight()), url);
		frame.parseApplets(frame.browser);
		// scrollPane.setViewportView(browser);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			navigate(frame, "about:econfailed");
		}
	}

	public static URL make_url(final String a_url) throws MalformedURLException
	{
	    try
	    {
	        return new URL(a_url);
	    }
	    catch (final MalformedURLException e)
	    {
	    }
	    return new URL("http://" + a_url);
	}
	
	public static void navigate(MainFrame frame, String location){
		URL url;
		try
		{
			url = make_url(location);
			if(url == null)
				throw new MalformedURLException();
		
			frame.clearComp();
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
			frame.browser.navigate(da.getRoot(), da, new java.awt.Dimension(frame.scrollPane.getWidth(), frame.scrollPane.getHeight()), url);
			frame.parseApplets(frame.browser);
			//frame.parseComponents(frame.browser);
		}
		catch (MalformedURLException e)
		{
			handleExcec(frame, location, e);
		}
		// scrollPane.setViewportView(browser);
		catch (UnknownHostException e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			navigateError(frame, "about:eunknownhost", "<br><br>The host <a href='"+HtmlUtils.stringToHTMLString(location)+"'>" + HtmlUtils.stringToHTMLString(location) + 
					"</a><br> could not be connected to."
					+ "<br>It may have been mistyped, or does not exist"
					+ "<br>Please ensure that the host is valid "
					+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
					+ "Java stack trace:"
					+"<br />&nbsp;<br />&nbsp;<br />"
					+HtmlUtils.stringToHTMLString(sStackTrace));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private static void handleExcec(MainFrame frame, String location, MalformedURLException e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString(); // stack trace as a string
		navigateError(frame, "about:eurlmalform", "<br><br>The address <a href='"+HtmlUtils.stringToHTMLString(location)+"'>" + HtmlUtils.stringToHTMLString(location) + 
				"</a><br>does not match any supported protocol."
				+ "<br>It may have been mistyped."
				+ "<br>Please ensure that the protocol is supported "
				+ "<br>and that everything is spelled correctly.<br />&nbsp;<br />"
				+ "Java stack trace:"
				+"<br />&nbsp;<br />&nbsp;<br />"
				+HtmlUtils.stringToHTMLString(sStackTrace));
	}
	

}
