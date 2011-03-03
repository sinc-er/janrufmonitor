package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.commons.http.jakarta.Request;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class IdentifiedOutgoingCallHandler extends Request {

	private ICall m_call;

	public IdentifiedOutgoingCallHandler(ICall call) {
		this.m_call = call;
		this.setMethod(IdentifiedOutgoingCallHandler.METHOD_POST);
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(IdentifiedOutgoingCallHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(IdentifiedOutgoingCallHandler.ACTION_IDENTIFIEDOUTGOINGCALL);
		return new URI(uri.toString());
	}

	public byte[] getContent() throws Exception {
		return XMLSerializer.toXML(this.m_call, false).getBytes();
	}

}
