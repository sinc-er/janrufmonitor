package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class GetDialExtensions extends Request {

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(GetDialExtensions.PARAMETER_ACTION);
		uri.append("=");
		uri.append(GetDialExtensions.ACTION_GETDIALEXTENSIONS);
		return new URI(uri.toString());
	}
}
