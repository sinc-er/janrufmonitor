package de.janrufmonitor.repository;

public class GoogleContactsLoginException extends GoogleContactsException {

	private static final long serialVersionUID = 4410638638407867124L;

	public GoogleContactsLoginException() {
		super();
	}

	public GoogleContactsLoginException(String arg0) {
		super("Login to google contacts account failed: "+arg0);
	}

	public GoogleContactsLoginException(Throwable arg0) {
		super(arg0);
	}

	public GoogleContactsLoginException(String arg0, Throwable arg1) {
		super("Login to google contacts account failed: "+arg0, arg1);
	}

}
