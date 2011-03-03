package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class OutgoingCallHandler extends Request {

	private ICall m_call;

	public OutgoingCallHandler(ICall call) {
		this.m_call = call;
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(OutgoingCallHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(OutgoingCallHandler.ACTION_OUTGOINGCALL);
		uri.append("&");
		uri.append(OutgoingCallHandler.PARAMETER_NUMBER);
		uri.append("=");
		IPhonenumber pn = this.m_call.getCaller().getPhoneNumber();
		uri.append(pn.getTelephoneNumber());
		uri.append("&");
		uri.append(OutgoingCallHandler.PARAMETER_MSN);
		uri.append("=");
		uri.append(this.m_call.getMSN().getMSN());
		uri.append("&");
		uri.append(OutgoingCallHandler.PARAMETER_CIP);
		uri.append("=");
		uri.append(this.m_call.getCIP().getCIP());
		uri.append("&");
		uri.append(OutgoingCallHandler.PARAMETER_DATE);
		uri.append("=");
		uri.append(this.m_call.getDate().getTime());
		return new URI(uri.toString());
	}
}
