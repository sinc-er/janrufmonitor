package de.janrufmonitor.service.client.http.simple;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.Handler;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.GenericHandler;

public class ClientHandler extends GenericHandler {
	 
	private static Map m_handlers;

	static {
		m_handlers = new HashMap();
		m_handlers.put(
			ClientHandler.ACTION_PING, "de.janrufmonitor.service.commons.http.simple.handler.Ping"
		);
		
		m_handlers.put(
			ClientHandler.ACTION_SHUTDOWN, "de.janrufmonitor.service.client.http.simple.handler.Shutdown"
		);
		
		m_handlers.put(
			ClientHandler.ACTION_REJECTED, "de.janrufmonitor.service.client.http.simple.handler.Rejected"
		);		
		
		m_handlers.put(
			ClientHandler.ACTION_ACCEPT, "de.janrufmonitor.service.client.http.simple.handler.Accept"
		);		
		
		m_handlers.put(
			ClientHandler.ACTION_APPLICATION_READY, "de.janrufmonitor.service.client.http.simple.handler.ApplicationReady"
		);		
		
		m_handlers.put(
			ClientHandler.ACTION_CLEAR, "de.janrufmonitor.service.client.http.simple.handler.Clear"
		);
		
		m_handlers.put(
			ClientHandler.ACTION_INCOMINGCALL, "de.janrufmonitor.service.client.http.simple.handler.IncomingCall"
		);		
		
		m_handlers.put(
			ClientHandler.ACTION_OUTGOINGCALL, "de.janrufmonitor.service.client.http.simple.handler.OutgoingCall"
		);	
		
		m_handlers.put(
			ClientHandler.ACTION_IDENTIFIEDCALL, "de.janrufmonitor.service.client.http.simple.handler.IdentifiedCall"
		);		
		
		m_handlers.put(
			ClientHandler.ACTION_IDENTIFIEDOUTGOINGCALL, "de.janrufmonitor.service.client.http.simple.handler.IdentifiedOutgoingCall"
		);	
	}
	
	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp)
			throws HandlerException {

		this.m_logger.info("Processing incoming server request ...");
		
		try {
			resp.setParameter("Server", "jAnrufmonitor-Client-"+InetAddress.getLocalHost().getHostName()+"/2.0");
		} catch (UnknownHostException e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		resp.setParameter("Date", Long.toString(System.currentTimeMillis()));

		String actionHandler = null;
		
		try {
			actionHandler = this.getActionHandler(req);
		} catch (Exception e) {
			throw new HandlerException("IOException in retrieving action handler", 500);
		}
		
		if (actionHandler==null) throw new HandlerException("No valid action parameter found", 404);
		
		if (actionHandler.length()>0) {
			try {
				Class handler = Thread.currentThread().getContextClassLoader().loadClass(actionHandler);
				Object o = handler.newInstance();
				if (o instanceof Handler) {
					((Handler)o).handleWithException(req, resp);
				}
			} catch (ClassNotFoundException ex) {
				throw new HandlerException("Class not found: "+actionHandler, 500);
			} catch (InstantiationException e) {
				throw new HandlerException("Cannot instantiate class: "+actionHandler, 500);
			} catch (IllegalAccessException e) {
				throw new HandlerException("Illegal access for class: "+actionHandler, 500);
			} catch (HandlerException e) {
				throw e;
			} catch (Exception e) {
				throw new HandlerException(e.getMessage(), 500);
			}
		}
		super.handleWithException(req,resp);
		this.m_logger.info("Finishing incoming server request ...");
	}
	
	private synchronized String getActionHandler(IHttpRequest req) throws Exception {
		String action = req.getParameter(ClientHandler.PARAMETER_ACTION);
		if (action!=null) {
			return (String) ClientHandler.m_handlers.get(action);
		}
		return null;
	}

}
