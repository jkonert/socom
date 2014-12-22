package de.tud.socom.client.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.tud.socom.client.logic.ServerAnswer;

@SuppressWarnings("serial")
public class ConsolePanel extends JPanel implements Observer {

	private JTextArea consoleArea;
	private JScrollPane scrollPane;

	public ConsolePanel() {
		scrollPane = new JScrollPane();
		add(scrollPane);
		consoleArea = new JTextArea();
		scrollPane.setViewportView(consoleArea);
		consoleArea.setEditable(false);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof ServerAnswer) {
			ServerAnswer ans = (ServerAnswer) arg1;
			String url = ans.url;
			String answer = ans.answer;
			String text = consoleArea.getText();
			text = new SimpleDateFormat("HH:mm:ss").format(new Date()) + " (..." + url.substring(21) + ")\n " + answer
					+ (text.isEmpty() ? "" : "\n---------------------------------------------------------------\n") + text;
			consoleArea.setText(text);
			consoleArea.setCaretPosition(0);
		}
	}
}
