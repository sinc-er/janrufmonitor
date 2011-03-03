package de.janrufmonitor.service.commons.http;

public interface IMutableHttpResponse extends IHttpMutableBaseObject{

	public void setCode(int code);
	
	public IHttpResponse getImmutable();
	
	public boolean isHandled();
}
