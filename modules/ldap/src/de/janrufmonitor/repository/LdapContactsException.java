package de.janrufmonitor.repository;

public class LdapContactsException extends Exception {

	private static final long serialVersionUID = -2138092648164731340L;

	public LdapContactsException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public LdapContactsException(String msg) {
		super(msg);
	}
}
