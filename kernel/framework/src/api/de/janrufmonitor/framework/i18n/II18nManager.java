package de.janrufmonitor.framework.i18n;

/**
 *  This interface must be implemented by a i18n manager object.
 *  An i18n manager object has to handle all language depend information for the framework.
 * 
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface II18nManager {

	/**
	 * Gets a language dependend information for the given namespace, parameter and identifier.
	 * 
	 * @param namespace the namespace
	 * @param parameter the parameter
	 * @param identifier the identifier
	 * @param language the language
	 * @return language dependend information
	 */
    public String getString(String namespace, String parameter, String identifier, String language);

	/**
	 * Get a list with all valid identifiers handled by this i18n manager.
	 * @return Array with all identifiers.
	 */
    public String[] getIdentifiers();

	/**
	 * Loads the data of this i18n manager.
	 */
    public void loadData();

	/**
	 * Saves the data of this i18n manager.
	 */
    public void saveData();

	/**
	 * Sets a language dependend information for the given namespace, parameter and identifier.
	 * 
	 * @param namespace the namespace
	 * @param parameter the parameter
	 * @param identifier the identifier
	 * @param language the language
	 * @param value language dependend information
	 */
    public void setString(String namespace, String parameter, String identifier, String language, String value);

	/**
	 * Removes a namespace from the i18n manager.
	 * 
	 * @param namespace the namespace to be removed.
	 */
	public void removeNamespace(String namespace);
	
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
}
