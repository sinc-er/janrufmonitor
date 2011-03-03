package de.janrufmonitor.service.commons.http;

public interface IHttpResponse extends IHttpBaseObject {

	public int getCode();
	
	public IMutableHttpResponse getMutable();

}
