package com.androdome.iadventure.componentutils;

import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.InlineBox;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.androdome.iadventure.MainFrame;

public class ComponentManager {

	public static void parseComponents(MainFrame frame, BrowserCanvas browser) {
		List<InlineBox> forms = browser.getViewport().getInlineBoxesByName("form", false);
		System.out.println(forms.size());
		for (InlineBox element : forms)
		{
			String action = ".";
			String method = "GET";
			String enctype = "application/x-www-form-urlencoded";
			if(element.getNode().getAttributes().getNamedItem("action") != null)
				action = element.getNode().getAttributes().getNamedItem("action").getNodeValue();
			if(element.getNode().getAttributes().getNamedItem("enctype") != null)
				enctype = element.getNode().getAttributes().getNamedItem("enctype").getNodeValue();
			if(element.getNode().getAttributes().getNamedItem("method") != null)
				method = element.getNode().getAttributes().getNamedItem("method").getNodeValue();
			FormItem form = new FormItem(action, method, enctype);
			Stack<Node> mustSearch = new Stack<Node>();
			mustSearch.push(element.getNode());
			Stack<Node> foundInput = new Stack<Node>();
			
			while(!mustSearch.isEmpty())
			{
				NodeList l = mustSearch.pop().getChildNodes();
				for (int i = 0; i < l.getLength(); i++)
				{
					if(l.item(i).getNodeName().equalsIgnoreCase("input"))
						foundInput.add(l.item(i));
					else mustSearch.add(l.item(i));
				}
			}
			for(Node n : foundInput)
			{
				Box box = browser.getViewport().getElementBoxByNode(n);
				if(box.getNode().getAttributes().getNamedItem("type") != null)
				{
					String type = box.getNode().getAttributes().getNamedItem("type").getNodeValue();
					if(type.equalsIgnoreCase("text"))
					{
						JTextField textField = new JTextField();
						textField.setBounds(box.getAbsoluteBounds());
						browser.add(textField);
						frame.addComponentNodeBinding(new JTextField(), box.getNode());
						form.add(textField, n);
					}
					if(type.equalsIgnoreCase("button"))
					{
						String text = "Generic Input Button";
						if(box.getNode().getAttributes().getNamedItem("value") != null)
							text = box.getNode().getAttributes().getNamedItem("value").getNodeValue();
						JButton textField = new JButton(text);
						textField.setBounds(box.getAbsoluteBounds());
						browser.add(textField);
						frame.addComponentNodeBinding(new JTextField(), box.getNode());
						form.add(textField, n);
					}
					if(type.equalsIgnoreCase("submit"))
					{
						String text = "Generic Submit Button";
						if(box.getNode().getAttributes().getNamedItem("value") != null)
							text = box.getNode().getAttributes().getNamedItem("value").getNodeValue();
						JButton textField = new JButton(text);
						textField.setBounds(box.getAbsoluteBounds());
						browser.add(textField);
						frame.addComponentNodeBinding(new JTextField(), box.getNode());
						form.add(textField, n);
					}
				}
				else
				{
					JTextField textField = new JTextField();
					textField.setBounds(box.getAbsoluteBounds());
					browser.add(textField);
					frame.addComponentNodeBinding(new JTextField(), box.getNode());
					form.add(textField, n);
				}
			}

			/*if(element.getNode().getAttributes().getNamedItem("type") != null)
			{
				String type = element.getNode().getAttributes().getNamedItem("type").getNodeValue();
				if(type.equalsIgnoreCase("text"))
				{
					JTextField textField = new JTextField();
					textField.setBounds(element.getAbsoluteBounds());
					browser.add(textField);
					frame.addComponentNodeBinding(new JTextField(), element.getNode());
				}
				if(type.equalsIgnoreCase("button"))
				{
					String text = "Generic Input Button";
					if(element.getNode().getAttributes().getNamedItem("value") != null)
						text = element.getNode().getAttributes().getNamedItem("value").getNodeValue();
					JButton textField = new JButton(text);
					textField.setBounds(element.getAbsoluteBounds());
					browser.add(textField);
					frame.addComponentNodeBinding(new JTextField(), element.getNode());
				}
				if(type.equalsIgnoreCase("submit"))
				{
					String text = "Generic Submit Button";
					if(element.getNode().getAttributes().getNamedItem("value") != null)
						text = element.getNode().getAttributes().getNamedItem("value").getNodeValue();
					JButton textField = new JButton(text);
					textField.setBounds(element.getAbsoluteBounds());
					browser.add(textField);
					frame.addComponentNodeBinding(new JTextField(), element.getNode());
				}
			}
			else
			{
				JTextField textField = new JTextField();
				textField.setBounds(element.getAbsoluteBounds());
				browser.add(textField);
				frame.addComponentNodeBinding(new JTextField(), element.getNode());
			}*/
		}
		browser.repaint();
		browser.revalidate();
	}

}
