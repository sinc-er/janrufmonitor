package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class ApplicationReadyHandler extends Request {

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(ApplicationReadyHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(ApplicationReadyHandler.ACTION_APPLICATION_READY);
		return new URI(uri.toString());
	}

}
