package com.androdome.wrapplet;

import java.awt.Rectangle;

import javax.swing.tree.DefaultMutableTreeNode;

import org.fit.cssbox.layout.Box;

public class HtmlUtils {

	public HtmlUtils() {
		// TODO Auto-generated constructor stub
	}

	public static String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++)
		{
			c = string.charAt(i);
			if (c == ' ')
			{
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar)
				{
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				}
				else
				{
					lastWasBlankChar = true;
					sb.append(' ');
				}
			}
			else
			{
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;");
				else if (c == '\n')
					// Handle Newline
					sb.append("<br />");
				else
				{
					int ci = 0xffff & c;
					if (ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else
					{
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	public static DefaultMutableTreeNode locateBox(DefaultMutableTreeNode root, int x, int y) {
		DefaultMutableTreeNode found = null;
		Box box = (Box) root.getUserObject();
		Rectangle bounds = box.getAbsoluteBounds();
		if (bounds.contains(x, y))
			found = root;

		// find if there is something smallest that fits among the child boxes
		for (int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode inside = locateBox((DefaultMutableTreeNode) root.getChildAt(i), x, y);
			if (inside != null)
			{
				if (found == null)
					found = inside;
				else
				{
					Box fbox = (Box) found.getUserObject();
					Box ibox = (Box) inside.getUserObject();
					if (ibox.getAbsoluteBounds().width * ibox.getAbsoluteBounds().height < fbox.getAbsoluteBounds().width * fbox.getAbsoluteBounds().height)
						found = inside;
				}
			}
		}

		return found;
	}
}
