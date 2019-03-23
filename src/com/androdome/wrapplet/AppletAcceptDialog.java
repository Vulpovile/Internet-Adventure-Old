package com.androdome.wrapplet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import java.awt.Color;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;

public class AppletAcceptDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	int dialogResult = 0;
	static final int DIALOG_RUN = 1;
	static final int DIALOG_CANCEL = 2;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the dialog.
	 * @param codeBase 
	 * @param className 
	 * @param archives 
	 * @param name 
	 */
	public AppletAcceptDialog(String name, URL[] archives, String className, String codeBase) {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		this.setAlwaysOnTop(true);
		setTitle("Warning - Security");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(510,290));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(-15, -24, 544, 84);
		contentPanel.add(panel);
		panel.setLayout(null);
		
		JLabel lblAnAppletWants = new JLabel("Applet \""+name+"\" wants to run");
		lblAnAppletWants.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblAnAppletWants.setBounds(23, 30, 410, 22);
		panel.add(lblAnAppletWants);
		
		JLabel lblPermissionDoYou = new JLabel("Do you accept it?");
		lblPermissionDoYou.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblPermissionDoYou.setBounds(23, 51, 410, 22);
		panel.add(lblPermissionDoYou);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		lblNewLabel.setBounds(458, 22, 64, 62);
		panel.add(lblNewLabel);
		
		String ar = "";
		for(int i = 0; i < archives.length; i++)
		{
			if(i != 0)
				ar+= ", ";
			ar += archives[i].toString().replace(codeBase, "");
		}
		JLabel lblMainClass = new JLabel("<html><b>Archives:</b> "+ar+"</html>");
		lblMainClass.setBounds(14, 71, 267, 14);
		contentPanel.add(lblMainClass);
		
		JLabel lblPublisherUnknown = new JLabel("<html><b>Publisher:</b> UNKNOWN</html>");
		lblPublisherUnknown.setBounds(14, 96, 267, 14);
		contentPanel.add(lblPublisherUnknown);
		
		JLabel lblmainClassNull = new JLabel("<html><b>Main Class:</b> "+className+"</html>");
		lblmainClassNull.setBounds(14, 121, 267, 14);
		contentPanel.add(lblmainClassNull);
		
		JLabel lblfrom = new JLabel("<html><b>From:</b> "+codeBase+"</html>");
		lblfrom.setBounds(14, 146, 267, 14);
		contentPanel.add(lblfrom);
		
		JCheckBox chckbxDoNotAsk = new JCheckBox("Always trust content from this website");
		chckbxDoNotAsk.setBounds(10, 167, 282, 23);
		contentPanel.add(chckbxDoNotAsk);
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(-15, 240, 575, 84);
		contentPanel.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblRunningUnknownApplets = new JLabel("Running untrusted Applets can pose a security risk.");
		lblRunningUnknownApplets.setBounds(25, 11, 491, 14);
		panel_1.add(lblRunningUnknownApplets);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogResult = DIALOG_CANCEL;
				dispose();
			}
		});
		btnCancel.setBounds(411, 206, 89, 23);
		contentPanel.add(btnCancel);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogResult = DIALOG_RUN;
				dispose();
			}
		});
		btnRun.setBounds(312, 206, 89, 23);
		contentPanel.add(btnRun);
		pack();
		this.setLocationRelativeTo(null);
	}
}
