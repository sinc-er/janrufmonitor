package de.janrufmonitor.repository;

import java.util.List;

/**
 *  This interface must be implemented by a caller manager factory object.
 *  The caller manager factory takes care about the concurrent existence of
 *  caller managers. Its task is to maintain and handle all registered caller
 *  manager instances.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallerManagerFactory {

	/**
	 * Gets the caller manager object for a certain ID
	 * 
	 * @param id ID of the caller manager implementation
	 * @return a caller manager instance
	 */
    public ICallerManager getCallerManager(String id);
    
    /**
     * Gets the default caller manager object. The default caller 
     * manager is normally represented by the "DefaultCallerManager" string.
     * 
     * @return the default caller manager instance
     */
    public ICallerManager getDefaultCallerManager();

	/**
	 * Gets a list with all caller manager objects.
	 * 
	 * @return list with all caller manager objects.
	 */
    public List getAllCallerManagers();

	/**
	 * Gets all caller mananger objects implementing a certain type.
	 * 
	 * @param type type of caller manager instance (interface definition from de.janrufmonitor.repository.types.*)
	 * @return list with caller mananger instances.
	 */
    public List getTypedCallerManagers(Class type);
    
	/**
	 * Gets all IDs of registered caller manager objects.
	 * 
	 * @return list with all registered IDs
	 */
    public String[] getAllCallerManagerIDs();
    
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
	
	/**
	 * Checks wether a manager is available or not.
	 *
	 * @param id id of the manager to check
	 * @return
	 */
	public boolean isManagerAvailable(String id);

}
