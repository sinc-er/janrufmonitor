package de.janrufmonitor.service.commons.http.handler;

public class HandlerException extends Exception {

	private static final long serialVersionUID = 1L;
	private int m_code;
	
	public HandlerException(int code) {
		this("", code);
	}
	
	public HandlerException(String text, int code) {
		super("Exception code "+code + ", "+text);
		this.m_code = code;
	}

	public int getCode() {
		return this.m_code;
	}
	
}
