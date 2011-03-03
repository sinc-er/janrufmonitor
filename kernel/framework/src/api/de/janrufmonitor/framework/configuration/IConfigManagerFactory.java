package de.janrufmonitor.framework.configuration;

/**
 *  This interface must be implemented by a Configuration Manager Factory object, which should be
 *  used in the framework. A configuration manager factory has to handle all configuration managers for
 *  the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IConfigManagerFactory {

	/**
	 * Gets the current configuration manager instance.
	 * 
	 * @return a valid configuration manager
	 */
    public IConfigManager getConfigManager();

	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
	
}
