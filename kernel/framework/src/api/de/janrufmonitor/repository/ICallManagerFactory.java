package de.janrufmonitor.repository;

import java.util.List;

/**
 *  This interface must be implemented by a call manager factory object.
 *  The call manager factory takes care about the concurrent existence of
 *  call managers. Its task is to maintain and handle all registered call
 *  manager instances.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallManagerFactory {

	/**
	 * Gets the call manager object for a certain ID
	 * 
	 * @param id ID of the call manager implementation
	 * @return a call manager instance
	 */
    public ICallManager getCallManager(String id);
    
	/**
	 * Gets the default call manager object. The default call
	 * manager is normally represented by the "DefaultCallManager" string.
	 * 
	 * @return the default call manager instance
	 */
	public ICallManager getDefaultCallManager();

	/**
	 * Gets a list with all call manager objects.
	 * 
	 * @return list with all call manager objects.
	 */
    public List getAllCallManagers();

	/**
	 * Gets all call mananger objects implementing a certain type.
	 * 
	 * @param type type of call manager instance
	 * @return list with call mananger instances.
	 */
    public List getTypedCallManagers(Class type);
    
	/**
	 * Gets all IDs of registered call manager objects.
	 * 
	 * @return list with all registered IDs
	 */
    public String[] getAllCallManagerIDs();
    
	/**
	 * Gets all call mananger objects implementing a certain type.
	 * 
	 * @param type type of call manager instance
	 * @return list with call mananger instances.
	 */
    public String[] getCallManagerIDs(Class type);
    
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
