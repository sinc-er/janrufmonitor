package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;


public class ShutdownHandler extends Request {

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(ShutdownHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(ShutdownHandler.ACTION_SHUTDOWN);
		return new URI(uri.toString());
	}

}
