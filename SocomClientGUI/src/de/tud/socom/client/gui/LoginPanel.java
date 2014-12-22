package de.tud.socom.client.gui;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import de.tud.socom.client.logic.Connection;
import de.tud.socom.client.logic.Cookies;
import de.tud.socom.client.logic.Status;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
	private JTextField txtName;
	private JTextField txtPassword;
	private JTextField txtGame;
	private JTextField txtVersion;
	private JTextField txtGamepassword;
	private JCheckBox chckbxLoggedin;
	private JButton btnLogin;

	private static LoginPanel instance = new LoginPanel();

	public static LoginPanel get() {
		return instance;
	}

	/**
	 * Create the panel.
	 */
	private LoginPanel() {
		init();
	}

	private void init() {
		this.removeAll();
		setLayout(new GridLayout(6, 2, 0, 0));

		JLabel lblName = new JLabel("Name");
		add(lblName);

		txtName = new JTextField();
		add(txtName);
		txtName.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		add(lblPassword);

		txtPassword = new JTextField();
		add(txtPassword);
		txtPassword.setColumns(10);

		JLabel lblGameinstance = new JLabel("Game");
		add(lblGameinstance);

		txtGame = new JTextField();
		add(txtGame);
		txtGame.setColumns(10);

		JLabel lblVersion = new JLabel("Version");
		add(lblVersion);

		txtVersion = new JTextField();
		add(txtVersion);
		txtVersion.setColumns(10);

		JLabel lblGamepassword = new JLabel("Gamepassword");
		add(lblGamepassword);

		txtGamepassword = new JTextField();
		add(txtGamepassword);
		txtGamepassword.setColumns(10);

		chckbxLoggedin = new JCheckBox("Logged in");
		chckbxLoggedin.setEnabled(false);
		add(chckbxLoggedin);

		btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (chckbxLoggedin.isSelected())
					startLogout();
				else
					startLogin();
			}
		});
		add(btnLogin);
	}

	private void startLogin() {
		String name = txtName.getText();
		String password = txtPassword.getText();
		String game = txtGame.getText();
		String version = txtVersion.getText();
		String gamepw = txtGamepassword.getText();

		if (name.isEmpty() || password.isEmpty() || game.isEmpty() || version.isEmpty() || gamepw.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Fill out all Parameter");
			return;
		}

		JSONObject json = Connection.get().sendGETRequest(
				Status.SERVER_URL + "user/loginuser?username=" + name + "&password=" + password + "&game=" + game + "&version=" + version + "&gamepassword="
						+ gamepw);

		if (json.has("uid")) {
			setLogin(name, password, game, version, gamepw);
		}

	}

	public void startLogout() {
		Connection.get().sendGETRequest(Status.SERVER_URL + "user/logout");
		Cookies.removeAllCookies();
		init();
		setVisible(false);
		setVisible(true);
	}

	public void setLogin(String name, String password, String game, String version, String gamepassword) {
		txtName.setText(name);
		txtName.setEnabled(false);

		txtPassword.setText(password);
		txtPassword.setEnabled(false);

		txtGame.setText(game);
		txtGame.setEnabled(false);

		txtVersion.setText(version);
		txtVersion.setEnabled(false);

		txtGamepassword.setText(gamepassword);
		txtGamepassword.setEnabled(false);

		chckbxLoggedin.setSelected(true);

		btnLogin.setText("Logout");
	}
	
	public boolean isLoggedIn(){
		return chckbxLoggedin.isSelected();
	}
}
