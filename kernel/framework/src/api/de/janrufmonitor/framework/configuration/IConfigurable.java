package de.janrufmonitor.framework.configuration;

import java.util.Properties;

/**
 *  This interface must be implemented by a object which has to be notified
 *  by setting or changing the configuration in the framework. This interface 
 *  should be implemented e.g. by services, repository managers and components which
 *  interact with the configuration manager.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IConfigurable {

	/**
	 * Gets the namespace of the configurable object.
	 * 
	 * @return a valid and unique namespace
	 */
    public String getNamespace();
    
    /**
     * Gets the ID of the configurable object.
     * 
     * @return a valid and unique ID
     */
    public String getConfigurableID();

	/**
	 * Sets the configuration in the configurable object.
	 * This method is called by the configuration notifier
	 * to interact with its registered components.
	 * 
	 * @param configuration the configuration for the given namespace
	 */
    public void setConfiguration(Properties configuration);

}
