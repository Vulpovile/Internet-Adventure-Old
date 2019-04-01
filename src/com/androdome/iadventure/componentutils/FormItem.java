package com.androdome.iadventure.componentutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import org.w3c.dom.Node;

import com.androdome.iadventure.utils.ArrayListBinding;

public class FormItem implements ActionListener{

	ArrayListBinding<Component, Node> componentNodeBinding = new ArrayListBinding<Component, Node>();
	String method;
	String enctype;
	String action;
	public FormItem(String action, String method, String enctype) {
		// TODO Auto-generated constructor stub
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
				System.out.println("Submit was pressed!");
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
