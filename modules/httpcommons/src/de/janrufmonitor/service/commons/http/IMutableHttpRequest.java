package de.janrufmonitor.service.commons.http;

import java.net.URI;

public interface IMutableHttpRequest extends IHttpMutableBaseObject{

	public void setMethod(String method);
	
	public void setURI(URI uri);
	
	public IHttpRequest getImmutable();

}
