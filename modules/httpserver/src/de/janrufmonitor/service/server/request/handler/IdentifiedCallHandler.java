package de.janrufmonitor.service.server.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.commons.http.jakarta.Request;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class IdentifiedCallHandler extends Request {

	private ICall m_call;

	public IdentifiedCallHandler(ICall call) {
		this.m_call = call;
		this.setMethod(IdentifiedCallHandler.METHOD_POST);
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(IdentifiedCallHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(IdentifiedCallHandler.ACTION_IDENTIFIEDCALL);
		return new URI(uri.toString());
	}

	public byte[] getContent() throws Exception {
		return XMLSerializer.toXML(this.m_call, false).getBytes();
	}

}
