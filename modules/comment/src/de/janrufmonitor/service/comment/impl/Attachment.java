package de.janrufmonitor.service.comment.impl;

import java.io.InputStream;

import de.janrufmonitor.service.comment.api.IAttachment;

public class Attachment implements IAttachment {

	private static final long serialVersionUID = -4428379702976957232L;
	
	String m_name;
	InputStream m_is;

	public Attachment(String name) {
		this.m_name = name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getName() {
		return this.m_name;
	}

	public void setContent(InputStream is) {
		this.m_is = is;
	}

	public InputStream getConent() {
		return this.m_is;
	}

}
