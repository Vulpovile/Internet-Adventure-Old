package com.androdome.iadventure.appletutils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

import com.androdome.iadventure.appletutils.AppletVerifier.Signage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AppletAcceptDialog extends JFrame
{

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
	 * 
	 * @param codeBase
	 * @param className
	 * @param archives
	 * @param name
	 */

	static File trustedSites = new File("TrustedSites.txt");
	static ArrayList<String> trusted = new ArrayList<String>();

	static
	{
		if (trustedSites.exists())
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(trustedSites));
				String line;
				while ((line = reader.readLine()) != null)
					trusted.add(line);
				reader.close();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				trustedSites.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isSiteTrusted(String codebase) {
		return trusted.contains(codebase.toLowerCase());
		// return true;
	}

	public void setSiteTrusted(String codebase) {
		codebase = codebase.toLowerCase();
		trusted.add(codebase);
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(trustedSites));
			for (int i = 0; i < trusted.size(); i++)
			{
				writer.write(trusted.get(i));
				writer.newLine();
			}
			writer.close();
		}
		catch (Exception ex)
		{
		}
	}

	String codebase;
	AppletVerifier.Signage signage = Signage.UNSIGNED;
	public AppletAcceptDialog(String name, URL[] archives, String className, final String codeBase) {

		codebase = codeBase;
		if (isSiteTrusted(codeBase))
		{
			this.dialogResult = DIALOG_RUN;
			return;
		}
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		this.setAlwaysOnTop(true);
		setTitle("Warning - Security");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(510, 290));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBounds(-15, -24, 544, 84);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblAnAppletWants = new JLabel("Applet \"" + name + "\" wants to run");
		lblAnAppletWants.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblAnAppletWants.setBounds(23, 30, 410, 22);
		panel.add(lblAnAppletWants);

		JLabel lblPermissionDoYou = new JLabel("Do you accept it?");
		lblPermissionDoYou.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblPermissionDoYou.setBounds(23, 51, 410, 22);
		panel.add(lblPermissionDoYou);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		try
		{
			if (archives.length >= 0)
			{
				signage = AppletVerifier.verifySignage(archives[0]);
				if(signage == Signage.CORRUPT)
					lblNewLabel.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/stopscale.png")).getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
				else if(signage == Signage.UNSIGNED)
					lblNewLabel.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/warnscale.png")).getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
				else if(signage == Signage.SELFSIGNED)
					lblNewLabel.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/questionscale.png")).getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
				else if(signage == Signage.SIGNED)
					lblNewLabel.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/okscale.png")).getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
			}
			else lblNewLabel.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/warnscale.png")).getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
		

		}
		catch (IOException e1)
		{
			lblNewLabel.setIcon(UIManager.getIcon(UIManager.getIcon("OptionPane.warningIcon")));
			e1.printStackTrace();
		}
		lblNewLabel.setBounds(458, 22, 64, 62);
		panel.add(lblNewLabel);

		String ar = "";
		for (int i = 0; i < archives.length; i++)
		{
			if (i != 0) ar += ", ";
			ar += archives[i].toString().replace(codeBase, "");
		}
		JLabel lblMainClass = new JLabel("<html><b>Archives:</b> " + ar + "</html>");
		lblMainClass.setBounds(14, 71, 486, 14);
		contentPanel.add(lblMainClass);

		JLabel lblPublisherUnknown = new JLabel("<html><b>Publisher:</b> UNKNOWN</html>");
		lblPublisherUnknown.setBounds(14, 96, 486, 14);
		contentPanel.add(lblPublisherUnknown);

		JLabel lblmainClassNull = new JLabel("<html><b>Main Class:</b> " + className + "</html>");
		lblmainClassNull.setBounds(14, 121, 486, 14);
		contentPanel.add(lblmainClassNull);

		JLabel lblfrom = new JLabel("<html><b>From:</b> " + codeBase + "</html>");
		lblfrom.setBounds(14, 146, 486, 14);
		contentPanel.add(lblfrom);

		final JCheckBox chckbxDoNotAsk = new JCheckBox("Always trust content from this website");
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
				if (chckbxDoNotAsk.isSelected()) setSiteTrusted(codeBase);
				dispose();
			}
		});
		btnRun.setBounds(312, 206, 89, 23);
		contentPanel.add(btnRun);
		pack();
		this.setLocationRelativeTo(null);
	}

	@Override()
	public void setVisible(boolean vis) {
		if (isSiteTrusted(codebase))
		{
			this.dialogResult = DIALOG_RUN;
			this.dispose();
		}
		else super.setVisible(vis);
	}
}
