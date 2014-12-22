package de.tud.kom.socom.util;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EasyEncrypter {

	private static final EasyEncrypter instance = new EasyEncrypter();
	private static final String KEY = "Q6tq(&S51={7_71C";
	private Key secretKey;
	private Cipher cipher;
	private Logger logger = LoggerFactory.getLogger();

	private static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '0', '2', '3', '4', '5', '6',
			'7', '8', '9', '+', '-', '@', };
	private static java.util.Random r = new java.util.Random();
	private static final int PASSWORD_LENGTH = 20;

	private EasyEncrypter() {
		try {
			secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
			cipher = Cipher.getInstance("AES");
		} catch (GeneralSecurityException e) {
			logger.Error(e);
		}
	}

	public static EasyEncrypter getInstance() {
		return instance;
	}

	public byte[] encryptString(String text) {
		try {
			byte[] encrypted = text.getBytes("UTF-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(encrypted);
		} catch (Exception e) {
			logger.Error(e);
			return null;
		}
	}

	public String decryptString(byte[] input) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptBytes = cipher.doFinal(input);
			return new String(decryptBytes, "UTF-8");
		} catch (Exception e) {
			logger.Error(e);
			return null;
		}
	}

	public String getRandomPassword() {
	/* Generate a Password object with a random password. */
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}
		return sb.toString();
	}
	
	public static String getSHA(String input){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : digest)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
