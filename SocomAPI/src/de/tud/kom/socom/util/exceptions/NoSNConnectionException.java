package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class NoSNConnectionException extends SocomException {

	private static final long serialVersionUID = -2022458267671152004L;

	@Override
	public int getErrorCode() {
		return ErrorCode.NO_SN_CONNECTION.ordinal();
	}

}
