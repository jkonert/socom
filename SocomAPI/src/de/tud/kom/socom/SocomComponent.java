package de.tud.kom.socom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.exceptions.ParseException;

/**
 * 
 * @author rhaban
 * 
 */
public abstract class SocomComponent {

	public abstract String getUrlPattern();

	/**
	 * handleRequest method of every Component, determines which method to be
	 * called and parses its parameter, furthermore append result to the output
	 * 
	 * @param req
	 *            socomrequest coming in
	 * @return error code or 0 for success
	 * @throws Throwable
	 */
	public int handleRequest(SocomRequest req) throws Throwable {
		try {
			String methodString = req.getMethodString();
			Class<?> thisClass = this.getClass();
			Class<?> parameterTypes[] = null;
			Method method = null;
			// determine the method
			for (Method m : thisClass.getMethods()) {
				if (m.getName().equalsIgnoreCase(methodString)) {
					methodString = m.getName();
					parameterTypes = m.getParameterTypes();
					break;
				}
			}

			// exacly 1 parameter: SocomRequest
			if (parameterTypes == null || parameterTypes.length != 1)
				throw new ParseException("method not found");

			method = thisClass.getMethod(methodString, parameterTypes);
			// invoke method
			int code = (Integer) method.invoke(this, req);
			return code;
		} catch (NoSuchMethodException e) {
			throw new ParseException("method");
		} catch (IllegalAccessException e) {
			LoggerFactory.getLogger().Error(e);
		} catch (InvocationTargetException e) {
			throw e.getCause(); // throw original exception
		}
		return 1; // unknown error
	}
}
