package de.janrufmonitor.service.commons.http;

import java.net.URI;

public interface IRequester {

	public IMutableHttpRequest createGetRequest(URI uri);
	
	public IMutableHttpRequest createPostRequest(URI uri, byte[] content) throws Exception;

	public String getServer();
	
	public int getPort();
	
	public void setRequest(IMutableHttpRequest r);
	
	public void setRequest(IHttpRequest r);

	public IHttpResponse request();
}
