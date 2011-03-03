package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.service.commons.http.jakarta.Request;

public class CallGetHandler extends Request {

	private String m_uuid;
	private String m_cm;

	public CallGetHandler(String uuid, String callmanagerid) {
		this.m_uuid = uuid;
		this.m_cm = callmanagerid;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(CallGetHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(CallGetHandler.ACTION_GETCALL);
		uri.append("&");
		uri.append(CallGetHandler.PARAMETER_UUID);
		uri.append("=");
		uri.append(this.m_uuid);
		if (this.m_cm!=null && this.m_cm.length()>0) {
			uri.append("&");
			uri.append(CallGetHandler.PARAMETER_CALLMANAGER);
			uri.append("=");
			uri.append(this.m_cm);
		}
		return new URI(uri.toString());
	}
}
