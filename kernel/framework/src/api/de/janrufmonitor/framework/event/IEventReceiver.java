package de.janrufmonitor.framework.event;

/**
 *  This interface must be implemented by a event receiver object.
 *  An event receiver gets informed if a certain event is fired by the framework. It could be
 *  implemented by any service, repository manager or component.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IEventReceiver {

	/**
	 * Receives the event for which this event receiver is registered for.
	 * 
	 * @param event the event to receive
	 */
    public void received(IEvent event);

	/**
	 * Gets the ID of the receiver
	 * 
	 * @return receivers ID
	 */
    public String getReceiverID();

	/**
	 * Gets the priority for which the framework handles this
	 * event receiver. 0 is the hightest priority, 99 the lowest.
	 * 
	 * @return the priority
	 */
    public int getPriority();

}
