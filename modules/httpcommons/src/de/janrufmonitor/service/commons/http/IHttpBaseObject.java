package de.janrufmonitor.service.commons.http;

import java.io.InputStream;
import java.net.InetAddress;

public interface IHttpBaseObject {

	public String getParameter(String name) throws Exception;

	public byte[] getContent() throws Exception;
	
	public InputStream getContentStreamForRead() throws Exception;
	
	public InetAddress getInetAddress() throws Exception;

}
