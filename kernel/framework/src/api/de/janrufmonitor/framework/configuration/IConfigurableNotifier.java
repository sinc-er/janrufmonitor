package de.janrufmonitor.framework.configuration;

/**
 *  This interface must be implemented by a configurable notifier object. This component
 *  has to inform all registered configurable obejcts due to changes in their configuration.
 *  Therefor this implementation must interact with a configuration manager.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IConfigurableNotifier {

	/**
	 * Registers a new configurable object.
	 * 
	 * @param c the object to be registered
	 */
    public void register(IConfigurable c);

	/**
	 * Unregisters a new configurable object.
	 * 
	 * @param c the object to be unregistered
	 */
    public void unregister(IConfigurable c);

	/**
	 * Notifies the configurable object identified by the given configurableId.
	 * 
	 * @param configurableId the configurable ID to be notified.
	 */
    public void notifyConfigurable(String configurableId);

	/**
	 * Notifies all configurables which have been registered for the
	 * given namespace.
	 * 
	 * @param namespace the namespace to be notified.
	 */
    public void notifyByNamespace(String namespace);

	/**
	 * Notifies all configurables with their current configuration.
	 */
    public void notifyAllConfigurables();

	/**
	 * Get all configurable IDs which are registered.
	 * 
	 * @return Array of configurable IDs
	 */
    public String[] getAllConfigurableIDs();

	/**
	 * Get all namespaces which are registered.
	 * 
	 * @return Array of namespaces
	 */
    public String[] getAllConfigurableNamespaces();
	
	/**
	 * Get the number of configurables which are registered.
	 * 
	 * @return number of configurables
	 */
    public int getConfigurableCount();
    
	/**
	 * Get the namespace of the specified configurable.
	 * 
	 * @return namespace of configurable
	 */
    public String getConfigurableNamespace(String id);
    
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
	
	/**
	 * Checks wether a configurable with the specified ID is registered or not.
	 * 
	 * @param id ID to check
	 * @return true is it is registered, false if not.
	 */
	public boolean isRegistered(String id);
	
}
