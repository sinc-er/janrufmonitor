package de.janrufmonitor.service.identification;

import java.util.Properties;
import java.util.logging.Level;

import de.janrufmonitor.framework.*;
import de.janrufmonitor.framework.event.*;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;

public class QuickIdentify extends AbstractReceiverConfigurableService implements IEventSender {

	private String ID = "QuickIdentify";
	private String NAMESPACE = "service.QuickIdentify";

	private IRuntime m_runtime;
	private IEventBroker m_eventBroker;

	public QuickIdentify() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public String getSenderID() {
		return this.getID();
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return this.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public void setConfiguration(Properties configuration) {
		this.m_eventBroker = this.getRuntime().getEventBroker();
		super.setConfiguration(configuration);	
	}

	public void shutdown() {
		super.shutdown();
		// unregister as EventSender
		this.m_eventBroker.unregister(this);
		// unregister as EventReceiver
		this.m_eventBroker.unregister(this, this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		this.m_eventBroker.unregister(this, this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
	}

	public void startup() {
		super.startup();
		// register as EventSender
		this.m_eventBroker.register(this);
		// register as EventReceiver
		this.m_eventBroker.register(this, this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		this.m_eventBroker.register(this, this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
	}

	public synchronized void enabledReceived(IEvent event) {
		this.m_logger.entering(QuickIdentify.class.getName(), "enabledReceived");
		if (event.getType() == IEventConst.EVENT_TYPE_INCOMINGCALL || event.getType() == IEventConst.EVENT_TYPE_OUTGOINGCALL) {
			Object o = event.getData();
			if (o==null || !(o instanceof ICall)) {
				this.m_logger.severe("Invalid object sent with EVENT_TYPE_INCOMINGCALL event.");
				this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
				return;
			}
			
			try {
				ICall incomingCall = (ICall)((ICall)o).clone();
				this.m_logger.info("New incoming call received for identification: "+incomingCall.toString());
				IMsn msn = incomingCall.getMSN();
				ICip cip = incomingCall.getCIP();
				IPhonenumber pn = null;
				if (incomingCall!=null && incomingCall.getCaller()!=null) {
					pn = incomingCall.getCaller().getPhoneNumber();
				}
				if (msn==null || cip==null) {
					this.m_logger.severe("Invalid MSN or CIP sent with incoming call.");
					this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
					return;
				}
				
				if (this.getRuntime().getRuleEngine().validate(this.getID(), msn, cip, pn)){
	
					this.m_logger.info("Identifiing MSN and CIP for incoming call.");
					// set MSN and CIP information to call
					msn.setAdditional(this.getRuntime().getMsnManager().getMsnLabel(msn));
					cip.setAdditional(this.getRuntime().getCipManager().getCipLabel(cip, ""));
	
					if (incomingCall.getCaller()==null) {
						this.m_logger.severe("Invalid Caller object sent with incoming call.");
						this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
						return;
					}
					
					//IPhonenumber pn = incomingCall.getCaller().getPhoneNumber();
					if (pn==null) {
						this.m_logger.severe("Invalid Phonenumber object sent with incoming call.");
						this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
						return;
					}
	
					// handle CLIR calls - do not need to proceed the whole detection part
					if (pn.isClired()) {
						this.handleClirCall(incomingCall);
						this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
						return;
					}

					ICaller identifiedCaller = Identifier.identify(getRuntime(), pn);
	
					if (identifiedCaller==null) {
						this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
						return;
					}
	
					incomingCall.setCaller(identifiedCaller);
	
					IEvent resultEvent = null;
					switch (event.getType()) {
						case IEventConst.EVENT_TYPE_INCOMINGCALL: 
							resultEvent = this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL, incomingCall);
							break;
						case IEventConst.EVENT_TYPE_OUTGOINGCALL:
							resultEvent = this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL, incomingCall);
							break;
						default: 
							resultEvent = this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL, incomingCall);
					}
				
					this.m_eventBroker.send(this, resultEvent);
					if (this.m_logger.isLoggable(Level.INFO))
						this.m_logger.info("Call successfully identified and forwarded.");  
				}
			} catch (CloneNotSupportedException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		this.m_logger.exiting(QuickIdentify.class.getName(), "enabledReceived");
	}

	private void handleClirCall(ICall clirCall) {
		this.m_logger.info("detected CLIR call.");
		IEvent event = this.m_eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL, clirCall);
		this.m_eventBroker.send(this, event);   
	}
}
