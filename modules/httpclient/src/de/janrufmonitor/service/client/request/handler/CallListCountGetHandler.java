package de.janrufmonitor.service.client.request.handler;

import java.net.URI;

import de.janrufmonitor.repository.filter.AbstractFilterSerializer;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.commons.http.jakarta.Request;

public class CallListCountGetHandler extends Request {

	private class URLFilterManager extends AbstractFilterSerializer {		
		private IRuntime m_runtime;
		
		protected IRuntime getRuntime() {
			if (this.m_runtime==null)
				this.m_runtime = PIMRuntime.getInstance();
			return this.m_runtime;
		}
		
	}
	
	private String m_filter;
	private String m_cm;

	public CallListCountGetHandler(String callmanager) {
		this.m_filter = "";
		this.m_cm = callmanager;
	}
	
	public CallListCountGetHandler(String callmanager, IFilter[] filters) {
		this.m_filter = new URLFilterManager().getFiltersToString(filters);
		this.m_cm = callmanager;
	}
	
	public URI getURI() throws Exception {
		StringBuffer uri = new StringBuffer();
		uri.append("/?");
		uri.append(CallListCountGetHandler.PARAMETER_ACTION);
		uri.append("=");
		uri.append(CallListCountGetHandler.ACTION_GETCALLLISTCOUNT);

		if (this.m_filter!=null && this.m_filter.length()>0) {
			uri.append("&");
			uri.append(CallListCountGetHandler.PARAMETER_FILTER);
			uri.append("=");
			uri.append(this.m_filter);
		}
		
		if (this.m_cm!=null && this.m_cm.length()>0) {
			uri.append("&");
			uri.append(CallListCountGetHandler.PARAMETER_CALLMANAGER);
			uri.append("=");
			uri.append(this.m_cm);
		}
		
		return new URI(uri.toString());
	}

}
