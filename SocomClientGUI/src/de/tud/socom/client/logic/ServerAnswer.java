package de.tud.socom.client.logic;

import java.io.File;

import de.tud.socom.client.gui.LoginPanel;

public class ServerAnswer {

	public boolean isPost;
	public File file;
	public String cookieParams;
	public String url;
	public String answer;

	public ServerAnswer(String url, String answer) {
		this.url = url;
		this.answer = answer;

		checkLogin();
	}
	
	public ServerAnswer(String url, String answer, File f, String params){
		this.isPost = true;
		this.url = url;
		this.answer = answer;
		this.cookieParams = params;
	}

	private void checkLogin() {
		String loginTempl = "servlet/user/validateUser";
		String createTempl = "servlet/user/createUser";
		boolean isLogin  = url.contains(loginTempl);
		boolean isCreate = url.contains(createTempl);
		if (isLogin || isCreate) {
			if (answer.contains("uid")) {
				String urlPart = isLogin ?  url.substring(url.indexOf(loginTempl)).substring(loginTempl.length() + 1) : 
					url.substring(url.indexOf(createTempl)).substring(createTempl.length() + 1);
				
				String[] params = urlPart.split("&");
				String username = null, password = null, game = null, version = null, gamepassword = null;
				for (String param : params) {
					if (param.startsWith("username"))
						username = param.substring("username".length() + 1);
					else if (param.startsWith("gamepassword"))
						gamepassword = param.substring("gamepassword".length() + 1);
					else if (param.startsWith("password"))
						password = param.substring("password".length() + 1);
					else if (param.startsWith("game"))
						game = param.substring("game".length() + 1);
					else if (param.startsWith("version"))
						version = param.substring("version".length() + 1);
				}

				if (username == null || password == null || game == null || version == null || gamepassword == null)
					return;
				
				LoginPanel.get().setLogin(username, password, game, version, gamepassword);
				
			}
		}
	}
}
