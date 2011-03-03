package de.janrufmonitor.service.commons.http;

import java.net.URI;

public interface IHttpRequest extends IHttpBaseObject {

	public String getMethod();

	public URI getURI() throws Exception ;

	public IMutableHttpRequest getMutable();

	public String[] getParameterNames() throws Exception;
}
