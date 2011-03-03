package de.janrufmonitor.service;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventReceiver;

/**
 *  This abstract class can be used as base class for a new service implementation which
 *  is supporting configuration and eventing.
 *
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public abstract class AbstractReceiverConfigurableService extends AbstractConfigurableService implements IEventReceiver {

	/**
	 * Default constructor.
	 */
	public AbstractReceiverConfigurableService() {
		super();
	}

	public void received(IEvent event) {
		if (this.isEnabled()) {
			this.enabledReceived(event);
		} else {
			this.m_logger.info("Service is not enabled.");
		}
	}
	
	/**
	 * This method is called by the framework if an event is fired by the
	 * event broker and the received event passes the service enabled check.
	 * This method can be overriden by any implementation.
	 * 
	 * @param event evnt which was fired by the event broker
	 */
	public void enabledReceived(IEvent event) {
		if (event.getType() == IEventConst.EVENT_TYPE_IDENTIFIED_CALL || event.getType() == IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL) {
			this.receivedIdentifiedCall(event);
			return;
		}
		this.receivedOtherEventCall(event);
	}
	
	/**
	 * This method is called by the framework if an event of type
	 * <code>EVENT_TYPE_IDENTIFIED_CALL</code> was fired by the event broker and the service
	 * passes the enabled check.
	 * 
	 * @param event the identified call event
	 */
	public void receivedIdentifiedCall(IEvent event) {
		ICall aCall = (ICall)event.getData();
		if (aCall!=null) {
			if (getRuntime().getRuleEngine().validate(this.getID(), aCall.getMSN(), aCall.getCIP(), aCall.getCaller().getPhoneNumber())) {
				this.receivedValidRule(aCall);
			} else {
				this.m_logger.info("No rule assigned to execute this service for call: "+aCall);
			}
		} 
	}
	
	/**
	 * This method is called by the framework if an event of any type except
	 * <code>EVENT_TYPE_IDENTIFIED_CALL</code> was fired by the event broker and the service
	 * passes the enabled check.
	 * 
	 * @param event the event
	 */
	public void receivedOtherEventCall(IEvent event) {
		// TODO: can be overriden by concrete class
	}

	/**
	 * This method is called by the framework if the service passes
	 * the valid rule check.
	 * 
	 * @param aCall the incoming call
	 */
	public void receivedValidRule(ICall aCall) {
		// TODO: can be overriden by concrete class
	}

	public String getReceiverID() {
		return this.getID();
	}
	
	public abstract String getNamespace();

	/**
	 * Gets the ID of the new service. The ID is taken for registration at 
	 * service factory and at the configurable notifier.
	 * 
	 * @return service ID
	 */
	public abstract String getID();

}
