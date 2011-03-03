package de.janrufmonitor.service;

import java.util.List;

/**
 *  This interface must be implemented by a service factory object, which should be
 *  used in the framework. The service factory takes care about all registered services.
 *
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface IServiceFactory {

	/**
	 * Shuts down the service factory and all its started services.
	 */
    public void shutdown();

	/**
	 * Starts up the service factory and all its registered services.
	 */
    public void startup();

	/**
	 * Gets a service for a certain ID.
	 * 
	 * @param id the service ID
	 * @return a service object
	 */
    public IService getService(String id);

	/**
	 * Gets a list with all service objects.
	 * 
	 * @return list with all services.
	 */
    public List getAllServices();
    
	/**
	 * Gets a list with all modifier service objects.
	 * 
	 * @return list with all modifier services.
	 */
    public List getModifierServices();

	/**
	 * Gets a list with all service IDs.
	 * 
	 * @return a list with all service IDs.
	 */
    public String[] getAllServiceIDs();

	/**
	 * Checks if the service with the specified ID is available.
	 * 
	 * @param id service ID
	 * @return true if service is available, false if not
	 */
    public boolean isServiceAvailable(String id);
    
    /**
     * Checks if a service is started.
     * 
     * @param id service ID
     * @return true if service is enabled, false if not
     */
	public boolean isServiceEnabled(String id);
	
	/**
	 * Starts the requested service explicitly
	 * 
	 * @param id service id
	 */
	public void startService(String id);
	
	/**
	 * Restarts the requested service explicitly
	 * 
	 * @param id service id
	 */
	public void restartService(String id);
	
	/**
	 * Stops the requested service explicitly
	 * 
	 * @param id service id
	 */
	public void stopService(String id);

}
