package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class RejectedHandler extends Request {

	private ICall m_call;

	public RejectedHandler(ICall call) {
		this.m_call = call;
	}
	
	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(RejectedHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(RejectedHandler.ACTION_REJECTED);
		uri.append("&");
		uri.append(RejectedHandler.PARAMETER_NUMBER);
		uri.append("=");
		uri.append(this.m_call.getCaller().getPhoneNumber().getTelephoneNumber());
		uri.append("&");
		uri.append(RejectedHandler.PARAMETER_MSN);
		uri.append("=");
		uri.append(this.m_call.getMSN().getMSN());
		return new URI(uri.toString());
	}

}