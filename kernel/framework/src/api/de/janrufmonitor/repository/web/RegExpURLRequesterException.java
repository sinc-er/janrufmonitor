package de.janrufmonitor.repository.web;

import java.util.List;

public class RegExpURLRequesterException extends Exception {

	private static final long serialVersionUID = -2095252938636316411L;

	List m_failures;
	
	public RegExpURLRequesterException() {
		super();
	}

	public RegExpURLRequesterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RegExpURLRequesterException(String arg0) {
		super(arg0);
	}
	
	public RegExpURLRequesterException(String arg0, List failures) {
		super(arg0);
		this.m_failures = failures;
	}

	public RegExpURLRequesterException(Throwable arg0) {
		super(arg0);
	}
	
	public List getFailures() {
		return this.m_failures;
	}
	
}
