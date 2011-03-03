package de.janrufmonitor.framework.event;

/**
 *  This interface must be implemented by a event broker object.
 *  A event broker handles all events fired by the framework and distributes 
 *  them to receivers.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IEventBroker {

	/**
	 * Sends an event from a specific sender.
	 * 
	 * @param sender sender of the event
	 * @param event the event
	 */
    public void send(IEventSender sender, IEvent event);

	/**
	 * Registers a new event sender.
	 * 
	 * @param sender the sender to be registered.
	 */
    public void register(IEventSender sender);

	/**
	 * Register a new event receiver for a given event.
	 * 
	 * @param receiver the receiver to be registered.
	 * @param event the event
	 */
    public void register(IEventReceiver receiver, IEvent event);

	/**
	 * Unregisters a new event sender.
	 * 
	 * @param sender the sender to be unregistered.
	 */
    public void unregister(IEventSender sender);

	/**
	 * Unregister a new event receiver for a given event.
	 * 
	 * @param receiver the receiver to be unregistered.
	 * @param event the event
	 */
    public void unregister(IEventReceiver receiver, IEvent event);

	/**
	 * Create a new event object with the given type, data and condition.
	 * 
	 * @param type type of the event
	 * @param data data of the event
	 * @param cond condition of the event.
	 * @return a new event object
	 */
    public IEvent createEvent(int type, Object data, IEventCondition cond);

	/**
	 * Create a new event object with the given type and data.
	 * 
	 * @param type type of the event
	 * @param data data of the event
	 * @return a new event object
	 */
    public IEvent createEvent(int type, Object data);

	/**
	 * Create a new event object with the given type.
	 * 
	 * @param type type of the event
	 * @return a new event object
	 */
    public IEvent createEvent(int type);

	/**
	 * Create a empty event condition object.
	 * 
	 * @return a new event object
	 */
    public IEventCondition createEventCondition();
    
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
}
