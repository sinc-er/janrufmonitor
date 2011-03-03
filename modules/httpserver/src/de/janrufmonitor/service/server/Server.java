package de.janrufmonitor.service.server;

import java.util.List;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IRequester;
import de.janrufmonitor.service.commons.http.RequesterFactory;
import de.janrufmonitor.service.commons.http.simple.SimplePortListener;
import de.janrufmonitor.service.server.http.simple.ServerHandler;
import de.janrufmonitor.service.server.request.handler.AcceptHandler;
import de.janrufmonitor.service.server.request.handler.ApplicationReadyHandler;
import de.janrufmonitor.service.server.request.handler.ClearHandler;
import de.janrufmonitor.service.server.request.handler.IdentifiedCallHandler;
import de.janrufmonitor.service.server.request.handler.IdentifiedOutgoingCallHandler;
import de.janrufmonitor.service.server.request.handler.IncomingCallHandler;
import de.janrufmonitor.service.server.request.handler.OutgoingCallHandler;
import de.janrufmonitor.service.server.request.handler.RejectedHandler;
import de.janrufmonitor.service.server.request.handler.ShutdownHandler;
import de.janrufmonitor.service.server.security.SecurityManager;

public class Server extends AbstractReceiverConfigurableService {

	private String ID = "Server";
	private String NAMESPACE = "service.Server";

	private String CFG_PORT = "port";
	private String DEFAULT_PORT = "5555";

	private IRuntime m_runtime;
	
	private SimplePortListener m_httpSrv;
	
	
	private class SenderThread implements Runnable {

		private IRequester m_r;
		
		public SenderThread(IRequester r) {
			this.m_r = r;
		}

		public void run() {
			if (this.m_r!=null)
				this.m_r.request();
		}	
	}
	
	public Server() {
		super();
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return this.ID;
	}

	public IRuntime getRuntime() {
		if(this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public void shutdown() {
		super.shutdown();
		
		IHttpRequest request = new ShutdownHandler();
		this.sendRequest(getRuntime().getEventBroker().createEvent(-1), request);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			this.m_logger.severe(e.getMessage());
		}

		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_MANUALCALLACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_APPLICATION_READY));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
		
		// stop HTTP Server
		this.m_httpSrv.stop();
		this.m_httpSrv = null;
		
		ClientRegistry.getInstance().shutdown();
		
		this.m_logger.info("Server is shut down ...");
	}

	public void startup() {
		super.startup();
			
		this.m_httpSrv = new SimplePortListener(new ServerHandler(), this.getListenPort());
		
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_MANUALCALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_APPLICATION_READY));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
		
		// start HTTP Server
		this.m_httpSrv.start();
		
		this.m_logger.info("Server is started ...");
	}
	

	private int getListenPort() {
		String value = this.m_configuration.getProperty(this.CFG_PORT, this.DEFAULT_PORT);
		return (Integer.parseInt(value));
	}

	public void received(IEvent event) {
		if (event.getType() == IEventConst.EVENT_TYPE_INCOMINGCALL) {
			this.m_logger.info("Sending INCOMING_CALL event to clients.");
			IHttpRequest request = new IncomingCallHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_OUTGOINGCALL) {
			this.m_logger.info("Sending OUTGOING_CALL event to clients.");
			IHttpRequest request = new OutgoingCallHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_APPLICATION_READY) {
			this.m_logger.info("Sending APPLICATION_READY event to clients.");
			IHttpRequest request = new ApplicationReadyHandler();
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_CALLACCEPTED) {
			this.m_logger.info("Sending CALLACCEPTED event to clients.");
			IHttpRequest request = new AcceptHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		// added: 19/06/2004: Reject flag on client was not set
		if (event.getType() == IEventConst.EVENT_TYPE_CALLREJECTED) {
			this.m_logger.info("Sending CALLREJECTED event to clients.");
			IHttpRequest request = new RejectedHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_CALLCLEARED) {
			this.m_logger.info("Sending CALLCLEARED event to clients.");
			IHttpRequest request = new ClearHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_IDENTIFIED_CALL) {
			this.m_logger.info("Sending IDENTIFIED_CALL event to clients.");
			IHttpRequest request = new IdentifiedCallHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}
		if (event.getType() == IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL) {
			this.m_logger.info("Sending IDENTIFIED_OUTGOING_CALL event to clients.");
			IHttpRequest request = new IdentifiedOutgoingCallHandler((ICall)event.getData());
			this.sendRequest(event, request);
		}		
	}
	
	private synchronized void sendRequest(IEvent event, IHttpRequest request) {
		List clients = ClientRegistry.getInstance().getAllClients();
		Client c = null;
		for (int i=0;i<clients.size();i++){
			c = (Client)clients.get(i);
			
			Object call = event.getData();
			if (call!=null && call instanceof ICall) {
				IMsn msn = ((ICall)call).getMSN();
				if (!SecurityManager.getInstance().isAllowedForMSN(c.getClientIP(), msn.getMSN()) || !SecurityManager.getInstance().isAllowedForMSN(c.getClientName(), msn.getMSN())){
					this.m_logger.info("Client "+c+" is blocked for MSN "+msn.getMSN()+".");
					continue;
				}
			}
			
			if (this.isEventClient(c.getEvents(), event.getType())) {
				this.m_logger.info("Sending event #"+event+" to client "+c+".");
				try {
					if (request.getContent()!=null)
						c.setByteReceived(request.getContent().length);
					
					IRequester r = null;
					if (c.getClientName().trim().length()>0)
						r = RequesterFactory.getInstance().getRequester(c.getClientName(), c.getClientPort());
					else 
						r = RequesterFactory.getInstance().getRequester(c.getClientIP(), c.getClientPort());
					
					r.setRequest(request);
					
					Thread s = new Thread(new SenderThread(
						r
					));
					s.setName("JAM-"+SenderThread.class.getName()+"#"+i+"-Thread-(non-deamon)");
					s.start();
				} catch (Exception e) {
					this.m_logger.severe(e.getMessage());
				}
			}
		}
	}
	
	private boolean isEventClient(List events, int event) {
		if (events==null)
			return false;
			
		if (event == -1)
			return true;
		
		if (events.size()==0) {
			this.m_logger.info("Client is registered for all events.");
			return true;
		}
		
		for (int j=0;j<events.size();j++) {
			int localEvent = Integer.parseInt((String)events.get(j));
			if (localEvent==event) {
				return true;
			}
		}
		return false;
	}

}
