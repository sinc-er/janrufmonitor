package de.janrufmonitor.framework.monitor;

import java.util.List;

import de.janrufmonitor.framework.ICall;

/**
 *  This interface must be implemented by a monitor listener object.
 *  A monitor listener keeps track of the information provided by a monitor object.
 * 	It normally creates events for the framework to notify on information provided by the monitor,
 *  e.g., an incoming call or a call information.
 * 
 *@author     Thilo Brandt
 *@created    2003/08/24
 *@changed	  2004/10/03
 */
public interface IMonitorListener {

	/**
	 * Informs the framework of an new call connection.
	 * 
	 * @param call call incoming
	 */
	public void doCallConnect(ICall call);
	
	/**
	 * Informs the framework of a call disconnection.
	 * 
	 * @param call call to be disconnected
	 */
	public void doCallDisconnect(ICall call);
	
	/**
	 * Indicates wether listener is enabled or not.
	 * 
	 * @return true if enabled, otherwise false.
	 */
	public boolean isEnabled();
	
	/**
	 * Indicates wether listener is running or not.
	 * 
	 * @return true if running, otherwise false.
	 */
	public boolean isRunning();
	
	/**
	 * Gets the monitor objects this listener is registered to.
	 * 
	 * @return List of IMonitor objects
	 */
	public List getMonitors();
	
	/**
	 * Gets the monitor object this listener is registered to with aspecific ID.
	 * 
	 * @return IMonitor objects of the specified ID or null, if it does not exist.
	 */
	public IMonitor getMonitor(String id);
	
	/**
	 * Gets the default monitor object this listener is registered to.
	 * 
	 * @return IMonitor object of the default implementation
	 */
	public IMonitor getDefaultMonitor();
	
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
	
	/**
	 * This method starts all IMonitor objects registered at this service.
	 */
	public void start();
    
	/**
	 * This method stops all IMonitor objects registered at this service.
	 */
	public void stop();
}
