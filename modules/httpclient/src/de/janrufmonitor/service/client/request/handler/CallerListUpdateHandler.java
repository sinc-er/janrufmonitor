package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.service.commons.http.jakarta.Request;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class CallerListUpdateHandler extends Request {

	private ICallerList m_l;
	private String m_cm;

	public CallerListUpdateHandler(ICallerList l, String callermanager) {
		super();
		this.m_l = l;
		this.m_cm = callermanager;
		this.setMethod(CallerListUpdateHandler.METHOD_POST);
	}

	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(CallerListUpdateHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(CallerListUpdateHandler.ACTION_UPDATECALLERLIST);
		if (this.m_cm!=null && this.m_cm.length()>0) {
			uri.append("&");
			uri.append(CallerListUpdateHandler.PARAMETER_CALLERMANAGER);
			uri.append("=");
			uri.append(this.m_cm);
		}
		return new URI(uri.toString());
	}

	public byte[] getContent() throws Exception {
		return XMLSerializer.toXML(this.m_l, false, true).getBytes();
	}

}
