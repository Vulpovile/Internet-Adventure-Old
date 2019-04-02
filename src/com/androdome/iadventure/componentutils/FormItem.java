package com.androdome.iadventure.componentutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.androdome.iadventure.MainFrame;
import com.androdome.iadventure.utils.ArrayListBinding;
import com.androdome.iadventure.utils.Binding;
import com.androdome.iadventure.utils.BindingObject;

public class FormItem implements ActionListener{

	ArrayListBinding<Component, Node> componentNodeBinding = new ArrayListBinding<Component, Node>();
	String method;
	String enctype;
	String action;
	MainFrame frame;
	public FormItem(String action, String method, String enctype, MainFrame frame) {
		this.action = action;
		this.enctype = enctype;
		this.method = method;
		this.frame = frame;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object c = arg0.getSource();
		if(c instanceof JButton)
		{
			JButton btn = (JButton) c;
			Node n = componentNodeBinding.getFirstValueFromKey(btn);
			if(n == null)
				return;
			if(n.getAttributes().getNamedItem("type") != null && n.getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase("submit"))
			{
				String subParams = "submit=submit";
				if(n.getAttributes().getNamedItem("name") != null)
				{
					try
					{
						subParams = URLEncoder.encode(n.getAttributes().getNamedItem("name").getNodeValue(), "UTF-8");
						if(n.getAttributes().getNamedItem("value") != null)
							subParams += "="+URLEncoder.encode(n.getAttributes().getNamedItem("value").getNodeValue(), "UTF-8");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println("Submit was pressed!");
				for(int i = 0; i < componentNodeBinding.getValueList().size(); i++)
				{
					BindingObject<Component, Node> binding = componentNodeBinding.getBindingObject(i);
					Node node = binding.value;
					if(binding.key == null)
					{
						if(node.getAttributes().getNamedItem("name") != null)
						{
							try
							{
								subParams+= "&"+URLEncoder.encode(node.getAttributes().getNamedItem("name").getNodeValue(), "UTF-8");
							
							if(node.getAttributes().getNamedItem("value") != null)
								subParams+= "="+URLEncoder.encode(node.getAttributes().getNamedItem("value").getNodeValue(), "UTF-8");
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(binding.key instanceof JTextField)
					{
						if(node.getAttributes().getNamedItem("name") != null)
						{
							try
							{
								subParams+= "&"+URLEncoder.encode(node.getAttributes().getNamedItem("name").getNodeValue(), "UTF-8");
								subParams+= "="+URLEncoder.encode(((JTextField)binding.key).getText(), "UTF-8");
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				System.out.println("Params: " + subParams);
				if(method.trim().equalsIgnoreCase("POST"))
					frame.conHandler.onFormPost(action, enctype, frame, subParams);
				else frame.conHandler.onFormGet(action, enctype, frame, subParams);
				
			}
		}
	}
	public void add(Component comp, Node n) {
		if(comp instanceof JButton)
		{
			((JButton)comp).addActionListener(this);
		}
		componentNodeBinding.addBinding(comp, n);
	}

}
