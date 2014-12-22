package de.tud.socom.client.gui.history;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.tud.socom.client.logic.Connection;

public class HistoryLoader {

	private static final int REQUEST_DELAY = 300;
	private Connection c = Connection.get();

	public HistoryLoader() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (!f.exists())
				return;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String url;

				while (reader.ready()) {
					String line = reader.readLine();
					if (line.startsWith(Connection.GET_REQUEST + ":")) {
						url = line.substring(Connection.GET_REQUEST.length() + 1);
						sendGET(url);
					} else if (line.startsWith(Connection.POST_REQUEST + ":")) {
						String[] parts = line.split(";");
						url = parts[0].substring(Connection.POST_REQUEST.length() + 1);
						String cookieParams = parts[1].split(":")[1];
						String filePath = parts[2].split(":")[1];
						sendPOST(url, cookieParams, filePath);
					}
				}
				reader.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendPOST(String url, String cookieParams, String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			int res = JOptionPane.showConfirmDialog(null, "The File " + f.getAbsolutePath()
					+ " could not be Found. Select another File? (Otherwise ignore Request)\n\nURL: " + url + "\nParameter: " + cookieParams);
			if (res == JOptionPane.YES_OPTION) {
				JFileChooser fileC = new JFileChooser();
				fileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
				res = fileC.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					f = fileC.getSelectedFile();
				} else
					JOptionPane.showMessageDialog(null, "The Request will be ingored");
			} else
				return;
		}
		c.sendPOSTRequest(url, f, cookieParams);
		delay();
	}

	public void sendGET(String url) {
		c.sendGETRequest(url);
		delay();
	}

	private void delay() {
		try {
			Thread.sleep(REQUEST_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
