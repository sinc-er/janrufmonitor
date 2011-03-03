package de.janrufmonitor.service;

import java.util.List;

/**
 *  This interface must be implemented by a service object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface IService {

	/**
	 * Gets the ID of the service.
	 * 
	 * @return a valid service ID
	 */
    public String getServiceID();

	/**
	 * Checks if the service is enabled.
	 * 
	 * @return true if enabled, false if not
	 */
    public boolean isEnabled();

	/**
	 * Sets the status of a service.
	 * 
	 * @param status true if enabled, false if not.
	 */
    public void setEnabled(boolean status);

	/**
	 * Starts up the service. Can be used for initialization.
	 */
    public void startup();

	/**
	 * Shuts down the service. Can be used for cleanup.
	 */
    public void shutdown();
    
	/**
	 * Restarts the service.
	 */
	public void restart();

	/**
	 * Gets the priority of the service within the framework. 0 = hight priority, 999 = low priority.
	 * 
	 * @return priority of the service.
	 */
    public int getPriority();
    
	/**
	 * Checks if the service is running.
	 * 
	 * @return true if running, false if not
	 */
    public boolean isRunning();
    
    /**
     * Gets a list of IDs (java.lang.String) of services
     * which have to be started before this service is started.
     * 
     * @return a list of service IDs
     */
    public List getDependencyServices();

}
