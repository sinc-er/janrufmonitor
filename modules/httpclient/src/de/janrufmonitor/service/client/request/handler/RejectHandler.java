package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class RejectHandler extends Request {

	ICall m_c;

	public RejectHandler(ICall c) {
		this.m_c = c;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(CallListSetHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(CallListSetHandler.ACTION_REJECT);
		uri.append("&");
		uri.append(RejectHandler.PARAMETER_NUMBER);
		uri.append("=");
		uri.append(this.m_c.getCaller().getPhoneNumber().getTelephoneNumber());
		uri.append("&");
		uri.append(RejectHandler.PARAMETER_MSN);
		uri.append("=");
		uri.append(this.m_c.getMSN().getMSN());
		return new URI(uri.toString());
	}

}
