package de.janrufmonitor.framework.event;

/**
 *  This interface must be implemented by a event sender object.
 *  An event sender can be implemented by any component which wants to fire
 *  events in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IEventSender {

	/**
	 * Gets the ID of the sender 
	 * 
	 * @return sender IDs
	 */
    public String getSenderID();

	/**
	 * Gets the priority of this sender.
	 * 
	 * @return the priority
	 */
    public int getPriority();

}
