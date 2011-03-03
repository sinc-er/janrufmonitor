package de.janrufmonitor.service.server.http.simple.handler;

import java.io.OutputStream;
import java.util.StringTokenizer;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class GetCaller extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ICallerManager mgr = null;
		String manager = null;
		try {
			manager = req.getParameter(GetCaller.PARAMETER_CALLERMANAGER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (manager==null) mgr = this.getRuntime().getCallerManagerFactory().getDefaultCallerManager();
		
		if (manager!=null && manager.length()>0)
			mgr = this.getRuntime().getCallerManagerFactory().getCallerManager(manager);
		 
		if (mgr==null || !mgr.isActive() || !mgr.isSupported(IIdentifyCallerRepository.class)) {
			throw new HandlerException("Requested Callermanager does not exist or is not active.", 404);
		}
		
		String number = null;
		try {
			number = req.getParameter(GetCaller.PARAMETER_NUMBER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		if (number==null || number.length()==0) {
			this.m_logger.severe("Parameter &number= was empty or not set.");
			throw new HandlerException("Parameter &number= was empty or not set.", 404);
		}
		
		IPhonenumber pn =null;
		
		StringTokenizer st = new StringTokenizer(number, ";");
		if (st.countTokens()==3) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim(),
				st.nextToken().trim(),
				st.nextToken().trim()
			);

		} 
		if (st.countTokens()==2) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim(),
				"",
				st.nextToken().trim()
			);
		} 
		if (st.countTokens()==1) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim()
			);
		} 
		
		try {
			ICaller caller = ((IIdentifyCallerRepository)mgr).getCaller(pn);
			String xml = XMLSerializer.toXML(caller, false);
			resp.setParameter("Content-Type", "text/xml");
			resp.setParameter("Content-Length", Long.toString(xml.length()));
			OutputStream ps = resp.getContentStreamForWrite();
			ps.write(xml.getBytes());
			ps.flush();
			ps.close();
		} catch (CallerNotFoundException e) {
			throw new HandlerException(e.getMessage(), 404);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}	
		
	}

}
