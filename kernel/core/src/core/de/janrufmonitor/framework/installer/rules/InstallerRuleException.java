package de.janrufmonitor.framework.installer.rules;

public class InstallerRuleException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String msgId;
	
	public InstallerRuleException(String msgId, String msg) {
		super(msg);
		this.msgId = msgId;
	}
	
	public InstallerRuleException(String msgId, String msg, Throwable t) {
		super(msg, t);
		this.msgId = msgId;
	}
	
	public String getMessageID() {
		return this.msgId;
	}

}
