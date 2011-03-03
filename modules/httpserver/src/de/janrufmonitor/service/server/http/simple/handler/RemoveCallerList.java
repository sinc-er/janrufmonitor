package de.janrufmonitor.service.server.http.simple.handler;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class RemoveCallerList extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ICallerManager mgr = null;
		String manager = null;
		try {
			manager = req.getParameter(RemoveCallerList.PARAMETER_CALLERMANAGER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (manager==null) mgr = this.getRuntime().getCallerManagerFactory().getDefaultCallerManager();
		
		if (manager!=null && manager.length()>0)
			mgr = this.getRuntime().getCallerManagerFactory().getCallerManager(manager);
		 
		if (mgr==null || !mgr.isActive() || !mgr.isSupported(IWriteCallerRepository.class)) {
			throw new HandlerException("Requested Callermanager does not exist or is not active.", 404);
		}
		
		ICallerList l;
		try {
			l = XMLSerializer.toCallerList(this.getPostData(req));
			if (l!=null) {
				this.m_logger.info("Removing caller list with "+l.size()+" entries.");
				((IWriteCallerRepository)mgr).removeCaller(l);
				resp.getContentStreamForWrite().close();
			} else { 
				this.m_logger.severe("Invalid caller list transfered from client.");
				throw new HandlerException("Invalid caller list transfered from client.", 500);
			}
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}

	}

}
