package de.tud.socom.client.gui.cookies;

import java.awt.BorderLayout;
import java.awt.Component;
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

import de.tud.socom.client.logic.Cookies;

@SuppressWarnings("serial")
public class CookieViewer extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public CookieViewer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final JList list = new JList();
		fillListWithCookies(list);

		contentPane.add(list, BorderLayout.CENTER);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);

		JMenuItem mntmDeleteCookie = new JMenuItem("Remove Cookie");
		mntmDeleteCookie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCookie = (String) list.getSelectedValue();
				if (selectedCookie != null)
					Cookies.removeCookie(selectedCookie);
				fillListWithCookies(list);
			}
		});
		popupMenu.add(mntmDeleteCookie);

		JButton btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CookieViewer.this.dispose();
			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	private void fillListWithCookies(JList list) {
		DefaultListModel model = new DefaultListModel();
		list.setModel(model);

		List<String> cookies = Cookies.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			model.add(i, cookies.get(i));
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
