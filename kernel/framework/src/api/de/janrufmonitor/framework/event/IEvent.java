package de.janrufmonitor.framework.event;

/**
 *  This interface must be implemented by a event object. Events are fired and catched
 * 	by the framework and can be handled by EventReceivers and EventSender.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IEvent {

	/**
	 * Gets the type of the event.
	 * 
	 * @return type of the event
	 */
    public int getType();

	/**
	 * Gets the condition of the event.
	 * 
	 * @return condition of the event.
	 */
    public IEventCondition getConditions();

	/**
	 * Gets the event data, e.g., a caller or a call
	 * 
	 * @return event data
	 */
    public Object getData();

	/**
	 * Sets the type of the event.
	 * 
	 * @param type type of the event
	 */
    public void setType(int type);

	/**
	 * Sets a condition for this event.
	 * 
	 * @param cond condition for this event
	 */
    public void setConditions(IEventCondition cond);

	/**
	 * Sets the data for this event, e.g., a caller or a call object.
	 * 
	 * @param obj data of the event
	 */
    public void setData(Object obj);

}
