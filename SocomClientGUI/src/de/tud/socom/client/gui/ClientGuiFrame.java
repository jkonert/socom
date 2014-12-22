package de.tud.socom.client.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerListModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import de.tud.socom.client.gui.batch.BatchCreate;
import de.tud.socom.client.gui.components.AchievementComponentPanel;
import de.tud.socom.client.gui.components.ContentComponentPanel;
import de.tud.socom.client.gui.components.GameComponentPanel;
import de.tud.socom.client.gui.components.InfluenceComponentPanel;
import de.tud.socom.client.gui.components.SocialComponentPanel;
import de.tud.socom.client.gui.components.StatisticComponentPanel;
import de.tud.socom.client.gui.components.UserComponentPanel;
import de.tud.socom.client.gui.cookies.CookieViewer;
import de.tud.socom.client.gui.history.HistoryDisplay;
import de.tud.socom.client.gui.history.HistoryExport;
import de.tud.socom.client.gui.history.HistoryLoader;
import de.tud.socom.client.logic.Connection;
import de.tud.socom.client.logic.Status;

@SuppressWarnings("serial")
public class ClientGuiFrame extends JFrame implements Observer {

	private JSplitPane contentPane;
	private JSpinner URLSpinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGuiFrame frame = new ClientGuiFrame();
					frame.setVisible(true);
					Status.get().addObserver(frame);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGuiFrame() {
		super("Socom Client GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1024, 768);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem mntmSetServerUrl = new JMenuItem("Set Server URL");
		mntmSetServerUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String s = (String) JOptionPane.showInputDialog(ClientGuiFrame.this, "Set Server URL", "Server URL:", JOptionPane.PLAIN_MESSAGE, null, null,
						Status.SERVER_URL.replaceAll("servlet/", ""));
				if (s != null) {
					Status.setHost(s);
				}
			}
		});
		mnSettings.add(mntmSetServerUrl);
		
		JMenuItem mntmCookies = new JMenuItem("Cookies");
		mntmCookies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new CookieViewer();
			}
		});
		mnSettings.add(mntmCookies);

		JMenu mnHistory = new JMenu("History");
		menuBar.add(mnHistory);

		JMenuItem mntmShowUrlhistory = new JMenuItem("Show URL-History");
		mntmShowUrlhistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HistoryDisplay();
			}
		});
		mnHistory.add(mntmShowUrlhistory);

		JMenuItem mntmSaveHistoryAs = new JMenuItem("Save URL-Sequence");
		mntmSaveHistoryAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HistoryExport();
			}
		});
		mnHistory.add(mntmSaveHistoryAs);

		JMenuItem mntmExecuteUrlsequencefile = new JMenuItem("Execute URL-Sequencefile");
		mntmExecuteUrlsequencefile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new HistoryLoader();
			}
		});
		mnHistory.add(mntmExecuteUrlsequencefile);

		JMenu mnBatch = new JMenu("Batch");
		menuBar.add(mnBatch);
		
		JMenuItem mntmBatchCreate = new JMenuItem("Create and befriend from file...");
		mntmBatchCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BatchCreate();
			}
		});
		mnBatch.add(mntmBatchCreate);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JOptionPane.showMessageDialog(ClientGuiFrame.this, "Socom Client GUI\n31.10.2014\n","About", JOptionPane.PLAIN_MESSAGE);
				
			}
		});
		mnAbout.add(mntmAbout);
		contentPane = new JSplitPane();
		contentPane.setResizeWeight(1.0);
		contentPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JSplitPane statusPane = new JSplitPane();
		statusPane.setResizeWeight(0.97);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.4);

		contentPane.setLeftComponent(splitPane);
		contentPane.setRightComponent(statusPane);

		URLSpinner = new JSpinner(new SpinnerListModel(new LinkedList<String>(Arrays.asList("Fetch URLs"))));
		URLSpinner.setFont(new Font("monospace", Font.PLAIN, 12));
		((DefaultEditor) URLSpinner.getEditor()).getTextField().setEditable(false);
		statusPane.setLeftComponent(URLSpinner);

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendRequest();
			}
		});
		statusPane.setRightComponent(sendButton);

		JTabbedPane componentTabs = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(componentTabs);
		componentTabs.setSize(componentTabs.getWidth() * 2, componentTabs.getHeight());
		componentTabs.addTab("User", new UserComponentPanel());
		componentTabs.addTab("Game", new GameComponentPanel());
		componentTabs.addTab("Social", new SocialComponentPanel());
		componentTabs.addTab("Influence", new InfluenceComponentPanel());
		componentTabs.addTab("Content", new ContentComponentPanel());
		componentTabs.addTab("Achievements", new AchievementComponentPanel());
		componentTabs.addTab("Statistics", new StatisticComponentPanel());
//		componentTabs.addTab("Settings", new OptionsPanel());

		JSplitPane rightPanel = new JSplitPane();
		rightPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(rightPanel);
		LoginPanel loginPanel = LoginPanel.get();
		loginPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Usersetting", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		rightPanel.setLeftComponent(loginPanel);

		ConsolePanel consolePanel = new ConsolePanel();
		consolePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Output", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		rightPanel.setRightComponent(consolePanel);
		consolePanel.setLayout(new GridLayout(1, 0, 0, 0));
		Connection.get().addObserver(consolePanel);
	}

	protected void sendRequest() {
		if(Status.get().isPost()){
			Connection.get().sendPOSTRequest(((String) URLSpinner.getValue()).split(" ")[1], Status.get().getFile(), Status.get().getCookieParams());
		} else {
			if(Status.get().isDownload())
				Connection.get().sendDownloadRequest((String) URLSpinner.getValue());
			Connection.get().sendGETRequest((String) URLSpinner.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof List) {
			List<String> lst = (List<String>) arg1;
			URLSpinner.setModel(new SpinnerListModel(lst));
			URLSpinner.setValue(lst.get(lst.size() - 1));
			URLSpinner.setFont(new Font("monospace", Font.PLAIN, 12));
			((DefaultEditor) URLSpinner.getEditor()).getTextField().setEditable(false);
			URLSpinner.setVisible(false);
			URLSpinner.setVisible(true);
		}
	}
}