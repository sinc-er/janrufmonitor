package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class UnregisterGetHandler extends Request {

	private int m_port;

	public UnregisterGetHandler(String client, String ip, int port) {
		this.m_port = port;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(UnregisterGetHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(UnregisterGetHandler.ACTION_UNREGISTER);
		uri.append("&");
		uri.append(UnregisterGetHandler.PARAMETER_CLIENT_PORT);
		uri.append("=");
		uri.append(this.m_port);
		return new URI(uri.toString());
	}
}
