package de.janrufmonitor.framework.manager;

/**
 *  This interface must be implemented by a Manager object, which should be
 *  used in the framework. A manager can handle different information used in
 *  the framework, e.g., configuration, callers or call lists
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IManager {

	/**
	 * Gets the ID of a manager implementation
	 * 
	 * @return Id of this Manager
	 */
    public String getManagerID();
    
	/**
	 * Sets the ID of a manager implementation
	 */
    public void setManagerID(String id);

	/**
	 * Gets priority of a manager, which is needed to be handled
	 * by the framework.
	 * 
	 * @return Id of this Manager
	 */
    public int getPriority();
    
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
	
	/**
	 * This method is called on restart time by the runtime object.
	 */
	public void restart();	

}
