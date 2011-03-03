package de.janrufmonitor.framework.configuration;

import java.util.Properties;

import de.janrufmonitor.framework.manager.IRepositoryManager;

/**
 *  This interface must be implemented by a Configuration Manager object, which should be
 *  used in the framework. A configuration manager has to handle all configuration settings for
 *  the framework. They have to be kept consistent and valid.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IConfigManager extends IRepositoryManager {

	/**
	 * Sets a certain property with a given namespace, name and a value.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins.
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @param value the value of the property
	 */
    public void setProperty(String namespace, String name, String value);

	/**
	 * Sets a certain property with a given namespace, name, metadata and a value.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins. The metadat can be used by a module / plugin for
	 * handling different information for one property e.g., value = 555 and type = integer.
	 * <pre>
	 * Example: namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = value
	 * 			value = 99999
	 * 
	 * 			namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = type
	 * 			value = integer
	 * </pre>
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @param metadata the metadata of the property
	 * @param value the value of the property
	 */
    public void setProperty(String namespace, String name, String metadata, String value);

	/**
	 * Sets a certain property with a given namespace, name, metadata and a value.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins. The metadat can be used by a module / plugin for
	 * handling different information for one property e.g., value = 555 and type = integer.
	 * <pre>
	 * Example: namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = value
	 * 			value = 99999
	 * 
	 * 			namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = type
	 * 			value = integer
	 * </pre>
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @param metadata the metadata of the property
	 * @param value the value of the property
	 * @param overwrite if the property already exists it can be overwritten or left by its old value.
	 */
    public void setProperty(String namespace, String name, String metadata, String value, boolean overwrite);

    
	/**
	 * Gets a certain property with a given namespace and name.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins.
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @return the value of the property
	 */
    public String getProperty(String namespace, String name);

	/**
	 * Gets a certain property with a given namespace, name and metadata.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins. The metadat can be used by a module / plugin for
	 * handling different information for one property e.g., value = 555 and type = integer.
	 * <pre>
	 * Example: namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = value
	 * 			value = 99999
	 * 
	 * 			namespace = de.development.myPlugin
	 * 			name = counter
	 * 			metadata = type
	 * 			value = integer
	 * </pre>
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @param metadata the metadata of the property
	 * @return the value of the property
	 */
    public String getProperty(String namespace, String name, String metadata);


	/**
	 * Removes a certain property with a given namespace and name.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins.
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 */
    public void removeProperty(String namespace, String name);

	/**
	 * Removes a certain property with a given namespace, name and metadata.
	 * The namespace is neccessary to avoid name collisions for properties from
	 * different modules / plugins. The metadat can be used by a module / plugin for
	 * handling different information for one property e.g., value = 555 and type = integer.
	 * 
	 * @param namespace the namespace of the property
	 * @param name the name of the property
	 * @param metadata the metadata of the property
	 */
    public void removeProperty(String namespace, String name, String metadata);

	/**
	 * Sets a list of properties for a given namespace.
	 * 
	 * @param namespace the namespace to set the list for
	 * @param props list of properties
	 */
    public void setProperties(String namespace, Properties props);

	/**
	 * Gets a list of all properties for a given namespace.
	 * 
	 * @param namespace the namespace to retrieve the list for
	 * @return
	 */
    public Properties getProperties(String namespace);

	/**
	 * Gets a list of selectable properties for a given namespace.
	 * 
	 * @param namespace the namespace to retrieve the list for
	 * @param selected a selected set of properties could be returned, e.g. for security reasons	
	 * @return
	 */
    public Properties getProperties(String namespace, boolean selected);
    
	/**
	 * Gets a list of all properties for a given namespace and names.
	 * This method is neccessary to get all metadata information for
	 * a certain property.
	 * 
	 * @param namespace the namespace to retrieve the list for
	 * @param name the name to retrieve the list for
	 * @return
	 */
    public Properties getProperties(String namespace, String name);

	/**
	 * Gets a list of selectable properties for a given namespace and names.
	 * This method is neccessary to get all metadata information for
	 * a certain property. 
	 * 
	 * @param namespace the namespace to retrieve the list for
	 * @param name the name to retrieve the list for
	 * @param selected a selected set of properties could be returned, e.g. for security reasons
	 * @return
	 */
    public Properties getProperties(String namespace, String name, boolean selected);

    
	/**
	 * Removes all properties for the given namespace.
	 * 
	 * @param namespace the namespace to be removed
	 */
    public void removeProperties(String namespace);

	/**
	 * Sets the source object to store the properties in. This
	 * could be a DB connection, a file or what ever you can imagine 
	 * for persistency.
	 * 
	 * @param obj the source object to store the properties
	 */
    public void setConfigurationSource(Object obj);

	/**
	 * Gets the source object for the properties
	 * 
	 * @return the source object
	 */
    public Object getConfigurationSource();

	/**
	 * Loads the configuration from the source object.
	 */
    public void loadConfiguration();

	/**
	 * Saves the configuration in the source object.
	 */
    public void saveConfiguration();

	/**
	 * Gets all namespaces supported by the configuration manager.
	 * 
	 * @return Array of all namespaces.
	 */
    public String[] getConfigurationNamespaces();
}
