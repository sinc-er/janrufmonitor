package de.janrufmonitor.repository.zip;

/**
 * This class is an implementation of a ZIP archive
 * exception which should be thrown on general problems
 * with the ZIP implementation.
 * 
 *@author     Thilo Brandt
 *@created    2005/05/25
 */
public class ZipArchiveException extends Exception {

	private static final long serialVersionUID = 1L;

	public ZipArchiveException() {
		super();
	}

	/**
	 * Constructor offering the possibility to specify a
	 * message which can be get with getMessage() calls
	 * 
	 * @param s message string 
	 */
	public ZipArchiveException(String s) {
		super(s);
	}

}
