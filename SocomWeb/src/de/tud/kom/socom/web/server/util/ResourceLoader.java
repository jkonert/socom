package de.tud.kom.socom.web.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
	
	private static final Logger logger = LoggerFactory.getLogger();
	private static Map<String,String> resources;

	public static String getResource(String key) {
		if(resources == null) {
			loadResources();
		}
		if(!resources.containsKey(key)){
			logger.Error("Resource \"" + key + "\" not found!");
		}
		return resources.get(key);
	}
	
	public static String buildPublicServerUrl(){
		String protocol = "http://";
		String host = getResource("public_url");
		String port = getResource("public_port");
		return protocol + host + ":" + port;
	}

	private static void loadResources() {
		File f = new File("WEB-INF/socomconfig.ini");
		if(!f.exists())
			f = new File("war/WEB-INF/socomconfig.ini");
		resources = new HashMap<String, String>();
		try {
			FileInputStream fis = new FileInputStream(f);
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			while((line = reader.readLine()) != null) {
				if(line.startsWith(";") || line.trim().equals("")) continue;
				String[] pair = line.split("=");
				if(pair.length != 2) {
					logger.Error("Invalid resource file. Check line " + line + " in " + f.getCanonicalPath() + ".");
					System.exit(1);
				}
				resources.put(pair[0], pair[1]);
			}
			reader.close();
			fis.close();
		} catch (FileNotFoundException e) {
			logger.Error("Resource file not found: " + f.getAbsolutePath() + ". Exit.");
			System.exit(1);
		} catch (IOException e) {
			logger.Error(e);
			System.exit(1);
		}
	}
}
