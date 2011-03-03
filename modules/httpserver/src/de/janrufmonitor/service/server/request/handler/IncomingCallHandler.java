package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class IncomingCallHandler extends Request {

	private ICall m_call;

	public IncomingCallHandler(ICall call) {
		this.m_call = call;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(IncomingCallHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(IncomingCallHandler.ACTION_INCOMINGCALL);
		uri.append("&");
		uri.append(IncomingCallHandler.PARAMETER_NUMBER);
		uri.append("=");
		IPhonenumber pn = this.m_call.getCaller().getPhoneNumber();
		uri.append(pn.getTelephoneNumber());
		uri.append("&");
		uri.append(IncomingCallHandler.PARAMETER_MSN);
		uri.append("=");
		uri.append(this.m_call.getMSN().getMSN());
		uri.append("&");
		uri.append(IncomingCallHandler.PARAMETER_CIP);
		uri.append("=");
		uri.append(this.m_call.getCIP().getCIP());
		uri.append("&");
		uri.append(IncomingCallHandler.PARAMETER_DATE);
		uri.append("=");
		uri.append(this.m_call.getDate().getTime());
		return new URI(uri.toString());
	}
}
