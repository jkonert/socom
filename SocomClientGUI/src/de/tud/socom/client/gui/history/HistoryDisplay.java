package de.tud.socom.client.gui.history;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import de.tud.socom.client.logic.Connection;

@SuppressWarnings("serial")
public class HistoryDisplay extends JFrame {

	private JPanel contentPane;

	public HistoryDisplay() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final JList list = new JList();
		fillListWithHistory(list);

		contentPane.add(list, BorderLayout.CENTER);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);
		
		JMenuItem mntmCopyUrl = new JMenuItem("Copy URL");
		mntmCopyUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedUrl = (String)list.getSelectedValue();
				if(selectedUrl != null)
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			               new StringSelection(selectedUrl), null);
			}
		});
		popupMenu.add(mntmCopyUrl);
		
		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HistoryDisplay.this.dispose();
			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	private void fillListWithHistory(JList list) {
		DefaultListModel model = new DefaultListModel();
		list.setModel(model);

		List<String> urls = Connection.get().getUrlHistory();
		for (int i = 0; i < urls.size(); i++) {
			model.add(i, urls.get(i));
		}
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
