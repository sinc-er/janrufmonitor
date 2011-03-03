package de.janrufmonitor.service.client.request.handler;

import java.net.URI;
import java.util.List;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class RegisterGetHandler extends Request {

	private int m_port;
	private List m_events;

	public RegisterGetHandler(String client, String ip, int port, List events) {
		this.m_port = port;
		this.m_events = events;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(RegisterGetHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(RegisterGetHandler.ACTION_REGISTER);
		uri.append("&");
		uri.append(RegisterGetHandler.PARAMETER_CLIENT_PORT);
		uri.append("=");
		uri.append(this.m_port);
		uri.append("&");
		uri.append(RegisterGetHandler.PARAMETER_CLIENT_EVENTS);
		uri.append("=");	
		for (int i=0;i<this.m_events.size();i++) {
			uri.append((String)this.m_events.get(i) + ",");
		}
		return new URI(uri.toString());
	}

}
