package de.janrufmonitor.service.server.http.simple.handler;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.service.commons.CompressBase64;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class RemoveCallList extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ICallManager mgr = null;
		String manager = null;
		boolean isCompression = false;
		try {
			manager = req.getParameter(RemoveCallList.PARAMETER_CALLMANAGER);
			isCompression = (req.getParameter(GetCallList.PARAMETER_COMPRESSION) != null ? req.getParameter(GetCallList.PARAMETER_COMPRESSION).equalsIgnoreCase("true"): false);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (manager==null) mgr = this.getRuntime().getCallManagerFactory().getDefaultCallManager();
		
		if (manager!=null && manager.length()>0)
			mgr = this.getRuntime().getCallManagerFactory().getCallManager(manager);
		 
		if (mgr==null || !mgr.isActive() || !mgr.isSupported(IWriteCallRepository.class)) {
			throw new HandlerException("Requested Callmanager does not exist or is not active.", 404);
		}
		
		ICallList l;
		try {
			byte[] data = this.getPostData(req).getBytes();
			if (isCompression) {
				data = CompressBase64.decompressBase64Decode(data);
			}
			l = XMLSerializer.toCallList(new String(data));
			if (l!=null) {
				this.m_logger.info("Removing call list with "+l.size()+" entries.");
				((IWriteCallRepository)mgr).removeCalls(l);
				resp.getContentStreamForWrite().close();
			} else { 
				this.m_logger.severe("Invalid call list transfered from client.");
				throw new HandlerException("Invalid call list transfered from client.", 500);
			}
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}

	}

}
