package de.janrufmonitor.repository.imexporter;

public class VcfParserException extends Exception {

	private static final long serialVersionUID = 2969526232347817581L;

	public VcfParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public VcfParserException(String message) {
		super(message);
	}

	public VcfParserException(Throwable cause) {
		super(cause);
	}

}
