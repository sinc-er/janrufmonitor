package de.janrufmonitor.framework.i18n;

/**
 *  This interface must be implemented by a i18n manager factory object.
 *  An i18n manager factory object has to handle all i18n managers for the framework.
 * 
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface II18nManagerFactory {

	/**
	 * Gets the current i18n manager object.
	 * 
	 * @return a valid i18n manager
	 */
    public II18nManager getI18nManager();
    
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
}
