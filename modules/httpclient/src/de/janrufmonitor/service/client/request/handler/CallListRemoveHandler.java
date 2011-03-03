package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.service.commons.CompressBase64;
import de.janrufmonitor.service.commons.http.jakarta.Request;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class CallListRemoveHandler extends Request {
	
	private ICallList m_l;
	private String m_cm;
	
	public CallListRemoveHandler(ICallList l, String callmanager) {
		super();
		this.m_l = l;
		this.m_cm = callmanager;
		this.setMethod(CallListRemoveHandler.METHOD_POST);
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(CallListRemoveHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(CallListRemoveHandler.ACTION_REMOVECALLLIST);

		if (this.m_cm!=null && this.m_cm.length()>0) {
			uri.append("&");
			uri.append(CallListRemoveHandler.PARAMETER_CALLMANAGER);
			uri.append("=");
			uri.append(this.m_cm);
		}
		// add compression request
		uri.append("&");
		uri.append(CallListGetHandler.PARAMETER_COMPRESSION);
		uri.append("=true");
		return new URI(uri.toString());
	}

	public byte[] getContent() throws Exception {
		return CompressBase64.compressBase64Encode(XMLSerializer.toXML(this.m_l, false)).getBytes();
	}
}
