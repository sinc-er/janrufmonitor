package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class ClearHandler extends Request {

	private ICall m_call;

	public ClearHandler(ICall call) {
		this.m_call = call;
	}
	
	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(ClearHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(ClearHandler.ACTION_CLEAR);
		uri.append("&");
		uri.append(ClearHandler.PARAMETER_NUMBER);
		uri.append("=");
		uri.append(this.m_call.getCaller().getPhoneNumber().getTelephoneNumber());
		uri.append("&");
		uri.append(ClearHandler.PARAMETER_MSN);
		uri.append("=");
		uri.append(this.m_call.getMSN().getMSN());
		return new URI(uri.toString());
	}

}
