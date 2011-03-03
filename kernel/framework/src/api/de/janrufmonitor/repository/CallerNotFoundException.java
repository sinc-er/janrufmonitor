package de.janrufmonitor.repository;

/**
 *  This class can be used to inform the framework the a certain
 *  caller object was not found in the persistency.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/12
 */
public class CallerNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CallerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallerNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new instance.
	 */
	public CallerNotFoundException() {
		super();
	}

	/**
	 * Creates a new instance with a message text.
	 * @param s message used as exception message.
	 */
	public CallerNotFoundException(String s) {
		super(s);
	}

}
