package de.janrufmonitor.service.server.http.simple;

import java.util.HashMap;
import java.util.Map;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.Handler;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.GenericHandler;
import de.janrufmonitor.service.server.Client;
import de.janrufmonitor.service.server.ClientHistoryItem;
import de.janrufmonitor.service.server.ClientRegistry;
import de.janrufmonitor.service.server.security.SecurityManager;

public class ServerHandler extends GenericHandler {

	private static Map m_handlers;
	
	static {
		m_handlers = new HashMap();
		m_handlers.put(
			ServerHandler.ACTION_PING, "de.janrufmonitor.service.commons.http.simple.handler.Ping"
		);
		
		// put URI handlers
		m_handlers.put(
			ServerHandler.URI_PATH_CONFIGURATION, "de.janrufmonitor.service.server.http.simple.handler.Configuration"
		);
		
		m_handlers.put(
			ServerHandler.URI_PATH_CALLTO, "de.janrufmonitor.service.server.http.simple.handler.Callto"
		);
		
		// put action handlers
		m_handlers.put(
			ServerHandler.ACTION_GETCALLLIST, "de.janrufmonitor.service.server.http.simple.handler.GetCallList"
		);
		
		m_handlers.put(
			ServerHandler.ACTION_GETCALLLISTCOUNT, "de.janrufmonitor.service.server.http.simple.handler.GetCallListCount"
		);
		
		m_handlers.put(
			ServerHandler.ACTION_GETCALL, "de.janrufmonitor.service.server.http.simple.handler.GetCall"
		);		
		
		m_handlers.put(
			ServerHandler.ACTION_GETCALLERLIST, "de.janrufmonitor.service.server.http.simple.handler.GetCallerList"
		);	
		
		m_handlers.put(
			ServerHandler.ACTION_GETCALLER, "de.janrufmonitor.service.server.http.simple.handler.GetCaller"
		);	
		
		m_handlers.put(
			ServerHandler.ACTION_REMOVECALLLIST, "de.janrufmonitor.service.server.http.simple.handler.RemoveCallList"
		);			
		
		m_handlers.put(
			ServerHandler.ACTION_REMOVECALLERLIST, "de.janrufmonitor.service.server.http.simple.handler.RemoveCallerList"
		);			
		
		m_handlers.put(
			ServerHandler.ACTION_UPDATECALLERLIST, "de.janrufmonitor.service.server.http.simple.handler.UpdateCallerList"
		);	
		
		m_handlers.put(
			ServerHandler.ACTION_SETCALLERLIST, "de.janrufmonitor.service.server.http.simple.handler.SetCallerList"
		);	
		
		m_handlers.put(
			ServerHandler.ACTION_SETCALLLIST, "de.janrufmonitor.service.server.http.simple.handler.SetCallList"
		);	
		
		m_handlers.put(
			ServerHandler.ACTION_REGISTER, "de.janrufmonitor.service.server.http.simple.handler.Register"
		);		
		
		m_handlers.put(
			ServerHandler.ACTION_UNREGISTER, "de.janrufmonitor.service.server.http.simple.handler.Unregister"
		);		
	
		m_handlers.put(
			ServerHandler.ACTION_REJECT, "de.janrufmonitor.service.server.http.simple.handler.Reject"
		);	

		m_handlers.put(
			ServerHandler.ACTION_IMAGE, "de.janrufmonitor.service.server.http.simple.handler.Image"
		);		
		m_handlers.put(
			ServerHandler.ACTION_DIAL, "de.janrufmonitor.service.server.http.simple.handler.Dial"
		);	
		m_handlers.put(
			ServerHandler.ACTION_GETDIALEXTENSIONS, "de.janrufmonitor.service.server.http.simple.handler.GetDialExtensions"
		);	
	}
	
	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp)
			throws HandlerException {

		resp.setParameter("Server", "jAnrufmonitor/5.0");
		resp.setParameter("Date", Long.toString(System.currentTimeMillis()));
		
		try {
			// do security checks
			if (!this.isAllowed(req))
				throw new HandlerException("Access denied by IP address", 403);
		} catch (Exception e) {
			if (e instanceof HandlerException) throw (HandlerException)e;
			throw new HandlerException("Exception in security check.", 500);
		}
		
		String actionHandler = null;
		
		try {
			actionHandler = this.getActionHandler(req);
		} catch (Exception e) {
			throw new HandlerException("Exception in retrieving action handler", 500);
		}
		
		if (actionHandler==null) throw new HandlerException("No valid action parameter found", 404);
		
		if (actionHandler.length()>0) {
			try {
				Class handler = Thread.currentThread().getContextClassLoader().loadClass(actionHandler);
				Object o = handler.newInstance();
				if (o instanceof Handler) {
					this.setHistoryEvent(req);
					this.setTransferedBytes(req);
					this.m_logger.info("Handle request "+req.getURI());
					
					((Handler)o).handleWithException(
							req, 
							resp);
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
	}
	
	private void setHistoryEvent(IHttpRequest req) throws Exception {
		String pimclient = req.getInetAddress().getHostAddress();
		if (pimclient.length()>0) {
			Client c = ClientRegistry.getInstance().getClient(pimclient);
				
			if (c!=null) {
				String event = req.getParameter(ServerHandler.PARAMETER_ACTION);
				if (event!=null) {
					ClientHistoryItem item = new ClientHistoryItem(System.currentTimeMillis(), event);
					c.setHistoryItem(item);
					this.m_logger.info("Set new history item to client: "+c);
				}
			}
		} 
	}
	
	private void setTransferedBytes(IHttpRequest req) throws Exception {
		String pimclient = req.getInetAddress().getHostAddress();
		if (pimclient.length()>0) {
			Client c = ClientRegistry.getInstance().getClient(pimclient);
			
			if (c!=null) {
				long rcved = 0;
				try {
					rcved = Long.parseLong(req.getParameter("Content-Length"));
				} catch (NumberFormatException e) {
					this.m_logger.info(e.getMessage());
				} catch (Exception e) {
					this.m_logger.severe(e.getMessage());
				}
				c.setByteReceived(rcved);
				this.m_logger.info("Transfered bytes from client: "+rcved);
			}
		} 
	}
	
	public boolean isAllowed(IHttpRequest req) throws Exception {
		String pimclient = req.getInetAddress().getHostName();
		String pimclientip = req.getInetAddress().getHostAddress();
		
		boolean allowed = (SecurityManager.getInstance().isAllowed(pimclient) || SecurityManager.getInstance().isAllowed(pimclientip));
		this.m_logger.info("Access for Client "+pimclientip+" ("+pimclient+") " + (allowed ? "granted" : "denied") + ".");
		return allowed;
	}
	
	private synchronized String getActionHandler(IHttpRequest req) throws Exception {
		String action = req.getParameter(ServerHandler.PARAMETER_ACTION);
		action = (String) ServerHandler.m_handlers.get(action);
		if (action!=null) return action;
		
		// check URI paths
		this.m_logger.info("Requested URI: "+req.getURI().getPath());
		action = req.getURI().getPath();
		if (action!=null && action.startsWith(URI_PATH_CONFIGURATION)) return (String) ServerHandler.m_handlers.get(URI_PATH_CONFIGURATION);
		if (action!=null && action.startsWith(URI_PATH_CALLTO)) return (String) ServerHandler.m_handlers.get(URI_PATH_CALLTO);
		
		this.m_logger.warning("No action handler found for URI: "+req.getURI().getPath());
		return null;
	}
}
