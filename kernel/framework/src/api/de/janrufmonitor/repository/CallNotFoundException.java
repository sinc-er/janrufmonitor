package de.janrufmonitor.repository;

/**
 *  This class can be used to inform the framework the a certain
 *  call object was not found in the persistency.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/12
 */
public class CallNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance.
	 */
	public CallNotFoundException() {
		super();
	}

	/**
	 * Creates a new instance with a message text.
	 * @param s message used as exception message.
	 */
	public CallNotFoundException(String s) {
		super(s);
	}

}
