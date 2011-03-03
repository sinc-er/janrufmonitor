package de.janrufmonitor.service.commons.http;

import java.io.OutputStream;

public interface IHttpMutableBaseObject {
	
	public void setParameter(String name, String value);
	
	public void setContent(byte[] content) throws Exception;
	
	public OutputStream getContentStreamForWrite() throws Exception;
	
}
