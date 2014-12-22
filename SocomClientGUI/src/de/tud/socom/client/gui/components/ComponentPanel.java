package de.tud.socom.client.gui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.tud.socom.client.logic.Connection;
import de.tud.socom.client.logic.Status;

@SuppressWarnings("serial")
public abstract class ComponentPanel extends JPanel {

	private JPanel parameterPanel;
	private JList methodList;
	private int optioanlParameterCount = 0;
	private boolean isPOST = false;

	protected Map<String, JTextField> stringParams = new HashMap<String, JTextField>();
	private Map<String, JCheckBox> booleanParams = new HashMap<String, JCheckBox>();
	private Map<String, JSpinner> integerParams = new HashMap<String, JSpinner>();
	private Map<String, JComboBox> comboParams = new HashMap<String, JComboBox>();
	private File uploadFile;

	public ComponentPanel() {
		setLayout(new BorderLayout(0, 0));

		JButton btnGenerateUrl = new JButton("Generate URL");
		add(btnGenerateUrl, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(splitPane_1);

		JLabel lblSelectMethod = new JLabel("Select Method");
		splitPane_1.setLeftComponent(lblSelectMethod);

		methodList = getJList();
		methodList.setSelectedIndex(0);
		methodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		splitPane_1.setRightComponent(methodList);

		parameterPanel = new JPanel();
		parameterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		splitPane.setRightComponent(parameterPanel);

		btnGenerateUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				generateURL();
			}

		});

		initListener(methodList);
	}

	private void generateURL() {
		String component = getComponent();
		String method = getMethod();
		if (method == null) {
			JOptionPane.showMessageDialog(this, "Select a method.");
			return;
		}

		String URL = Status.SERVER_URL + component + "/" + method + "?";

		if (!isPOST) {
			URL = appendGETParams(URL);
			Status.get().setCurrentMethod(Connection.GET_REQUEST);
		} else {
			URL = URL.substring(0, URL.length() - 1);
			String params = getPOSTParams();
			Status.get().setCookieParams(params);
			Status.get().setFile(uploadFile);
			Status.get().setCurrentMethod(Connection.POST_REQUEST);
		}

		Status.get().setCurrentURL(URL);
	}

	private String getPOSTParams() {
		String cookieParams = "";
		for (String param : stringParams.keySet()) {
			cookieParams += param + "=" + stringParams.get(param).getText().replaceAll(",", ";") + ",";
		}

		for (String param : booleanParams.keySet()) {
			cookieParams += param + "=" + booleanParams.get(param).isSelected() + ",";
		}

		for (String param : integerParams.keySet()) {
			JSpinner spinner = integerParams.get(param);
			if (spinner.isEnabled())
				cookieParams += param + "=" + spinner.getValue() + ",";
		}
		for (String param : comboParams.keySet()) {
			cookieParams += param + "=" + comboParams.get(param).getSelectedItem() + ",";
		}
		if (cookieParams.endsWith(","))
			cookieParams = cookieParams.substring(0, cookieParams.length() - 1);
		return cookieParams;
	}

	private String appendGETParams(String URL) {
		for (String param : stringParams.keySet()) {
			URL += param + "=" + stringParams.get(param).getText() + "&";
		}

		for (String param : booleanParams.keySet()) {
			URL += param + "=" + booleanParams.get(param).isSelected() + "&";
		}

		for (String param : integerParams.keySet()) {
			URL += param + "=" + integerParams.get(param).getValue() + "&";
		}
		for (String param : comboParams.keySet()) {
			URL += param + "=" + comboParams.get(param).getSelectedItem() + "&";
		}

		if (URL.endsWith("&"))
			URL = URL.substring(0, URL.length() - 1);
		if (URL.endsWith("?"))
			URL = URL.substring(0, URL.length() - 1);
		return URL;
	}

	protected abstract String getComponent();

	protected String getMethod() {
		return ((String) methodList.getSelectedValue()).split(" ")[0];
	}

	protected abstract JList getJList();

	protected abstract void updatePanel(Object selectedValue);

	protected JCheckBox addOptionalIntegerParameter(final String key) {
		JCheckBox keyLabel = new JCheckBox(key);
		parameterPanel.add(keyLabel);

		final JSpinner paramField = new JSpinner();
		paramField.setEnabled(false);
		parameterPanel.add(paramField);

		keyLabel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (((JCheckBox) arg0.getSource()).isSelected()) {
					paramField.setEnabled(true);
					integerParams.put(key, paramField);
				} else {
					integerParams.remove(key);
					paramField.setEnabled(false);
				}
			}
		});
		return keyLabel;
	}

	protected void addOptionalStringParameter(String key) {
		addOptionalStringParameter(key, null);
	}
	
	protected void addOptionalStringParameter(final String key, String tooltip) {
		JCheckBox keyLabel = new JCheckBox(key);
		parameterPanel.add(keyLabel);

		final JTextField paramField = new JTextField();
		if(tooltip != null)
			paramField.setToolTipText(tooltip);
		parameterPanel.add(paramField);
		paramField.setColumns(1);
		paramField.setEnabled(false);
		parameterPanel.add(paramField);

		keyLabel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (((JCheckBox) arg0.getSource()).isSelected()) {
					paramField.setEnabled(true);
					stringParams.put(key, paramField);
				} else {
					stringParams.remove(key);
					paramField.setEnabled(false);
				}
			}
		});
	}

	protected void addOptionalParameter() {
		final JTextField keyBox = new JTextField();
		final JTextField valueBox = new JTextField();
		keyBox.setToolTipText("Optional Key-Value Pair.");
		valueBox.setEnabled(false);
		
		parameterPanel.add(keyBox);
		parameterPanel.add(valueBox);

		keyBox.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				valueBox.setEnabled(!keyBox.getText().trim().isEmpty());
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		valueBox.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String keyText = keyBox.getText();
				String valueText = valueBox.getText();
				if (!keyText.isEmpty() && valueBox.isEnabled() && !valueText.isEmpty())
					stringParams.put(keyText, valueBox);
				else
					stringParams.remove(keyText);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	protected void addOptionalBooleanParameter(final String key) {
		JCheckBox keyLabel = new JCheckBox(key);
		parameterPanel.add(keyLabel);

		final JCheckBox paramField = new JCheckBox();
		parameterPanel.add(paramField);
		paramField.setEnabled(false);
		parameterPanel.add(paramField);

		keyLabel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (((JCheckBox) arg0.getSource()).isSelected()) {
					paramField.setEnabled(true);
					booleanParams.put(key, paramField);
				} else {
					booleanParams.remove(key);
					paramField.setEnabled(false);
				}
			}
		});
	}

	protected JTextField addStringParameter(String key, String tip) {
		JLabel keyLabel = new JLabel(key);
		parameterPanel.add(keyLabel);

		JTextField paramField = new JTextField();
		parameterPanel.add(paramField);
		paramField.setColumns(1);
		if (tip != null) {
			paramField.setToolTipText(tip);
			paramField.setText(tip);
		}

		stringParams.put(key, paramField);
		return paramField;
	}

	protected void addBooleanParameter(String key) {
		JLabel keyLabel = new JLabel(key);
		parameterPanel.add(keyLabel);

		JCheckBox paramField = new JCheckBox();
		parameterPanel.add(paramField);

		booleanParams.put(key, paramField);
	}

	protected JSpinner addIntegerParameter(String key) {
		JLabel keyLabel = new JLabel(key);
		parameterPanel.add(keyLabel);

		JSpinner paramField = new JSpinner();
		parameterPanel.add(paramField);

		integerParams.put(key, paramField);
		return paramField;
	}

	protected void addComboParameter(String... lst) {
		JLabel param = new JLabel(lst[0]);
		parameterPanel.add(param);

		JComboBox combo = new JComboBox(Arrays.copyOfRange(lst, 1, lst.length));
		parameterPanel.add(combo);

		comboParams.put(lst[0], combo);
	}

	protected void addFileChooserParameter(String name) {
		JButton button = new JButton(name);
		parameterPanel.add(button);

		final JLabel fileLabel = new JLabel("-");
		parameterPanel.add(fileLabel);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.showOpenDialog(null);

				File f = jfc.getSelectedFile();
				if (f == null || !f.exists() || f.isDirectory())
					return;

				uploadFile = f;
				fileLabel.setText(f.getName());
			}
		});
	}

	protected void refresh() {
		parameterPanel.setLayout(new GridLayout(getRows(), 2, 0, 0));
		parameterPanel.setVisible(false);
		parameterPanel.setVisible(true);
	}

	private int getRows() {
		return stringParams.size() + booleanParams.size() + integerParams.size() + comboParams.size() + optioanlParameterCount;
	}

	protected void initListener(JList comboBox) {
		comboBox.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				clearParameterList();
				updatePanel(((JList) e.getSource()).getSelectedValue());
			}
		});
	}

	protected void addStringParameterList(String... string) {
		for (String param : string) {
			addStringParameter(param, null);
		}
	}

	protected void addStringPredefinedParameterList(String... string) {
		for (int i = 0; i < string.length; i += 2) {
			addStringParameter(string[i], string[i + 1]);
		}
	}

	protected void addBooleanParameterList(String... string) {
		for (String param : string) {
			addBooleanParameter(param);
		}
	}

	protected void setAdditionalParameterCount(int count) {
		this.optioanlParameterCount = count;
	}

	protected void clearParameterList() {
		parameterPanel.removeAll();
		parameterPanel.setVisible(false);
		parameterPanel.setVisible(true);
		stringParams.clear();
		booleanParams.clear();
		integerParams.clear();
		comboParams.clear();
		Status.get().setFile(null);
		isPOST = false;
	}

	protected void activatePost() {
		isPOST = true;
	}

	protected void setUploadFile(File f) {
		this.uploadFile = f;
	}
}
