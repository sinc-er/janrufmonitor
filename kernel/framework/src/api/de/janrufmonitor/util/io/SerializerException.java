package de.janrufmonitor.util.io;

/**
 *  This exception is thrown on serializing problems.
 * 
 *@author     Thilo Brandt
 *@created    2004/01/31
 */
public class SerializerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SerializerException() {
		super();
	}

	/**
	 * Constructor with message text.
	 * 
	 * @param s exception message
	 */
	public SerializerException(String s) {
		super("Serializer: " + s);
	}

}
