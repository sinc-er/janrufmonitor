package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class GetImageHandler extends Request {

	private IPhonenumber m_pn;
	private String m_cm;

	public GetImageHandler(IPhonenumber pn, String callermanagerid) {
		this.m_pn = pn;
		this.m_cm = callermanagerid;
	}
	
	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(GetImageHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(GetImageHandler.ACTION_IMAGE);
		uri.append("&");
		uri.append(GetImageHandler.PARAMETER_NUMBER);
		uri.append("=");
		if (this.m_pn.getIntAreaCode().length()==0 || 
			this.m_pn.getAreaCode().length()==0 ||
			this.m_pn.getCallNumber().length()==0)
				uri.append(this.m_pn.getTelephoneNumber());
		else {
			uri.append(this.m_pn.getIntAreaCode());
			uri.append(";");
			uri.append(this.m_pn.getAreaCode());
			uri.append(";");
			uri.append(this.m_pn.getCallNumber());
		}

		if (this.m_cm!=null && this.m_cm.length()>0) {
			uri.append("&");
			uri.append(GetImageHandler.PARAMETER_CALLERMANAGER);
			uri.append("=");
			uri.append(this.m_cm);
		}
		return new URI(uri.toString());
	}
}
