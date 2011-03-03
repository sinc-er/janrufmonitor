package de.janrufmonitor.service.server.http.simple.handler;

import java.io.OutputStream;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.filter.UUIDFilter;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class GetCall extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ICallManager mgr = null;
		String manager = null;
		try {
			manager = req.getParameter(GetCall.PARAMETER_CALLMANAGER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (manager==null) mgr = this.getRuntime().getCallManagerFactory().getDefaultCallManager();
		
		if (manager!=null && manager.length()>0)
			mgr = this.getRuntime().getCallManagerFactory().getCallManager(manager);
		 
		if (mgr==null || !mgr.isActive() || !mgr.isSupported(IReadCallRepository.class)) {
			throw new HandlerException("Requested Callmanager does not exist or is not active.", 404);
		}
		
		String uuid = null;
		try {
			uuid = req.getParameter(GetCall.PARAMETER_UUID);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (uuid==null || uuid.length()==0) {
			this.m_logger.severe("Parameter &uuid= was empty or not set.");
			throw new HandlerException("Parameter &uuid= was empty or not set.", 404);
		}

		try {
			ICallList callList = ((IReadCallRepository)mgr).getCalls(new UUIDFilter(new String[] {uuid}));
			if (callList.size()>0) {
				String xml = XMLSerializer.toXML(callList.get(0), false);
				resp.setParameter("Content-Type", "text/xml");
				resp.setParameter("Content-Length", Long.toString(xml.length()));
				OutputStream ps = resp.getContentStreamForWrite();
				ps.write(xml.getBytes());
				ps.flush();
				ps.close();
			}
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

}
