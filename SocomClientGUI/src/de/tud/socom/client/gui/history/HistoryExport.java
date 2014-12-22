package de.tud.socom.client.gui.history;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import de.tud.socom.client.logic.Connection;

@SuppressWarnings("serial")
public class HistoryExport extends JFrame {

	final JList list = new JList();
	private JPanel contentPane;

	public HistoryExport() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		list.setCellRenderer(new CellRenderer());
		fillListWithHistory(list);

		contentPane.add(list, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane);

		JButton btnNewButton = new JButton("Export");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exportUrls();
			}
		});

		splitPane.setLeftComponent(btnNewButton);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HistoryExport.this.dispose();
			}
		});
		splitPane.setRightComponent(btnClose);

		list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) list.getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});

		selectAll();

		this.setVisible(true);
	}

	private void exportUrls() {
		List<String> urls = new LinkedList<String>();
		ListModel model = list.getModel();
		int size = model.getSize();
		for (int i = 0; i < size; i++) {
			JCheckBox checkbox = (JCheckBox) model.getElementAt(i);
			boolean selected = checkbox.isSelected();
			if (selected)
				urls.add(checkbox.getText());
		}

		JFileChooser jFileChooser = new JFileChooser();
		int returnVal = jFileChooser.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = jFileChooser.getSelectedFile();

			if (f.exists()) {
				int result = JOptionPane.showConfirmDialog(this, "File already exists. Overwrite?");
				if (result == JOptionPane.YES_OPTION) {
					f.delete();
				} else {
					System.out.println("do not overwrite");
					return;
				}
			}

			try {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f));
				Map<String, String> methods = Connection.get().getUrlHistoryRequests();
				for (String url : urls) {
					writer.write(methods.get(url) + ":" + url + "\n");
				}
				writer.flush();
				writer.close();

				JOptionPane.showMessageDialog(this, "File saved.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void fillListWithHistory(JList list) {
		DefaultListModel model = new DefaultListModel();
		list.setModel(model);

		List<String> urls = Connection.get().getUrlHistory();
		for (int i = 0; i < urls.size(); i++) {
			model.add(i, new JCheckBox(urls.get(i)));
		}
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			return checkbox;
		}
	}

	public void selectAll() {
		int size = list.getModel().getSize();
		for (int i = 0; i < size; i++) {
			JCheckBox checkbox = (JCheckBox) list.getModel().getElementAt(i);
			checkbox.setSelected(true);
		}
		this.repaint();
	}

	public void deselectAll() {
		int size = list.getModel().getSize();
		for (int i = 0; i < size; i++) {
			JCheckBox checkbox = (JCheckBox) list.getModel().getElementAt(i);
			checkbox.setSelected(false);
		}
		this.repaint();
	}
}
