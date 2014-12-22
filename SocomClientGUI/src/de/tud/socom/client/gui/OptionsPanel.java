package de.tud.socom.client.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.tud.socom.client.logic.Status;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class OptionsPanel extends JPanel {
	private JTextField txtHttplocalhost;

	/**
	 * Create the panel.
	 */
	public OptionsPanel() {
		
		JLabel lblServerUrl = new JLabel("Server URL");
		
		txtHttplocalhost = new JTextField();
		txtHttplocalhost.setText("http://localhost:7999/");
		txtHttplocalhost.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Status.setHost(txtHttplocalhost.getText());
				JOptionPane.showMessageDialog(OptionsPanel.this, "Server Adress is now: " + Status.SERVER_URL);
			}
		});
		
		JTextPane txtpnSocomClientGui = new JTextPane();
		txtpnSocomClientGui.setText("Socom Client GUI");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(64)
							.addComponent(lblServerUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtHttplocalhost, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
							.addGap(12)
							.addComponent(btnSave))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(114)
							.addComponent(txtpnSocomClientGui, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(49)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(txtHttplocalhost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblServerUrl))
					.addGap(50)
					.addComponent(txtpnSocomClientGui, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(69, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

	}
}
