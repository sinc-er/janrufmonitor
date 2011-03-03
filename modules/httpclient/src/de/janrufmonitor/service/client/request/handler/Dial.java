package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class Dial extends Request {

	private String m_pn;
	private String m_ext;

	public Dial(String pn, String ext) {
		this.m_pn = pn;
		this.m_ext = ext;
	}
	
	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(Dial.PARAMETER_ACTION);
		uri.append("=");
		uri.append(Dial.ACTION_DIAL);
		uri.append("&");
		uri.append(Dial.PARAMETER_NUMBER);
		uri.append("=");
		uri.append(this.m_pn);
		uri.append("&");
		uri.append(Dial.PARAMETER_EXTENSION);
		uri.append("=");
		uri.append(this.m_ext);
		return new URI(uri.toString());
	}
}
