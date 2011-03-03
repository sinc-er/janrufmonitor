package de.janrufmonitor.service.commons.http.handler;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import simple.http.ProtocolHandler;

public interface Handler extends ProtocolHandler {
	
	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException;
	
}
