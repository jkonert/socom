package de.tud.kom.socom.util;

public class NumberParser {

	public static double parseDouble(String var) throws NumberFormatException {
//		try {
			var = var.replaceFirst(",", ".");
			return Double.parseDouble(var);
//		} catch (NumberFormatException e) {
//			if (var.contains(",")) {
//				throw new NumberFormatException();
//			} else {
//				return Double.parseDouble(var);
//			}
//		}
	}
}
